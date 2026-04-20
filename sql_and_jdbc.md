Yes. Your current project uses **CSV files**. To upgrade it professionally, convert storage to **MySQL + JDBC** and connect it with your Java Swing frontend.

I’ll give you a **complete beginner-friendly roadmap**:

# ✅ What You Need to Do

You asked for:

✔ MySQL code
✔ 5 tables
✔ JDBC connection
✔ Show database data in frontend GUI
✔ Full integration guide

---

# 🎯 Final Database Design (5 Tables)

We will create these tables:

1. **movies** → store movie details
2. **customers** → customer info
3. **rentals** → rental transactions
4. **users** → admin login
5. **payments** → payment records

---

# 🛠 STEP 1: Install Required Tools

## Install:

### 1. MySQL Server

Install MySQL Community Server.

### 2. MySQL Workbench

GUI for database.

### 3. JDBC Driver

Download:

**mysql-connector-j.jar**

---

# 🛠 STEP 2: MySQL Database Code

Run this in MySQL Workbench:

```sql
CREATE DATABASE movie_rental_db;
USE movie_rental_db;

-- 1. Movies Table
CREATE TABLE movies (
    movie_id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(100),
    genre VARCHAR(50),
    director VARCHAR(100),
    release_year INT,
    available BOOLEAN
);

-- 2. Customers Table
CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100)
);

-- 3. Rentals Table
CREATE TABLE rentals (
    txn_id VARCHAR(20) PRIMARY KEY,
    customer_id INT,
    movie_id VARCHAR(10),
    rent_date DATE,
    return_date DATE,
    status VARCHAR(20),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (movie_id) REFERENCES movies(movie_id)
);

-- 4. Users Table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50),
    role VARCHAR(20)
);

-- 5. Payments Table
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    txn_id VARCHAR(20),
    amount DECIMAL(10,2),
    payment_date DATE,
    payment_mode VARCHAR(30),
    FOREIGN KEY (txn_id) REFERENCES rentals(txn_id)
);
```

---

# 🎬 Insert Sample Data

```sql
INSERT INTO movies VALUES
('M001','The Dark Knight','Action','Christopher Nolan',2008,true),
('M002','Inception','Sci-Fi','Christopher Nolan',2010,true),
('M003','Interstellar','Sci-Fi','Christopher Nolan',2014,true),
('M004','The Matrix','Sci-Fi','Wachowski',1999,true),
('M005','Avengers Endgame','Action','Russo Brothers',2019,true);

INSERT INTO users(username,password,role)
VALUES ('admin','1234','ADMIN');
```

---

# 🛠 STEP 3: Add JDBC Driver in Java Project

If using VS Code / IntelliJ / Eclipse:

Add:

```text
mysql-connector-j-8.x.x.jar
```

to project libraries.

---

# 🛠 STEP 4: Create DB Connection File

Create file:

## 📄 DBConnection.java

```java
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    static String url = "jdbc:mysql://localhost:3306/movie_rental_db";
    static String user = "root";
    static String password = "your_password";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch(Exception e) {
            System.out.println("Database Connection Failed");
            e.printStackTrace();
            return null;
        }
    }
}
```

---

# 🛠 STEP 5: Load Movies from MySQL Instead of CSV

Replace your `loadData()` method.

## 📄 MovieRentalSystem.java

```java
static void loadData() {
    movies.clear();

    try {
        Connection con = DBConnection.getConnection();
        String sql = "SELECT * FROM movies";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while(rs.next()) {
            Movie m = new Movie(
                rs.getString("movie_id"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getString("director"),
                rs.getInt("release_year"),
                rs.getBoolean("available")
            );

            movies.put(m.getId(), m);
        }

        con.close();

    } catch(Exception e) {
        e.printStackTrace();
    }
}
```

---

# 🛠 STEP 6: Rent Movie → Save in Database

Replace rent logic:

