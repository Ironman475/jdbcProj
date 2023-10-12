package org.example;

import java.sql.*;
import java.util.Scanner;

public class App
{
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/mydb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "1234";
    static Connection connection;
    public static void main( String[] args )
    {
        Scanner sc = new Scanner(System.in);
        try {
            try {
                connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
                initDB();

                while (true) {
                    Scanner scan = new Scanner(System.in);
                    System.out.println("1: add flats");
                    System.out.println("2: view flats");
                    System.out.println("3: view flats with parameters");
                    System.out.print("-> ");

                    String s = scan.nextLine();

                    switch (s) {
                        case "1":
                            addFlats(sc);
                            break;
                        case "2":
                            viewFlats();
                            break;
                        case "3":
                            viewFlatsParam(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                if (connection != null) connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void initDB() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS Flats");
            st.execute("CREATE TABLE Flats (id INT NOT NULL " +
                    "AUTO_INCREMENT PRIMARY KEY, district VARCHAR(20) " +
                    "NOT NULL, square INT " +
                    "NOT NULL, room_number INT " +
                    "NOT NULL, price INT NOT NULL)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addFlats(Scanner scanner) throws SQLException {
        Scanner cons = new Scanner(System.in);
        System.out.println("Enter district : ");
        String district = cons.nextLine();
        System.out.println("Enter square : ");
        int square = scanner.nextInt();
        System.out.println("Enter number of rooms : ");
        int room_num = scanner.nextInt();
        System.out.println("Enter price : ");
        int price = scanner.nextInt();

        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO Flats(district, square, room_number, price)" +
                "values(?, ?, ?, ?)")) {
            ps.setString(1, district);
            ps.setInt(2, square);
            ps.setInt(3, room_num);
            ps.setInt(4, price);
            ps.executeUpdate();
        }
    }
    private static void view(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();

            for (int i = 1; i <= md.getColumnCount(); i++)
                System.out.print(md.getColumnName(i) + "\t\t");
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(rs.getString(i) + "\t\t\t");
                }
                System.out.println();
            }
        }
    }
    private static void viewFlats() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM Flats ")) {
            view(ps);
        }
    }

    private static void viewFlatsParam(Scanner sc) throws SQLException {
        System.out.println("Chose the parameter : ");
        System.out.println("1 -> square ");
        System.out.println("2 -> number of rooms ");
        System.out.println("3 -> price ");
        int s = sc.nextInt();
        String choose;
        PreparedStatement ps;
        switch (s) {
            case 1:
                choose = Choosing();
                ps = connection.prepareStatement("SELECT * FROM Flats WHERE square "+ choose +" ?");
                System.out.println("Enter the value : ");
                ps.setInt(1, sc.nextInt());
                break;
            case 2:
                choose = Choosing();
                ps = connection.prepareStatement("SELECT * FROM Flats WHERE room_number "+ choose +" ?");
                System.out.println("Enter the value : ");
                ps.setInt(1, sc.nextInt());
                break;
            case 3:
                choose = Choosing();
                ps = connection.prepareStatement("SELECT * FROM Flats WHERE price "+ choose +" ?");
                System.out.println("Enter the value : ");
                ps.setInt(1, sc.nextInt());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + s);
        }
        try {
            view(ps);
        } finally {
            ps.close();
        }
    }
    private static String Choosing(){
        Scanner sc = new Scanner(System.in);
        System.out.println("More (1) Less (2)? ");
        int choose = sc.nextInt();
        switch (choose){
            case 1 :
                return ">";
            case 2 :
                return "<";
            default :
                System.out.println("Choose correct number");
                Choosing();
        }
        return null;
    }


}
