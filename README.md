# 🏦 Enterprise Banking System

A comprehensive, enterprise-grade banking management system built with JavaFX and PostgreSQL, featuring multi-role authentication, account management, transaction processing, and real-time analytics.

## ✨ Features

### 🔐 Authentication & Security
- **Multi-role Access Control**: Admin, Cashier, and Auditor roles with secure login
- **Password Security**: SHA-256 hashing with salt generation
- **Session Management**: Automatic logout and session timeout
- **Role-based Permissions**: Different access levels for different user types

### 🏛️ Account Management
- **Account Types**: Savings and Current accounts with business rule enforcement
- **CRUD Operations**: Create, Read, Update, and Delete accounts
- **Balance Management**: Real-time balance updates with validation
- **Account Validation**: Minimum balance requirements and overdraft limits

### 💰 Transaction Management
- **Transaction Types**: Deposit, Withdrawal, and Transfer operations
- **Real-time Validation**: Business rule enforcement and error handling
- **Transaction History**: Complete audit trail of all banking operations
- **Status Tracking**: Pending, Completed, Failed, and Cancelled states

### 📊 Dashboard & Analytics
- **Real-time Statistics**: Live updates of key banking metrics
- **Visual Cards**: Beautiful UI components showing account totals, balances, and transactions
- **System Status**: Real-time monitoring of all system components
- **Quick Actions**: Fast access to common banking operations

### 📈 Reports & Analytics
- **Report Generation**: Customizable reports with date ranges and formats
- **Quick Statistics**: Instant access to key performance indicators
- **Report Templates**: Pre-built templates for common banking reports
- **Data Export**: Support for multiple export formats

### ⚙️ System Settings
- **User Preferences**: Theme, language, and session timeout settings
- **Security Configuration**: Password policies and authentication settings
- **System Information**: Version details and system health monitoring
- **Performance Metrics**: System performance and resource utilization

## 🛠️ Technology Stack

- **Frontend**: JavaFX 24.0.1 with FXML and CSS styling
- **Backend**: Java 22 (OpenJDK) with modern OOP principles
- **Database**: PostgreSQL 15.4 with comprehensive schema design
- **Architecture**: MVC pattern with DAO/Repository layers
- **Security**: SHA-256 hashing, role-based access control
- **Build Tool**: Manual compilation with JavaFX modules

## 🚀 Getting Started

