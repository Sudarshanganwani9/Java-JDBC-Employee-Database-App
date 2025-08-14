# Java-JDBC-Employee-Database-App

# Java JDBC – Employee Database App

## 📌 Overview
This is a simple **Java console-based Employee Database App** that uses **JDBC** to connect to a MySQL database and perform **CRUD** operations (Create, Read, Update, Delete).

The application allows users to:
- Add a new employee
- View all employees
- Update employee details
- Delete an employee

---

## 🛠️ Technologies Used
- **Java 17+**
- **MySQL / MariaDB**
- **JDBC (Java Database Connectivity)**
- **MySQL Connector/J (JDBC Driver)**

---

## 📂 Project Structure

EmployeeDatabaseApp/
│
├── lib/
│   └── mysql-connector-j-8.4.0.jar      # MySQL JDBC Driver
│
├── src/
│   └── EmployeeApp.java                 # Java code with CRUD logic
│


---

## ⚙️ Setup & Installation

### 1️⃣ Install MySQL
Ensure MySQL is installed and running on your system.

### 2️⃣ Create Database & Table
Run the following SQL commands in MySQL:
```sql
CREATE DATABASE employee_db;

USE employee_db;

CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    salary DOUBLE
);

## Compile and Run

javac EmployeeApp.java
java -cp .:mysql-connector-j-8.4.0.jar EmployeeApp
