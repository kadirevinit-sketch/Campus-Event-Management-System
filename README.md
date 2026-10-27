# Campus Event Management System 🎓

A complete, functional, beginner-friendly web application for college event management built with **Java 21**, **Spring Boot 3**, **Spring Security**, **Thymeleaf**, **HTML5/CSS3/JavaScript**, and **SQLite**.

---

## 🌟 Key Features

### For Students
- **Account Registration & Login**: Create personal student accounts with department and academic year.
- **Browse & Search Events**: Filter campus events by category (Technical, Hackathon, Workshop, Seminar, Cultural, Sports) or keyword search.
- **Seat Availability & Details**: View real-time seating availability, registration deadlines, schedules, and venue locations.
- **One-Click Event Registration**: Instantly reserve seats for published events.
- **My Registrations Dashboard**: Track registered events and cancel bookings anytime before the event date, automatically releasing the seat to other students.

### For Administrators
- **Admin Dashboard**: Live statistics showing Total Events, Upcoming Scheduled, Completed / Past Events, and Total Registrations.
- **Complete Event Lifecycle**: Create new campus events, edit details, update status (Published, Draft, Cancelled, Completed), or delete events with confirmation.
- **Participant Rosters**: View detailed student participant lists for any event with full names, student emails, departments, and timestamps.
- **Access Control**: All `/admin/**` endpoints are strictly protected with Spring Security role-based authorization.

---

## 🛠️ Technology Stack

- **Java**: Java 21 (also compatible with Java 17)
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security 6 (Session-based, BCrypt password hashing, CSRF protection, Role-based authorization)
- **Persistence**: Spring Data JPA & Hibernate Community SQLite Dialect
- **Database**: SQLite 3 (`campus_events.db`)
- **UI Engine**: Thymeleaf & Thymeleaf Extras Spring Security 6
- **Frontend**: Vanilla CSS3 (Dark Slate & Indigo Glassmorphism design system), HTML5, JavaScript
- **Build Tool**: Apache Maven

---

## 🚀 How to Run the Project Locally

### Prerequisites
1. **JDK 17 or JDK 21** installed and added to your `PATH`.
2. **Apache Maven 3.8+** installed.

### Step-by-Step Instructions

1. **Open Terminal** in the project directory:
   ```bash
   cd c:\Users\ARFAT\Desktop\campusEvent
   ```

2. **Compile and Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Open the Website**:
   Navigate your browser to:
   ```
   http://localhost:8080
   ```

---

## 🔐 Default Accounts & Initial Administrator

On first startup, the application's `DataInitializer` automatically configures the database and creates default accounts:

### 1. Administrator Account
- **Email**: `admin@campus.edu`
- **Password**: `Admin@123`
- **Role**: `ROLE_ADMIN`
- **Dashboard URL**: `http://localhost:8080/admin/dashboard`

> **Note**: You can customize the administrator credentials in `src/main/resources/application.properties` using `app.admin.email` and `app.admin.password`, or pass them as environment variables (`APP_ADMIN_EMAIL`, `APP_ADMIN_PASSWORD`).

### 2. Sample Student Account
- **Email**: `student@campus.edu`
- **Password**: `Student@123`
- **Role**: `ROLE_STUDENT`

> **Tip**: On the Login page (`/login`), you can click the **"⚡ Quick Demo Fill"** buttons to auto-populate the email and password fields for testing.

---

## 🗄️ Database Location & Persistence

- The database file is located at the project root directory:
  ```
  ./campus_events.db
  ```
- SQLite stores all tables, user accounts, events, and registration records directly in this single self-contained file.
- All data **persists across application restarts**.

---

## 🧪 Running Automated Tests

To execute the test suite (testing database connectivity, registration business rules, capacity enforcement, duplicate prevention, and Spring Security access control):

```bash
mvn test
```

---

## 📁 Project Architecture & Package Structure

```
com.campus.event
├── config/
│   ├── CustomUserDetailsService.java          # Loads users from DB for Spring Security
│   ├── DataInitializer.java                  # Seeds initial admin, student, & 6 sample events
│   ├── RoleBasedAuthenticationSuccessHandler.java # Routes admins to dashboard, students to events
│   └── SecurityConfig.java                   # Security filter chain, CSRF, & role permissions
├── controller/
│   ├── AdminController.java                  # Admin dashboard, event CRUD, participant rosters
│   ├── AuthController.java                   # Login & student registration handling
│   ├── CustomErrorController.java            # 403, 404, 500 error views
│   ├── EventController.java                  # Event listing, search, filtering, and detail view
│   ├── GlobalControllerAdvice.java           # Injects user session info to all Thymeleaf templates
│   ├── HomeController.java                   # Landing page with featured events & live stats
│   └── RegistrationController.java           # Student registration booking & cancellation
├── dto/
│   ├── EventDisplayDto.java                  # UI model containing event + calculated seat metrics
│   ├── EventDto.java                         # Form backing object for event creation/editing
│   └── StudentRegisterDto.java               # Form backing object for student signup
├── model/
│   ├── Event.java                            # JPA entity for campus events
│   ├── Registration.java                     # JPA entity for event bookings with unique constraint
│   └── User.java                             # JPA entity for students and admins
├── repository/
│   ├── EventRepository.java                  # JPA repo with custom search queries
│   ├── RegistrationRepository.java           # JPA repo for booking queries & seat counts
│   └── UserRepository.java                   # JPA repo for email lookups & role counts
└── service/
    ├── EventService.java                     # Event business logic & dashboard metrics
    ├── RegistrationService.java              # Registration rules & concurrency-safe bookings
    └── UserService.java                      # Account creation & password hashing
```

---

## 📜 Business Rules Enforced

1. **Single Registration**: A student cannot register more than once for the same event (`uk_student_event` database unique constraint + backend check).
2. **Status Check**: Registrations are only allowed for `PUBLISHED` events.
3. **No Inactive Events**: Registrations are rejected if the event is `CANCELLED`, `COMPLETED`, or past its date.
4. **Deadline Enforcement**: Registrations are rejected once the deadline date has elapsed.
5. **Capacity Limits**: Strict seat capacity enforcement prevents overbooking.
6. **Thread-Safe Seat Allocation**: Synchronized monitor locks ensure concurrent requests never exceed maximum capacity.
7. **Seat Release on Cancellation**: Cancelling a registration immediately increases available seats.
8. **Privacy**: Only administrators can view full participant lists; students can only view their own registrations.
9. **Role Isolation**: Public registration forms can only register students (`ROLE_STUDENT`); admin rights cannot be self-assigned.
