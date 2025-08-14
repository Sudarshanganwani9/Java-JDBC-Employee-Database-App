// EmployeeApp.java
// Single-file Java JDBC app: creates DB/table (if needed) and supports CRUD via console menu.
// Update the DB credentials in CONFIG below before running.

import java.sql.*;
import java.util.*;

public class EmployeeApp {

    // ===== CONFIG (edit these for your setup) =====
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";            // MySQL default
    private static final String DB_NAME = "employee_db";
    private static final String DB_USER = "root";            // <-- your MySQL username
    private static final String DB_PASS = "";                // <-- your MySQL password
    // ==============================================

    // Base URL (no DB) for creating database if missing
    private static final String BASE_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // URL with database selected
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // ----- Simple Employee DTO -----
    static class Employee {
        int id;
        String name;
        String department;
        double salary;

        Employee(int id, String name, String department, double salary) {
            this.id = id; this.name = name; this.department = department; this.salary = salary;
        }

        Employee(String name, String department, double salary) {
            this.name = name; this.department = department; this.salary = salary;
        }
    }

    // ----- Bootstrapping: create DB and table if they don't exist -----
    private static void ensureDatabaseAndTable() throws SQLException {
        // 1) Create database if not exists
        try (Connection conn = DriverManager.getConnection(BASE_URL, DB_USER, DB_PASS);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        }
        // 2) Create table if not exists
        String createTable = "CREATE TABLE IF NOT EXISTS employees (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(100) NOT NULL," +
                "department VARCHAR(50)," +
                "salary DOUBLE" +
                ")";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement st = conn.createStatement()) {
            st.executeUpdate(createTable);
        }
    }

    // ----- CRUD helpers -----
    private static void addEmployee(Employee e) throws SQLException {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.name);
            ps.setString(2, e.department);
            ps.setDouble(3, e.salary);
            ps.executeUpdate();
        }
    }

    private static List<Employee> listEmployees() throws SQLException {
        String sql = "SELECT id, name, department, salary FROM employees ORDER BY id";
        List<Employee> result = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getDouble("salary")
                ));
            }
        }
        return result;
    }

    private static boolean updateEmployee(int id, String name, String dept, double salary) throws SQLException {
        String sql = "UPDATE employees SET name = ?, department = ?, salary = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, dept);
            ps.setDouble(3, salary);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static boolean deleteEmployee(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ----- Console UI -----
    private static void menuLoop() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Employee Database App (MySQL + JDBC) ===");
            System.out.println("1) Add Employee");
            System.out.println("2) View All Employees");
            System.out.println("3) Update Employee");
            System.out.println("4) Delete Employee");
            System.out.println("5) Exit");
            System.out.print("Choose option: ");

            String choiceStr = sc.nextLine().trim();
            int choice;
            try { choice = Integer.parseInt(choiceStr); }
            catch (NumberFormatException ex) { System.out.println("Enter a valid number."); continue; }

            try {
                switch (choice) {
                    case 1: {
                        System.out.print("Name: ");
                        String name = sc.nextLine().trim();
                        System.out.print("Department: ");
                        String dept = sc.nextLine().trim();
                        System.out.print("Salary: ");
                        double sal = readDouble(sc);
                        addEmployee(new Employee(name, dept, sal));
                        System.out.println("✅ Employee added.");
                        break;
                    }
                    case 2: {
                        List<Employee> all = listEmployees();
                        if (all.isEmpty()) {
                            System.out.println("(no employees yet)");
                        } else {
                            System.out.printf("%-5s %-22s %-16s %-10s%n", "ID", "Name", "Department", "Salary");
                            System.out.println("-----------------------------------------------------------");
                            for (Employee e : all) {
                                System.out.printf("%-5d %-22s %-16s %-10.2f%n",
                                        e.id, e.name, e.department == null ? "" : e.department, e.salary);
                            }
                        }
                        break;
                    }
                    case 3: {
                        System.out.print("Employee ID to update: ");
                        int id = readInt(sc);
                        System.out.print("New Name: ");
                        String name = sc.nextLine().trim();
                        System.out.print("New Department: ");
                        String dept = sc.nextLine().trim();
                        System.out.print("New Salary: ");
                        double sal = readDouble(sc);
                        boolean ok = updateEmployee(id, name, dept, sal);
                        System.out.println(ok ? "✅ Updated." : "❌ No employee with that ID.");
                        break;
                    }
                    case 4: {
                        System.out.print("Employee ID to delete: ");
                        int id = readInt(sc);
                        boolean ok = deleteEmployee(id);
                        System.out.println(ok ? "✅ Deleted." : "❌ No employee with that ID.");
                        break;
                    }
                    case 5:
                        System.out.println("Bye!");
                        return;
                    default:
                        System.out.println("Choose 1–5.");
                }
            } catch (SQLException ex) {
                System.out.println("DB Error: " + ex.getMessage());
            }
        }
    }

    // Robust numeric input helpers
    private static int readInt(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException ex) { System.out.print("Enter a valid integer: "); }
        }
    }
    private static double readDouble(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim();
            try { return Double.parseDouble(s); }
            catch (NumberFormatException ex) { System.out.print("Enter a valid number: "); }
        }
    }

    public static void main(String[] args) {
        try {
            // Optional for older JDBC: Class.forName("com.mysql.cj.jdbc.Driver");
            ensureDatabaseAndTable();
            menuLoop();
        } catch (SQLException ex) {
            System.out.println("Startup error: " + ex.getMessage());
            System.out.println("Tip: Ensure MySQL is running and credentials in CONFIG are correct.");
        }
    }
               }