```java
static void rentMovieDB(String customerName, String movieId) {
    try {
        Connection con = DBConnection.getConnection();

        // Insert customer
        PreparedStatement ps1 = con.prepareStatement(
            "INSERT INTO customers(customer_name) VALUES(?)",
            Statement.RETURN_GENERATED_KEYS
        );

        ps1.setString(1, customerName);
        ps1.executeUpdate();

        ResultSet keys = ps1.getGeneratedKeys();
        keys.next();
        int customerId = keys.getInt(1);

        String txnId = "T" + System.currentTimeMillis();

        PreparedStatement ps2 = con.prepareStatement(
            "INSERT INTO rentals VALUES(?,?,?,?,?,?)"
        );

        ps2.setString(1, txnId);
        ps2.setInt(2, customerId);
        ps2.setString(3, movieId);
        ps2.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
        ps2.setDate(5, null);
        ps2.setString(6, "RENTED");

        ps2.executeUpdate();

        PreparedStatement ps3 = con.prepareStatement(
            "UPDATE movies SET available=false WHERE movie_id=?"
        );

        ps3.setString(1, movieId);
        ps3.executeUpdate();

        con.close();

        System.out.println("Movie Rented Successfully");

    } catch(Exception e) {
        e.printStackTrace();
    }
}
```

---

# 🛠 STEP 7: Return Movie

```java
static void returnMovieDB(String txnId) {
    try {
        Connection con = DBConnection.getConnection();

        PreparedStatement ps1 = con.prepareStatement(
            "UPDATE rentals SET return_date=?, status='RETURNED' WHERE txn_id=?"
        );

        ps1.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
        ps1.setString(2, txnId);
        ps1.executeUpdate();

        PreparedStatement ps2 = con.prepareStatement(
            "UPDATE movies SET available=true WHERE movie_id=(SELECT movie_id FROM rentals WHERE txn_id=?)"
        );

        ps2.setString(1, txnId);
        ps2.executeUpdate();

        con.close();

    } catch(Exception e) {
        e.printStackTrace();
    }
}
```

---

# 🛠 STEP 8: Show Tables in Frontend GUI

You asked: **Display 5 tables in frontend**

Best way = Add tabs.

Use:

```java
JTabbedPane tabs = new JTabbedPane();

tabs.add("Movies", createMoviesPanel());
tabs.add("Customers", createCustomersPanel());
tabs.add("Rentals", createRentalsPanel());
tabs.add("Users", createUsersPanel());
tabs.add("Payments", createPaymentsPanel());

add(tabs);
```

---

# 🛠 Example: Movies Table Panel

```java
private JPanel createMoviesPanel() {

    JPanel panel = new JPanel(new BorderLayout());

    String[] cols = {"ID","Title","Genre","Director","Year","Available"};
    DefaultTableModel model = new DefaultTableModel(cols,0);
    JTable table = new JTable(model);

    try {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM movies");

        while(rs.next()) {
            model.addRow(new Object[]{
                rs.getString("movie_id"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getString("director"),
                rs.getInt("release_year"),
                rs.getBoolean("available")
            });
        }

    } catch(Exception e) {
        e.printStackTrace();
    }

    panel.add(new JScrollPane(table));
    return panel;
}
```

---

# 🎯 Final Frontend Layout

Your GUI will have:

### Sidebar:

✔ Rent Movie
✔ Return Movie
✔ Refresh

### Main Area Tabs:

✔ Movies
✔ Customers
✔ Rentals
✔ Users
✔ Payments

---

# 📁 Final Project Files

```text
MovieRentalGUI.java
MovieRentalSystem.java
DBConnection.java
mysql-connector.jar
```

---

# 🔥 Important Change

Remove:

```java
movies.csv
rentals.csv
```

No need now.

---

# 🎯 JDBC Compile Command

```bash
javac -cp ".;mysql-connector-j-8.0.xx.jar" *.java
java -cp ".;mysql-connector-j-8.0.xx.jar" MovieRentalGUI
```

(Windows)

---

# 🚀 If You Want Professional Features Next

I can also give you:

## ⭐ Full Advanced Version

✔ Login Page
✔ Dashboard Cards
✔ Search Filter
✔ Charts
✔ Reports PDF
✔ Dark UI
✔ Admin Panel
✔ Fine Calculation
✔ Customer Registration Form
✔ NetBeans Ready Project
✔ Full Source Code

---

# 👉 If you want, I can build your **complete Movie Rental System with MySQL + Swing GUI + Login + 5 Database Tables + Attractive UI** ready to run.

Just say:

**"Give full project code"**
