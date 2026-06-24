# 🏗️ Land_Link — Land Management System

A Java Swing desktop application for buying and selling land properties.

## 👥 Team Members

| Member | Module | 
|--------|--------|
| Member 1 | Foundation & Authentication |
| Member 2 | Land Management (Seller Side) |
| Member 3 | Browse & Purchase (Buyer Side) |
| Member 4 | Admin Dashboard |
| Member 5 | UI Theme & User Dashboard |

## 🚀 How to Run

### Prerequisites
- Java 17 or later
- Maven 3.6+
- NetBeans IDE (optional)

### From Command Line
```bash
# Build the project
mvn clean compile

# Run the application
mvn exec:java

# Create executable JAR
mvn clean package
java -jar target/LandLink-1.0-SNAPSHOT.jar
```

### From NetBeans
1. Open the project as a Maven project
2. Right-click → Run

## 🔑 Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| User | (Register a new account) | |

## 📁 Project Structure

```
src/main/java/com/landlink/
├── Main.java              # Entry point
├── model/                 # Data models (User, Land, Transaction)
├── dao/                   # Database access layer
├── service/               # Business logic
├── ui/                    # Swing GUI
│   ├── theme/             # Custom UI components
│   ├── admin/             # Admin screens
│   └── user/              # User screens
└── util/                  # Utilities
```

## 🛠️ Tech Stack
- Java 17 + Swing GUI
- SQLite Database
- Maven Build System
- Git + GitHub for collaboration
