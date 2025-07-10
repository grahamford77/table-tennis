# 🏓 Rightmove Table Tennis Portal

A modern web application for managing table tennis tournaments, player registrations, and games within Rightmove.

## ✨ Features

### 🔐 Authentication & Security
- **User Login System** - Secure authentication with username/password
- **Forgotten Password** - Email-based password reset functionality
- **Admin Access Control** - Role-based access to administrative features
- **Email Setup** - First-time login email configuration

### 🏆 Tournament Management
- **Create Tournaments** - Add new tournaments with details (name, description, date, time, location, max entrants)
- **Edit Tournaments** - Update tournament information
- **Delete Tournaments** - Remove tournaments (disabled once started)
- **Tournament Scheduling** - Automatic game generation for round-robin format
- **Game Results** - Record and track match scores

### 👥 Player & Registration Management
- **Player Registration** - Register for available tournaments
- **Player Profiles** - Automatic player creation from registration data
- **Registration Tracking** - View all tournament registrations
- **Capacity Management** - Prevent over-registration based on tournament limits

### 📊 Dashboard & Reporting
- **Admin Dashboard** - Overview of tournaments, registrations, and statistics
- **Active Tournaments** - Count of tournaments starting within 2 weeks
- **Registration Lists** - View participants for each tournament
- **Game Tracking** - Monitor match progress and results

### 📧 Email System
- **Password Reset Emails** - Secure token-based password recovery
- **Mailjet Integration** - Professional email delivery service
- **HTML & Text Templates** - Multi-format email support
- **Custom Email Templates** - Branded Rightmove styling

## 🚀 Technology Stack

- **Backend**: Spring Boot 3.5.3, Java 21
- **Database**: H2 (local), PostgreSQL (production)
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Security**: Spring Security with BCrypt password encoding
- **Email**: Mailjet API for reliable email delivery
- **Database Migration**: Liquibase for schema management
- **Testing**: JUnit 5, Mockito, DataFaker for test data

## 🛠️ Setup & Installation

### Prerequisites
- Java 21 or higher
- Gradle 8.1.1 or higher
- PostgreSQL (for production)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd table-tennis
   ```

2. **Set up environment variables**
   ```bash
   export MAILJET_API_KEY=your-mailjet-api-key
   export MAILJET_SECRET_KEY=your-mailjet-secret-key
   export EMAIL_FROM=noreply@rightmove.co.uk
   export BASE_URL=http://localhost:8080
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the application**
   - Main app: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

### Database Setup

The application uses Liquibase for database migration:

```bash
# Update H2 database
./gradlew liquibaseUpdateH2

# Check migration status
./gradlew liquibaseStatusH2
```

### Production Deployment

For production with PostgreSQL:

1. **Set environment variables**
   ```bash
   export SPRING_PROFILES_ACTIVE=docker
   export DATABASE_URL=postgresql://username:password@host:port/database
   ```

2. **Build and run with Docker**
   ```bash
   docker build -t table-tennis .
   docker run -p 10000:10000 table-tennis
   ```

## 📧 Email Configuration

The application uses Mailjet for sending password reset emails. You need to:

1. **Sign up for Mailjet** at [mailjet.com](https://www.mailjet.com)
2. **Get API credentials** from your Mailjet dashboard
3. **Set environment variables** as shown above
4. **Configure sender email** - must be verified in Mailjet

### Email Templates

Email templates are stored in:
- HTML: `src/main/resources/templates/email/password-reset.html`
- Text: `src/main/resources/templates/email/password-reset.txt`

## 🔐 Security Features

### Password Reset Flow
1. User clicks "Forgotten Password?" on login page
2. Enters email address
3. System generates unique UUID token (1-hour expiry)
4. Email sent with secure reset link
5. User clicks link and enters new password
6. Token validated and password updated

### Security Measures
- **No Email Enumeration** - Always shows success message
- **Token Expiration** - 1-hour limit for security
- **Secure Token Generation** - UUID-based tokens
- **Password Validation** - Minimum 8 characters
- **Automatic Cleanup** - Expired tokens removed

## 🏗️ Architecture

### Project Structure
```
src/main/java/com/tabletennis/
├── config/          # Spring configuration classes
├── controller/      # Web controllers (Admin, Tournament, Registration)
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── exception/      # Custom exception classes
├── mapping/        # Entity-DTO mapping services
├── repository/     # JPA repositories
└── service/        # Business logic services

src/main/resources/
├── db/changelog/   # Liquibase migration files
├── static/        # CSS, JS, images
└── templates/     # Thymeleaf HTML templates
   ├── auth/       # Authentication pages
   ├── admin/      # Admin dashboard
   └── email/      # Email templates
```

### Key Components

- **EmailService** - Handles password reset emails via Mailjet
- **PasswordResetService** - Manages token generation and validation
- **TournamentService** - Tournament and game management
- **UserService** - User authentication and profile management

## 🧪 Testing

The application includes comprehensive test coverage:

```bash
# Run all tests
./gradlew test

# Run specific test category
./gradlew test --tests "*Controller*"
./gradlew test --tests "*Service*"
./gradlew test --tests "*Repository*"
```

### Test Features
- **DataFaker** for realistic test data generation
- **MockMvc** for integration testing
- **Testcontainers** for database testing
- **Mocked Email Service** for testing without sending real emails

## 🎨 UI/UX Features

### Responsive Design
- Mobile-friendly responsive layout
- Rightmove brand colors (#00DEB6, whites, light greys)
- Professional table tennis themed styling
- Accessible form validation and error handling

### User Experience
- Clear navigation between features
- Real-time form validation
- Informative success/error messages
- Professional email templates with branding

## 📋 API Endpoints

### Public Endpoints
- `GET /` - Registration form
- `GET /login` - Login page
- `POST /forgot-password` - Request password reset
- `GET /reset-password` - Password reset form
- `POST /reset-password` - Update password

### Admin Endpoints (Authentication Required)
- `GET /admin` - Admin dashboard
- `GET /tournaments` - Tournament management
- `POST /tournaments/create` - Create new tournament
- `POST /tournaments/edit/{id}` - Update tournament
- `DELETE /tournaments/delete/{id}` - Delete tournament
- `POST /admin/tournaments/{id}/start` - Start tournament
- `GET /admin/tournaments/{id}/games` - View games
- `POST /admin/games/{id}/result` - Update game result

## 🔧 Configuration

### Application Properties

```properties
# Database Configuration
spring.datasource.url=jdbc:h2:file:./data/tabletennis
spring.jpa.hibernate.ddl-auto=none

# Email Configuration
mailjet.api.key=${MAILJET_API_KEY}
mailjet.secret.key=${MAILJET_SECRET_KEY}
app.email.from=${EMAIL_FROM:noreply@rightmove.co.uk}
app.base-url=${BASE_URL:http://localhost:8080}

# Security
logging.level.org.springframework.security=DEBUG
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes with tests
4. Ensure all tests pass
5. Submit a pull request

## 📝 License

Internal Rightmove project - All rights reserved.

## 🆘 Support

For support or questions, contact the development team or raise an issue in the project repository.
