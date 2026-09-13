# SimpleBankAccount

## Description
SimpleBankAccount is a beginner-friendly banking application built in Java using Apache Maven. It simulates core banking operations including account creation, money deposit, money withdrawal (with overdraft protection), and balance inquiry.

It supports two modes:
1. **Interactive Console Mode** (Terminal / Command Prompt)
2. **Modern Web Browser Dashboard** (Runs locally on `http://localhost:8080` using standard Java 17, zero external dependencies!)

## Features
- **Account Creation**: Prompts or accepts account number and account holder name.
- **Deposit**: Adds funds to the account with positive amount validation.
- **Withdrawal**: Deducts funds with strict overdraft protection preventing withdrawal when funds are insufficient.
- **Check Balance**: Instant display of account details and updated balance.
- **Web Dashboard**: Modern, responsive UI accessible via any browser with live transaction history and instant feedback.
- **Zero External Dependencies**: Pure standard Java 17 (`java.util.Scanner` and `com.sun.net.httpserver.HttpServer`).

## Technologies
- **Java**: Java 17
- **Build Tool**: Apache Maven
- **Web Server**: Built-in Java `HttpServer`
- **Frontend**: Responsive HTML5, CSS3, and JavaScript

## Project Structure
```text
SimpleBankAccount/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── bank/
    │               ├── BankAccount.java
    │               ├── BankApp.java
    │               └── BankWebServer.java
    └── test/
        └── java/
```

## How to Build
Navigate to the project root directory (`SimpleBankAccount`) and run:
```bash
mvn clean package
```

## How to Run

### Option A: Web Browser Dashboard (Recommended)
Run the web server:
```bash
java -cp target\classes com.bank.BankWebServer
```
Then open your browser and visit:
[http://localhost:8080](http://localhost:8080)

### Option B: Interactive Console (Terminal)
Run the console application:
```bash
java -cp target\classes com.bank.BankApp
```
Or directly run the generated JAR:
```bash
java -cp target/simple-bank-account-1.0.jar com.bank.BankApp
```