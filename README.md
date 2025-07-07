# Rightmove Table Tennis Portal

A comprehensive Spring Boot web application for managing table tennis tournaments with player registration, round-robin game management, and administrative features.

## 🏓 Features

### Tournament Management
- **Create Tournaments**: Add new tournaments with name, description, date, time, location, and maximum entrants
- **Edit Tournaments**: Modify existing tournament details
- **Delete Tournaments**: Remove tournaments from the system
- **Tournament Listing**: View all tournaments with registration counts and participant details
- **Active Tournament Tracking**: Monitor tournaments starting within the next 2 weeks

### Player Registration
- **Registration Form**: Simple, responsive form for tournament registration
- **Player Management**: Automatic player creation and management by email
- **Validation**: Comprehensive form validation with error handling
- **Registration Tracking**: View all registrations with tournament breakdowns

### Game Management
- **Round-Robin Tournament**: Automatic generation of games where each player plays every other player
- **Game Scheduling**: Systematic game ordering and management
- **Score Recording**: Enter and update game results
- **Tournament Progress**: Track completion status and statistics

### Administrative Features
- **Admin Dashboard**: Comprehensive overview with tournament statistics
- **User Authentication**: Secure login system with role-based access
- **Admin-Only Areas**: Protected tournament management and game administration
- **Registration Overview**: View all player registrations and tournament participants

### Technical Features
- **Database Management**: Liquibase-based schema versioning and data migration
- **Multi-Environment Support**: Separate configurations for local H2 and production PostgreSQL
- **Docker Support**: Containerized deployment with Docker
- **Professional UI**: Modern, responsive design with Rightmove branding
- **RESTful API**: JSON-based endpoints for form submissions

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- Gradle 8.0 or higher
- Docker (optional, for containerized deployment)

### Local Development

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

### Docker Deployment

1. **Build Docker image**
   ```bash
   docker build -t table-tennis-app .
   ```

2. **Run with Docker**
   ```bash
   docker run -p 10000:10000 table-tennis-app
   ```

3. **Access the application**
   - Application: http://localhost:10000

## 🏗️ Architecture

### Technology Stack
- **Backend**: Spring Boot 3.3.5, Spring Security, Spring Data JPA
- **Database**: H2 (local), PostgreSQL (production)
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Build Tool**: Gradle
- **Database Migration**: Liquibase
- **Testing**: JUnit 5, Spring Boot Test

### Project Structure
```
src/
├── main/
│   ├── java/com/tabletennis/
│   │   ├── config/           # Security and password configuration
│   │   ├── controller/       # REST controllers (Admin, Auth, Registration, Tournament)
│   │   ├── dto/             # Data transfer objects
│   │   ├── entity/          # JPA entities (Tournament, Player, Game, User, etc.)
│   │   ├── repository/      # Data access layer
│   │   └── service/         # Business logic layer
│   └── resources/
│       ├── db/changelog/    # Liquibase database migrations
│       ├── static/css/      # Stylesheets
│       └── templates/       # Thymeleaf templates
└── test/                    # Test classes and resources
```

### Database Schema
- **tournaments**: Tournament information and details
- **players**: Player profiles and contact information
- **tournament_registrations**: Many-to-many relationship between players and tournaments
- **games**: Individual game records with scores and status
- **users**: Authentication and authorization data

## 📱 User Interface

### Public Pages
- **Registration Form** (`/`): Tournament registration for players
- **Success Page** (`/success`): Confirmation after successful registration
- **Login Page** (`/login`): Authentication for admin users

### Admin Pages (Authentication Required)
- **Dashboard** (`/admin`): Overview with statistics and tournament management
- **Tournament Management** (`/tournaments`): Create, edit, and delete tournaments
- **Tournament Games** (`/admin/tournaments/{id}/games`): Game management and score entry
- **Registration Overview** (`/registrations`): View all player registrations

## 🔐 Security

