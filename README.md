# Graham's Table Tennis Tournament Registration System

A comprehensive Spring Boot web application for managing table tennis tournament registrations with a professional teal and white design.

## 🏓 Features

- **Tournament Management**: Create, edit, and view tournaments with details like date, time, location, and participant limits
- **Player Registration**: Simple form-based registration system with validation
- **Registration Tracking**: View all registrations with dynamic statistics and tournament breakdowns
- **Professional UI**: Modern, responsive design with CSS animations and table tennis imagery
- **Data Persistence**: H2 database with file-based storage for data persistence across restarts

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd table-tennis
   ```

2. **Build the application**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the application**
   - Main application: http://localhost:8080
   - H2 Database console: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:file:./data/tabletennis`
     - Username: `sa`
     - Password: (leave empty)

## 📋 Application Structure

### Layers Architecture

```
┌─────────────────┐
│   Controllers   │ ← Web Layer (HTTP requests/responses)
├─────────────────┤
│    Services     │ ← Business Logic Layer
├─────────────────┤
│  Repositories   │ ← Data Access Layer
├─────────────────┤
│    Entities     │ ← Data Model Layer
└─────────────────┘
```

### Package Structure

```
com.tabletennis/
├── controller/          # Web controllers
│   └── TournamentController.java
├── service/            # Business logic services
│   ├── TournamentService.java
│   ├── TournamentServiceImpl.java
│   ├── RegistrationService.java
│   └── RegistrationServiceImpl.java
├── repository/         # Data access repositories
│   ├── TournamentRepository.java
│   └── TournamentRegistrationRepository.java
├── entity/            # JPA entities
│   ├── Tournament.java
│   └── TournamentRegistration.java
└── TableTennisApplication.java
```

## 🎯 API Endpoints

### Web Pages

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Tournament registration form |
| POST | `/register` | Process registration submission |
| GET | `/success` | Registration success page |
| GET | `/registrations` | View all registrations |
| GET | `/tournaments` | Tournament management page |
| GET | `/tournaments/new` | Create tournament form |
| POST | `/tournaments/create` | Process tournament creation |
| GET | `/tournaments/edit/{id}` | Edit tournament form |
| POST | `/tournaments/edit/{id}` | Process tournament update |

### Database Access

| Endpoint | Description |
|----------|-------------|
| `/h2-console` | H2 database web console |

## 🗄️ Database Schema

### Tournament Table
```sql
CREATE TABLE tournaments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    location VARCHAR(255) NOT NULL,
    max_entrants INTEGER NOT NULL
);
```

### Tournament Registration Table
```sql
CREATE TABLE tournament_registrations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    tournament_id BIGINT NOT NULL,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id)
);
```

## 🎨 Frontend Features

### CSS Architecture
- **External Stylesheet**: All styles moved to `/css/main.css`
- **Responsive Design**: Mobile-first approach with breakpoints
- **Animations**: CSS animations for table tennis elements
- **Professional Theme**: Teal and white color scheme

### Interactive Elements
- **Sports Imagery**: CSS-created table tennis paddles and balls
- **Hover Effects**: Card animations and button interactions
- **Form Validation**: Real-time validation with error messages
- **Statistics Dashboard**: Dynamic tournament registration counts

## 🧪 Testing

### Test Coverage
- **Unit Tests**: All service classes and entities
- **Integration Tests**: Controller layer with MockMvc
- **End-to-End Tests**: Complete application workflow
- **Total Test Classes**: 6 comprehensive test suites

### Running Tests
```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests "TournamentServiceImplTest"
```

### Test Structure
```
src/test/java/com/tabletennis/
├── controller/
│   └── TournamentControllerTest.java
├── service/
│   ├── TournamentServiceImplTest.java
│   └── RegistrationServiceImplTest.java
├── entity/
│   ├── TournamentTest.java
│   └── TournamentRegistrationTest.java
└── TableTennisApplicationIntegrationTest.java
```

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:h2:file:./data/tabletennis
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# H2 Console
spring.h2.console.enabled=true
```

### Build Configuration
- **Java Version**: 17
- **Spring Boot Version**: 3.1.0
- **Build Tool**: Gradle
- **Database**: H2 (file-based)

## 🚀 Deployment

### Development
```bash
./gradlew bootRun
```

### Production Build
```bash
./gradlew build
java -jar build/libs/table-tennis-1.0-SNAPSHOT.jar
```

### Environment Variables
```bash
# Optional: Override database location
export SPRING_DATASOURCE_URL=jdbc:h2:file:/path/to/database

# Optional: Override server port
export SERVER_PORT=8080
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Spring Boot best practices
- Use constructor injection over field injection
- Write comprehensive tests for new features
- Follow the existing code style and naming conventions
- Update documentation for new features

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- H2 Database for the embedded database solution
- Thymeleaf for the templating engine
- The table tennis community for inspiration

## 📞 Support

If you encounter any issues or have questions:
1. Check the existing issues in the repository
2. Create a new issue with detailed description
3. Include steps to reproduce any bugs
4. Provide system information (Java version, OS, etc.)

---

**Happy Playing! 🏓**
