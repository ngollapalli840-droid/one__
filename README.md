# SimpleBankAccount

## Description
SimpleBankAccount is a beginner-friendly banking web application built in Java 17 using Apache Maven. It packages into a deployable `.war` file suitable for **Jenkins CI/CD** and **Apache Tomcat** (or standalone local execution).

It simulates core banking operations including account creation, money deposit, money withdrawal (with overdraft protection), balance inquiry, and real-time transaction tracking.

## Deployment & Execution Modes
1. **Jenkins & Apache Tomcat Deployment (WAR File)**:
   - Packages as `target/simple-bank-account.war`.
   - Ready for automated Jenkins CI/CD deployment to Tomcat's `webapps` directory.
   - Accessible via browser at `http://<tomcat-server>:8080/simple-bank-account/`.
2. **Local Standalone Web Server**:
   - Runs directly on `http://localhost:8080/` without needing an external servlet container.
3. **Interactive Console Mode (Terminal / CLI)**:
   - Command-line application using `java.util.Scanner`.

## Features
- **Account Creation**: Set up account with account number, holder name, and optional initial deposit.
- **Deposit**: Adds funds to the account with positive number validation.
- **Withdrawal**: Overdraft guard preventing withdrawals when balance is insufficient.
- **Check Balance**: Real-time display of current balance and account holder information.
- **Transaction History**: Real-time transaction log.
- **RESTful Endpoints & Jakarta Servlet**: Handles `/api/account`, `/api/create`, `/api/deposit`, `/api/withdraw`, `/api/reset`.

## Technologies
- **Java**: Java 17
- **Packaging**: WAR (`<packaging>war</packaging>`)
- **Build Tool**: Apache Maven
- **Servlet Specification**: Jakarta Servlet API 6.0 (`jakarta.servlet-api`)
- **Frontend**: HTML5, CSS3, JavaScript

## Project Structure
```text
├── pom.xml
├── README.md
├── .gitignore
├── .vscode/
│   ├── launch.json
│   └── tasks.json
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── bank/
    │   │           ├── BankAccount.java
    │   │           ├── BankApp.java
    │   │           ├── BankServlet.java
    │   │           └── BankWebServer.java
    │   └── webapp/
    │       ├── index.html
    │       └── WEB-INF/
    │           └── web.xml
    └── test/
        └── java/
```

## How to Build (Jenkins / Maven)
Run Maven in the repository root directory:
```bash
mvn clean package
```
This compiles the code and generates:
```text
target/simple-bank-account.war
```

## How to Deploy & Run

### 1. Deploy on Apache Tomcat via Jenkins
- In your Jenkins job configuration, add a post-build step **"Deploy war/ear to a container"** (or use `scp`/`cp` in pipeline script).
- Point to: `target/simple-bank-account.war`
- Context path: `simple-bank-account` (or `/` for root).
- Access in your browser:
  ```text
  http://<server-ip>:8080/simple-bank-account/
  ```

### 2. Standalone Web Server (Local testing without Tomcat)
```bash
java -cp target/classes com.bank.BankWebServer
```
Open [http://localhost:8080](http://localhost:8080) in your browser.

### 3. Interactive Console (Terminal)
```bash
java -cp target/classes com.bank.BankApp
```