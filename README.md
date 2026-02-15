# 💰 Expense Tracker

A web-based expense tracking application built with Java technologies to help users manage their personal finances efficiently.

## 🎯 About

Expense Tracker is a comprehensive web application designed for managing daily expenses. Users can register, log in, and track their spending across different categories with an intuitive dashboard that provides visual insights into their financial habits.

## ✨ Features

- 🔐 **User Authentication**: Secure registration and login system with password hashing
- ➕ **Add Expenses**: Record expenses with amount, category, date, and description
- 📊 **Dashboard**: View total expenses and category-wise breakdown
- 📝 **Expense Management**: View, edit, and delete expense records
- 🔍 **Filter & Sort**: Filter expenses by date range and category
- 📈 **Visual Reports**: Interactive charts showing spending patterns
- 🎨 **Responsive UI**: Clean, modern interface using Bootstrap 5

## 🛠 Technologies Used

### Backend
- **Core Java** - Business logic and object-oriented programming
- **Advanced Java (JDBC)** - Database connectivity and operations
- **Servlets** - HTTP request handling and routing
- **JSP (JavaServer Pages)** - Dynamic web page generation

### Frontend
- **HTML5 & CSS3** - Structure and styling
- **Bootstrap 5** - Responsive UI framework
- **JavaScript** - Client-side validation and interactivity
- **Chart.js** - Data visualization

### Database
- **MySQL** - Relational database management

### Server
- **Apache Tomcat 9/10** - Servlet container and web server

### Build Tool
- **Maven** - Dependency management and build automation

## 📦 Prerequisites

Before running this project, ensure you have:

- **JDK 8 or higher** ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Apache Maven** ([Download](https://maven.apache.org/download.cgi))
- **MySQL Server** ([Download](https://dev.mysql.com/downloads/mysql/))
- **Apache Tomcat 9 or 10** ([Download](https://tomcat.apache.org/download-90.cgi))
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code with Java extensions)

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Ajinkyawagh4846/Expense_Tracker.git
cd Expense_Tracker
```

### 2. Setup MySQL Database

Open MySQL Workbench or MySQL Command Line and run:

```sql
-- Create database
CREATE DATABASE expense_tracker;
USE expense_tracker;

-- Create users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create categories table
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- Create expenses table
CREATE TABLE expenses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    category_id INT NOT NULL,
    description VARCHAR(255),
    expense_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Insert default categories
INSERT INTO categories (name) VALUES 
('Food'), ('Transport'), ('Entertainment'), 
('Bills'), ('Shopping'), ('Health'), 
('Education'), ('Other');
```

### 3. Configure Database Connection

Open `src/main/java/com/expensetracker/dao/DatabaseConnection.java` and update:

```java
private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker";
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Deploy to Tomcat

**Option A: Using IDE**
1. Import project as Maven project
2. Configure Tomcat server in your IDE
3. Deploy and run the application

**Option B: Manual Deployment**
1. Copy `target/expense-tracker.war` to `tomcat/webapps/`
2. Start Tomcat: `./bin/startup.sh` (Linux/Mac) or `bin\startup.bat` (Windows)

### 6. Access Application

Open your browser and navigate to:
```
http://localhost:8080/expense-tracker
```

## 💻 Usage

### First Time Users

1. **Register**: Click "Register" and create a new account
2. **Login**: Use your credentials to log in
3. **Add Expense**: Navigate to "Add Expense" and fill in the details
4. **View Dashboard**: See your spending overview and charts
5. **Manage Expenses**: Edit or delete expenses from the expense list

### Database Verification

Check if data is being stored correctly:
```sql
USE expense_tracker;
SELECT * FROM users;
SELECT * FROM expenses;
```

## 📁 Project Structure

```
expense-tracker/
│
├── src/main/
│   ├── java/com/expensetracker/
│   │   ├── model/              # POJOs (User, Expense, Category)
│   │   ├── dao/                # Database access layer
│   │   ├── servlet/            # Request handlers
│   │   └── util/               # Helper classes
│   │
│   ├── webapp/
│   │   ├── WEB-INF/
│   │   │   └── web.xml         # Servlet mappings
│   │   ├── css/                # Stylesheets
│   │   ├── js/                 # JavaScript files
│   │   └── *.jsp               # JSP pages
│   │
│   └── resources/              # SQL scripts
│
├── target/                     # Build output
├── pom.xml                     # Maven configuration
└── README.md                   # Project documentation
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m 'Add YourFeature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Open a Pull Request

## 👨‍💻 Author

**Ajinkya Wagh**
- GitHub: [@Ajinkyawagh4846](https://github.com/Ajinkyawagh4846)

---

⭐ **If you found this project helpful, please consider giving it a star!** ⭐
