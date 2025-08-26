# 🗄️ Database Setup Guide for Enterprise Banking System

## 📋 Prerequisites

### 1. Install PostgreSQL
- **Download**: [PostgreSQL Official Website](https://www.postgresql.org/download/)
- **Version**: 12 or higher recommended
- **Platform**: Windows, macOS, or Linux

### 2. Install PostgreSQL JDBC Driver
- **Download**: [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/download/)
- **Version**: 42.2.0 or higher
- **File**: `postgresql-42.2.0.jar`

---

## 🚀 Database Setup Steps

### Step 1: Install PostgreSQL
1. **Run the installer** with default settings
2. **Set password** for `postgres` user (remember this!)
3. **Keep default port** (5432)
4. **Complete installation**

### Step 2: Create Database
1. **Open pgAdmin** (comes with PostgreSQL)
2. **Connect to server** using your password
3. **Right-click on Databases** → **Create** → **Database**
4. **Name**: `enterprise_banking`
5. **Click Save**

### Step 3: Run Database Schema
1. **Open pgAdmin Query Tool**
2. **Copy and paste** the contents of `database_schema.sql`
3. **Execute the script** (F5 or Execute button)
4. **Verify tables created** in the left panel

---

## 🔧 Database Configuration

### 1. Update Connection Settings
Edit `src/util/DatabaseConfig.java`:

```java
// Change these values to match your setup
private static final String DB_PASSWORD = "your_actual_password"; // Your PostgreSQL password
private static final String DB_URL = "jdbc:postgresql://localhost:5432/enterprise_banking";
```

### 2. Add JDBC Driver to Classpath
Copy `postgresql-42.2.0.jar` to your project's `lib` folder and add to classpath.

---

## 📊 Database Schema Overview

### Core Tables
- **`users`** - User authentication and roles
- **`bank_accounts`** - Account information
- **`transactions`** - Transaction records
- **`customers`** - Customer information
- **`audit_logs`** - System audit trail

### Views
- **`account_summary`** - Account overview with transaction counts
- **`transaction_summary`** - Daily transaction summaries

### Functions
- **`update_account_balance()`** - Balance update logic
- **`log_audit_event()`** - Audit logging

---

## 🧪 Test Database Connection

### 1. Compile and Test
```bash
# Compile with JDBC driver
javac -cp "lib/*" src/util/DatabaseConfig.java

# Test connection
java -cp "lib/*:src" util.DatabaseConfig
```

### 2. Expected Output
```
Database connected successfully!
Database connection test passed!
```

---

## 🚨 Troubleshooting

### Common Issues

#### 1. Connection Refused
- **Check**: PostgreSQL service is running
- **Fix**: Start PostgreSQL service

#### 2. Authentication Failed
- **Check**: Password in `DatabaseConfig.java`
- **Fix**: Update password to match PostgreSQL

#### 3. Database Not Found
- **Check**: Database name in connection URL
- **Fix**: Create database or update URL

#### 4. JDBC Driver Not Found
- **Check**: JAR file in classpath
- **Fix**: Add `-cp "lib/*"` to java command

---

## 📁 File Structure

```
project/
├── database_schema.sql          # Database schema
├── DATABASE_SETUP.md            # This guide
├── lib/
│   └── postgresql-42.2.0.jar   # JDBC driver
└── src/
    └── util/
        └── DatabaseConfig.java  # Database connection utility
```

---

## 🔐 Security Notes

1. **Change default password** for `postgres` user
2. **Use environment variables** for sensitive data in production
3. **Enable SSL** for production deployments
4. **Restrict network access** to database server

---

## 📈 Next Steps

After database setup:
1. **Test connection** from Java application
2. **Implement DAO classes** for data access
3. **Add connection pooling** for production use
4. **Implement data validation** and error handling

---

## 🆘 Need Help?

- **PostgreSQL Documentation**: [https://www.postgresql.org/docs/](https://www.postgresql.org/docs/)
- **JDBC Documentation**: [https://jdbc.postgresql.org/documentation/](https://jdbc.postgresql.org/documentation/)
- **Stack Overflow**: Tag with `postgresql` and `java`