### Authentication
- **Admin Login**: Secure authentication with encrypted passwords
- **Role-Based Access**: Admin role required for administrative functions
- **Session Management**: Secure session handling with logout functionality

### Password Security
- **BCrypt Encryption**: Passwords encrypted with strength 12
- **Secure Configuration**: Separated password configuration to avoid circular dependencies

## 🗄️ Database Management

### Liquibase Integration
- **Schema Versioning**: Automated database schema management
- **Data Migration**: Structured data insertion and updates
- **Environment-Specific**: Different configurations for local and production

### Available Commands
```bash
# Update database schema
./gradlew liquibaseUpdateH2

# Check database status
./gradlew liquibaseStatusH2

# Validate changelog
./gradlew liquibaseValidateH2
```

## 🎮 Game Management

### Tournament Flow
1. **Create Tournament**: Admin creates tournament with details
2. **Player Registration**: Players register through the public form
3. **Start Tournament**: Admin initiates round-robin game generation
4. **Game Management**: Track games and record scores
5. **Monitor Progress**: View completion statistics and results

### Round-Robin Algorithm
- Automatically generates games for all player combinations
- Each player plays every other player exactly once
- Games are ordered systematically for fair play
- Supports tournaments with 2+ players

## 🔧 Configuration

### Application Properties
- **Local Development**: Uses H2 database on port 8080
- **Docker Deployment**: Uses PostgreSQL on port 10000
- **Environment-Specific**: Separate configuration files for different deployments

### Environment Variables
- Database connection strings configurable via properties files
- Support for both H2 and PostgreSQL databases
- Flexible port configuration for different deployment scenarios

## 🧪 Testing

### Test Coverage
- **Unit Tests**: Service layer and business logic testing
- **Integration Tests**: Controller and repository testing
- **Security Tests**: Authentication and authorization testing
- **Database Tests**: Data persistence and retrieval testing

### Running Tests
```bash
./gradlew test
```

## 📊 Monitoring and Logging

### Logging
- **SLF4J Integration**: Comprehensive logging throughout the application
- **Structured Logging**: Parameterized log messages for better performance
- **Error Tracking**: Detailed error logging for debugging

### Statistics
- **Tournament Counts**: Active tournaments and registration statistics
- **Player Tracking**: Registration counts and participation metrics
- **Game Progress**: Tournament completion tracking

## 🚀 Deployment

### Local Development
- H2 file-based database for persistence
- Port 8080 for local access
- Auto-configuration for development environment

### Production Deployment
- PostgreSQL database support
- Docker containerization
- Port 10000 for production access
- Environment-specific configurations

## 🤝 Contributing

### Development Standards
- **Code Style**: Uses `var` for local variables where appropriate
- **Stream API**: Functional programming approach for collections
- **Constructor Injection**: Dependency injection best practices
- **Service Layer**: Clean separation of concerns

### Code Quality
- **Lombok Integration**: Reduces boilerplate code
- **Validation**: Comprehensive input validation
- **Error Handling**: Graceful error handling and user feedback
- **Documentation**: Javadoc comments for all public methods

## 📝 API Documentation

### Registration Endpoints
- `POST /register`: Submit tournament registration (JSON)
- `GET /registrations`: View all registrations (Admin only)

### Tournament Endpoints
- `GET /tournaments`: List all tournaments (Admin only)
- `POST /tournaments/create`: Create new tournament (Admin only)
- `POST /tournaments/edit/{id}`: Update tournament (Admin only)
- `POST /tournaments/delete/{id}`: Delete tournament (Admin only)

### Game Management Endpoints
- `POST /admin/tournaments/{id}/start`: Start tournament and generate games
- `GET /admin/tournaments/{id}/games`: View tournament games
- `POST /admin/games/{gameId}/result`: Update game score

## 📞 Support

For issues or questions, please refer to the application logs or contact the development team.

## 🏆 License

This project is proprietary software developed for internal use.
