# Selenium Practice Portal

A small Java/Spring Boot registration and login application built for beginners and Selenium WebDriver practice. It stores registration details in a database, hashes passwords with BCrypt, and protects the profile dashboard with Spring Security.

## Features

- Responsive Bootstrap home, registration, login, and dashboard pages.
- Server-side validation, duplicate-username checks, password confirmation, and clear success/error messages.
- BCrypt password hashing and Spring Security login sessions.
- H2 database by default; switch to MySQL through environment variables.
- Stable element IDs and a separate Selenium Java test project.

## Requirements

- JDK 17 or newer
- Maven 3.8+
- Git
- Chrome browser for the Selenium examples (Selenium Manager resolves the matching driver)

## Run locally

1. Clone the repository and open its folder in VS Code:

   ```bash
   git clone https://github.com/YOUR_GITHUB_USERNAME/googleform_selenium.git
   cd googleform_selenium
   code .
   ```

2. Confirm Java and Maven are available:

   ```bash
   java -version
   mvn -version
   ```

3. Start the application:

   ```bash
   mvn spring-boot:run
   ```

4. Open [http://localhost:8080](http://localhost:8080), register, and log in. The default H2 database is stored under `data/` and is created automatically. The `data/` folder is ignored by Git.

Build a runnable JAR with `mvn clean package`, then start it with:

```bash
java -jar target/selenium-practice-portal-0.0.1-SNAPSHOT.jar
```

## Database configuration

The default H2 file database is suitable for local learning. The application uses Spring Boot environment-variable configuration, so a MySQL database can be used without editing Java code. Set:

```text
SPRING_DATASOURCE_URL=jdbc:mysql://HOST:3306/DATABASE?sslMode=REQUIRED
SPRING_DATASOURCE_USERNAME=DATABASE_USER
SPRING_DATASOURCE_PASSWORD=DATABASE_PASSWORD
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
```

Create the database/schema in your MySQL service before starting the application. The MySQL Connector/J driver is included. Hibernate updates the schema automatically for this learning project; for a production system, use reviewed database migrations and a managed database with backups. Never put production credentials in Git.

## Run the Java Selenium examples

Start the application in one terminal (`mvn spring-boot:run`), then in another:

```bash
cd selenium-tests
mvn test
```

The test class covers successful and failed registration, successful and invalid login, department/section dropdown selection, gender radio selection, and logout. Selenium Manager obtains a compatible ChromeDriver if required. To run against a deployed or alternate URL:

```bash
mvn test -DbaseUrl=https://YOUR_PUBLIC_HTTPS_HOST
```

Headless Chrome can be enabled with:

```bash
mvn test -Dheadless=true
```

The application test suite (no browser required) runs with `mvn test` from the repository root.

## Publish to GitHub

1. Create a new, empty repository on GitHub (for example `googleform_selenium`).
2. From this project directory, set the remote and push:

   ```bash
   git remote add origin https://github.com/YOUR_GITHUB_USERNAME/googleform_selenium.git
   git add .
   git commit -m "Create Selenium Practice Portal"
   git branch -M main
   git push -u origin main
   ```

If a remote is already configured, inspect it with `git remote -v` and use the correct repository URL instead of adding a duplicate.

## Deploy and obtain a public HTTPS URL (Render + managed MySQL)

This project includes a Dockerfile so it can run on a Java-compatible Docker host. Render's free web services may sleep when idle and free-tier terms can change; choose a paid instance or another Java/Docker host if you need persistent availability. Use a managed MySQL provider with backups for persistent deployed data; the local H2 file is not appropriate for ephemeral hosting.

1. Push the project to GitHub and create a managed MySQL database with a provider of your choice. Note its host, database name, username, and password. Allow the hosting service to connect, and require TLS if supported.
2. In Render, choose **New → Web Service**, connect the GitHub repository, and select **Docker** as the runtime. Keep the root directory at the repository root.
3. Add the environment variables from the database provider:
   - `SPRING_DATASOURCE_URL`: `jdbc:mysql://HOST:3306/DATABASE?sslMode=REQUIRED` (use the host and database name from your provider)
   - `SPRING_DATASOURCE_USERNAME`: database username
   - `SPRING_DATASOURCE_PASSWORD`: database password
   - `SPRING_DATASOURCE_DRIVER_CLASS_NAME`: `com.mysql.cj.jdbc.Driver`
4. Deploy. The Docker image compiles the project and starts Spring Boot on the port provided by the host.
5. After deployment succeeds, open the service's **Settings** or overview page and copy its public `onrender.com` URL. Render provisions HTTPS for the public service URL; visit that HTTPS address to register a user and verify login.
6. To run Selenium against it, use the public URL as `-DbaseUrl`, and run tests only against an application/database intended for test data.

Hosting plans, free tiers, database availability, and dashboard labels can change. Check the selected providers' current pricing and networking/TLS instructions before deployment. Do not expose a database publicly without access controls.

## Project files

| File/folder | Purpose |
| --- | --- |
| `pom.xml` | Spring Boot Maven build and runtime/test dependencies. |
| `Dockerfile` | Builds the application JAR and packages it in a Java 17 runtime image. |
| `.gitignore` | Excludes build output, local database files, and IDE files. |
| `src/main/java/.../SeleniumPracticePortalApplication.java` | Spring Boot application entry point. |
| `src/main/java/.../config/SecurityConfig.java` | Public routes, BCrypt authentication, login, and logout security configuration. |
| `src/main/java/.../model/PortalUser.java` | JPA database entity for registered profile data and the password hash. |
| `src/main/java/.../repository/PortalUserRepository.java` | Database lookups and unique-username checks. |
| `src/main/java/.../service/PortalUserDetailsService.java` | Loads a persisted user for Spring Security authentication. |
| `src/main/java/.../web/RegistrationForm.java` | Registration form fields and server-side validation rules. |
| `src/main/java/.../web/PortalController.java` | Home, registration, login page, and protected dashboard web routes. |
| `src/main/resources/application.properties` | Port and environment-driven H2/MySQL database settings. |
| `src/main/resources/templates/fragments.html` | Shared navigation bar and footer used by the pages. |
| `src/main/resources/templates/index.html` | Home page and registration/login links. |
| `src/main/resources/templates/register.html` | Registration form with validation feedback and Selenium-friendly controls. |
| `src/main/resources/templates/login.html` | Login form and invalid-credentials message. |
| `src/main/resources/templates/dashboard.html` | Authenticated user profile table and logout form. |
| `src/main/resources/static/css/styles.css` | Small custom layer over Bootstrap styling. |
| `src/main/resources/static/js/registration.js` | Live password-match hint; server validation remains authoritative. |
| `src/test/java/.../PortalRegistrationLoginTest.java` | HTTP-level tests for registration, BCrypt, authentication, and access control. |
| `selenium-tests/pom.xml` | Independent Maven build for browser-based Selenium tests. |
| `selenium-tests/src/test/java/.../SeleniumPortalTest.java` | Seven Selenium WebDriver examples for registration, login, controls, and logout. |

## Selenium locator examples

Registration and login controls have predictable IDs and `name` attributes, for example `fullName`, `username`, `password`, `confirmPassword`, `dob`, `genderMale`, `genderFemale`, `genderOther`, `department`, `section`, `receiveUpdates`, `registerBtn`, and `loginBtn`. The registration checkbox preference is saved with the user's profile. The dashboard exposes `profileTable`, and its logout form uses `logoutBtn`. Forms include associated labels and Spring Security CSRF tokens.