### Prerequisites
- **Java**: OpenJDK 22 or higher
- **JavaFX**: SDK 24.0.1
- **Database**: PostgreSQL 15.4 or higher
- **OS**: Windows 10/11, macOS, or Linux

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/enterprise-banking-system.git
   cd enterprise-banking-system
   ```

2. **Set up PostgreSQL Database**
   ```sql
   -- Create database
   CREATE DATABASE enterprise_banking;
   
   -- Connect to database
   \c enterprise_banking;
   
   -- Run the complete schema script
   -- Copy and paste the contents of database_schema.sql
   ```

3. **Configure Database Connection**
   - Update `src/util/DatabaseConfig.java` with your database credentials
   - Ensure PostgreSQL JDBC driver is in the `lib/` folder
   - Test connection using `DatabaseTest.java`

4. **Compile the Application**
   ```bash
   # Windows PowerShell
   javac -cp "lib/*" --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml -sourcepath src -d out src/Main.java src/model/*.java src/model/auth/*.java src/model/banking/*.java src/dao/*.java src/util/*.java src/controller/auth/*.java src/controller/banking/*.java src/controller/dashboard/*.java
   
   # Linux/macOS
   javac -cp "lib/*" --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -sourcepath src -d out src/Main.java src/model/*.java src/model/auth/*.java src/model/banking/*.java src/dao/*.java src/util/*.java src/controller/auth/*.java src/controller/banking/*.java src/controller/dashboard/*.java
   ```

5. **Copy FXML Files**
   ```bash
   # Windows PowerShell
   copy src\view\auth\*.fxml out\view\auth\
   copy src\view\dashboard\*.fxml out\view\dashboard\
   copy src\view\banking\*.fxml out\view\banking\
   copy src\view\reports\*.fxml out\view\reports\
   copy src\view\settings\*.fxml out\view\settings\
   
   # Linux/macOS
   cp src/view/auth/*.fxml out/view/auth/
   cp src/view/dashboard/*.fxml out/view/dashboard/
   cp src/view/banking/*.fxml out/view/banking/
   cp src/view/reports/*.fxml out/view/reports/
   cp src/view/settings/*.fxml out/view/settings/
   ```

6. **Run the Application**
   ```bash
   # Windows PowerShell
   cd out
   java -cp "..\lib\*;." --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml Main
   
   # Linux/macOS
   cd out
   java -cp "lib/*:." --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml Main
   ```

## 🔑 Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| **Admin** | admin | admin123 |
| **Cashier** | cashier | cashier123 |
| **Auditor** | auditor | auditor123 |

## 📁 Project Structure

```
src/
├── model/                    # Data models
│   ├── BankAccount.java     # Abstract base class for accounts
│   ├── SavingsAccount.java  # Savings account implementation
│   ├── CurrentAccount.java  # Current account implementation
│   ├── auth/                # Authentication models
│   │   ├── User.java        # User entity
│   │   └── Role.java        # Role enumeration
│   └── banking/             # Banking models
│       └── Transaction.java # Transaction entity
├── controller/               # FXML controllers (MVC pattern)
│   ├── auth/                # Authentication controllers
│   ├── banking/             # Banking operation controllers
│   └── dashboard/           # Dashboard and navigation controllers
├── dao/                     # Data Access Objects
│   ├── UserDAO.java         # User database operations
│   ├── BankAccountDAO.java  # Account database operations
│   └── TransactionDAO.java  # Transaction database operations
├── util/                    # Utility classes
│   ├── DatabaseConfig.java  # Database connection management
│   ├── SecurityUtil.java    # Security and encryption utilities
│   └── DatabaseTest.java    # Database connectivity testing
├── view/                    # FXML UI files
│   ├── auth/                # Authentication views
│   ├── dashboard/           # Dashboard and navigation views
│   ├── banking/             # Banking operation views
│   ├── reports/             # Reports and analytics views
│   └── settings/            # System settings views
└── Main.java                # Application entry point
```

## 🎨 UI Features

- **Modern Design**: Clean, professional interface with rounded corners and shadows
- **Responsive Layout**: Adaptive design that works on different screen sizes
- **Color-coded Elements**: Intuitive color scheme for different account types and statuses
- **Interactive Components**: Buttons, forms, and tables with proper validation
- **Professional Styling**: Enterprise-grade appearance with consistent design language

## 🔒 Security Features

- **Password Hashing**: Secure password storage using SHA-256 with salt
- **Session Management**: Automatic logout and session timeout
- **Input Validation**: Comprehensive validation for all user inputs
- **Audit Logging**: Complete trail of all system activities
- **Role-based Access**: Granular permissions based on user roles
- **Account Masking**: Secure display of sensitive account information

## 📊 Database Schema

The system includes comprehensive database design with:

### Core Tables
- **users**: User authentication and profile information
- **bank_accounts**: Account details and balances
- **transactions**: Complete transaction history
- **audit_logs**: System activity and security logs

### Supporting Tables
- **customers**: Customer relationship management
- **customer_accounts**: Customer-account relationships
- **account_statements**: Account statement generation
- **system_settings**: System configuration and preferences

### Features
- **Indexes**: Optimized for performance
- **Views**: Simplified data access
- **Functions**: Business logic implementation
- **Triggers**: Automated data consistency
- **Sample Data**: Pre-populated for testing

## 🧪 Testing

### Database Testing
```bash
# Test database connectivity
javac -cp "lib/*" util/DatabaseTest.java
java -cp "lib/*;src" util.DatabaseTest

# Test database integration
javac -cp "lib/*" dao/DatabaseIntegrationTest.java
java -cp "lib/*;src" dao.DatabaseIntegrationTest
```

### Application Testing
1. **Login Testing**: Test all user roles and invalid credentials
2. **Account Operations**: Create, modify, and delete accounts
3. **Transaction Processing**: Test deposits, withdrawals, and transfers
4. **Navigation Testing**: Verify all menu items and page loads
5. **Data Validation**: Test input validation and error handling

## 🚀 Deployment

### Development Environment
- Local PostgreSQL database
- JavaFX SDK for development
- Manual compilation and testing

### Production Environment
- Production PostgreSQL server
- JavaFX runtime modules
- Automated build and deployment
- Monitoring and logging

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit your changes** (`git commit -m 'Add some AmazingFeature'`)
4. **Push to the branch** (`git push origin feature/AmazingFeature`)
5. **Open a Pull Request**

### Contribution Guidelines
- Follow Java coding conventions
- Add comprehensive documentation
- Include unit tests for new features
- Update README.md for significant changes
- Ensure all tests pass before submitting

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **JavaFX Team**: For the excellent desktop application framework
- **PostgreSQL Community**: For the robust database system
- **Open Source Contributors**: For various libraries and tools
- **Enterprise Architecture Patterns**: For scalable design principles

## 📞 Support

### Getting Help
- **GitHub Issues**: Open an issue for bugs or feature requests
- **Documentation**: Check the README and code comments
- **Community**: Join our discussion forum

### Common Issues
- **JavaFX Not Found**: Ensure JavaFX SDK is properly installed and configured
- **Database Connection**: Verify PostgreSQL is running and credentials are correct
- **Compilation Errors**: Check Java version compatibility and module paths

## 🔮 Roadmap

### Version 2.0
- [ ] Mobile application support
- [ ] Advanced reporting with charts
- [ ] Multi-currency support
- [ ] API endpoints for integration

### Version 3.0
- [ ] Cloud deployment support
- [ ] Machine learning analytics
- [ ] Blockchain integration
- [ ] Advanced security features

---

**Built with ❤️ for Enterprise Banking Solutions**

*This project demonstrates modern Java development practices, enterprise architecture patterns, and professional software engineering principles.*
