# 🏢 Employee Management System

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg?style=for-the-badge&logo=java)](https://openjfx.io/)
[![SceneBuilder](https://img.shields.io/badge/SceneBuilder-21-green.svg?style=for-the-badge)](https://gluonhq.com/products/scene-builder/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

A modern, feature-rich Employee Management System built with JavaFX and JDK 21. This desktop application provides a comprehensive solution for managing employee data with an intuitive graphical user interface designed using SceneBuilder.

## ✨ Features

### Core Functionality
- 👥 **Employee Management**: Add, edit, delete, and search employees
- 📊 **Department Management**: Organize employees by departments
- 💰 **Salary Management**: Track and manage employee compensation
- 📅 **Attendance Tracking**: Monitor employee attendance records
- 📈 **Reports Generation**: Generate comprehensive reports and analytics
- 🔍 **Advanced Search**: Filter and search employees by various criteria

### User Interface
- 🎨 **Modern UI Design**: Clean and intuitive interface built with JavaFX
- 📱 **Responsive Layout**: Adaptive design that works on different screen sizes
- 🌙 **Dark/Light Theme**: Toggle between different visual themes
- 📋 **Data Tables**: Sortable and filterable data presentation
- 🔄 **Real-time Updates**: Instant data synchronization across views

### Technical Features
- 💾 **Database Integration**: Persistent data storage
- 🔐 **Data Validation**: Input validation and error handling
- 📁 **Export/Import**: Data export to various formats (CSV, PDF)
- 🔄 **Auto-save**: Automatic data backup and recovery
- ⚡ **Performance Optimized**: Efficient data handling for large datasets

## 🚀 Getting Started

### Prerequisites

Before running this application, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **JavaFX SDK 21** or higher
- **Scene Builder 21** (for UI development)
- **MySQL/PostgreSQL** (or your preferred database)
- **Maven** (for dependency management)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/employee-management-system.git
   cd employee-management-system
   ```

2. **Set up JavaFX**
   ```bash
   # Download JavaFX SDK and set JAVAFX_HOME
   export JAVAFX_HOME=/path/to/javafx-sdk-21
   export PATH_TO_FX=$JAVAFX_HOME/lib
   ```

3. **Configure Database**
   ```bash
   # Create database and update connection settings in config.properties
   cp src/main/resources/config.properties.example src/main/resources/config.properties
   # Edit the database connection details
   ```

4. **Build the project**
   ```bash
   mvn clean compile
   ```

5. **Run the application**
   ```bash
   mvn javafx:run
   ```

   Or with module path:
   ```bash
   java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp target/classes com.yourpackage.Main
   ```

## 🏗️ Project Structure

```
employee-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yourpackage/
│   │   │       ├── controllers/     # JavaFX Controllers
│   │   │       ├── models/          # Data Models
│   │   │       ├── services/        # Business Logic
│   │   │       ├── utils/           # Utility Classes
│   │   │       └── Main.java        # Application Entry Point
│   │   └── resources/
│   │       ├── fxml/               # FXML Files (SceneBuilder)
│   │       ├── css/                # Stylesheets
│   │       ├── images/             # Images and Icons
│   │       └── config.properties   # Configuration
│   └── test/                       # Unit Tests
├── docs/                           # Documentation
├── screenshots/                    # Application Screenshots
├── pom.xml                        # Maven Configuration
└── README.md
```

## 💻 Usage

### Main Dashboard
- View employee statistics and quick actions
- Access different modules through the navigation menu
- Monitor system notifications and alerts

### Employee Management
1. **Adding Employees**: Click "Add Employee" and fill in the required details
2. **Editing Employees**: Double-click on any employee record to edit
3. **Searching**: Use the search bar to find employees by name, ID, or department
4. **Filtering**: Apply filters to view specific employee groups

### Generating Reports
1. Navigate to the Reports section
2. Select report type and date range
3. Choose export format (PDF, CSV, Excel)
4. Click "Generate Report"

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Core programming language |
| JavaFX | 21 | GUI framework |
| Scene Builder | 21 | UI design tool |
| Maven | 3.8+ | Build automation |
| MySQL/PostgreSQL | 8.0+/13+ | Database |
| JUnit | 5.9+ | Testing framework |
| Apache POI | 5.2+ | Excel file handling |
| iText | 7.2+ | PDF generation |

## 📸 Screenshots

### Loing
![Loing](https://github.com/user-attachments/assets/16f1f5fd-4592-4c90-b086-a1e3456c964b)

### Main Dashboard
![Dashboard](screenshots/dashboard.png)

### Employee List
![Employee List](screenshots/employee-list.png)

### Add Employee Form
![Add Employee](screenshots/add-employee.png)

### Reports Generation
![Reports](screenshots/reports.png)

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Development Guidelines
- Follow Java coding conventions
- Write meaningful commit messages
- Add unit tests for new features
- Update documentation as needed
- Ensure UI changes are tested with Scene Builder

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🐛 Bug Reports & Feature Requests

If you encounter any bugs or have feature requests, please:

1. Check existing [issues](https://github.com/yourusername/employee-management-system/issues)
2. Create a new issue with detailed description
3. Include steps to reproduce (for bugs)
4. Attach screenshots if applicable

## 📞 Support

- 📧 Email: your.email@example.com
- 💬 Discussions: [GitHub Discussions](https://github.com/yourusername/employee-management-system/discussions)
- 📖 Wiki: [Project Wiki](https://github.com/yourusername/employee-management-system/wiki)

## 🙏 Acknowledgments

- JavaFX community for excellent documentation
- Scene Builder team for the intuitive UI design tool
- Contributors who helped improve this project
- Open source libraries that made this project possible

---

<div align="center">
  <p>Made with ❤️ by [Your Name]</p>
  <p>⭐ Star this repo if you find it helpful!</p>
</div>
