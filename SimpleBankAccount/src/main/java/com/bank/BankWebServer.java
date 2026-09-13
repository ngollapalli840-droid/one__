package com.bank;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BankWebServer provides a lightweight, zero-dependency HTTP server
 * that serves a modern browser-based UI for the SimpleBankAccount application.
 */
public class BankWebServer {
    private static final int PORT = 8080;
    private static BankAccount currentAccount = null;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Web UI route
        server.createContext("/", new FrontendHandler());

        // REST API routes
        server.createContext("/api/account", new AccountHandler());
        server.createContext("/api/create", new CreateAccountHandler());
        server.createContext("/api/deposit", new DepositHandler());
        server.createContext("/api/withdraw", new WithdrawHandler());
        server.createContext("/api/reset", new ResetHandler());

        server.setExecutor(null); // default executor
        server.start();

        System.out.println("=================================================");
        System.out.println("   Simple Bank Account Web Server Started!       ");
        System.out.println("   Open in your browser: http://localhost:" + PORT);
        System.out.println("=================================================");
    }

    /**
     * Serves the single-page web UI
     */
    static class FrontendHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
                return;
            }

            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Simple Bank Account - Web Dashboard</title>
                    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
                    <style>
                        :root {
                            --primary: #2563eb;
                            --primary-hover: #1d4ed8;
                            --success: #16a34a;
                            --danger: #dc2626;
                            --bg: #f8fafc;
                            --card-bg: #ffffff;
                            --text-main: #0f172a;
                            --text-muted: #64748b;
                            --border: #e2e8f0;
                        }
                        * {
                            box-sizing: border-box;
                            margin: 0;
                            padding: 0;
                            font-family: 'Inter', sans-serif;
                        }
                        body {
                            background: var(--bg);
                            color: var(--text-main);
                            min-height: 100vh;
                            padding: 24px;
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                        }
                        .container {
                            width: 100%;
                            max-width: 650px;
                        }
                        header {
                            text-align: center;
                            margin-bottom: 24px;
                        }
                        header h1 {
                            font-size: 26px;
                            font-weight: 700;
                            color: #1e293b;
                        }
                        header p {
                            color: var(--text-muted);
                            font-size: 14px;
                            margin-top: 4px;
                        }
                        .card {
                            background: var(--card-bg);
                            border-radius: 16px;
                            padding: 24px;
                            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.07), 0 2px 4px -2px rgba(0,0,0,0.05);
                            border: 1px solid var(--border);
                            margin-bottom: 20px;
                        }
                        .card-title {
                            font-size: 18px;
                            font-weight: 600;
                            margin-bottom: 16px;
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                        }
                        .balance-display {
                            background: linear-gradient(135deg, #2563eb, #1e40af);
                            color: white;
                            border-radius: 14px;
                            padding: 20px;
                            text-align: center;
                            margin-bottom: 20px;
                        }
                        .balance-label {
                            font-size: 13px;
                            text-transform: uppercase;
                            letter-spacing: 0.05em;
                            opacity: 0.85;
                        }
                        .balance-amount {
                            font-size: 36px;
                            font-weight: 700;
                            margin: 6px 0;
                        }
                        .account-meta {
                            font-size: 13px;
                            opacity: 0.9;
                            display: flex;
                            justify-content: space-around;
                            border-top: 1px solid rgba(255,255,255,0.2);
                            padding-top: 10px;
                            margin-top: 10px;
                        }
                        .form-group {
                            margin-bottom: 16px;
                        }
                        label {
                            display: block;
                            font-size: 13px;
                            font-weight: 500;
                            color: #475569;
                            margin-bottom: 6px;
                        }
                        input[type="text"], input[type="number"] {
                            width: 100%;
                            padding: 10px 14px;
                            border: 1px solid var(--border);
                            border-radius: 8px;
                            font-size: 14px;
                            outline: none;
                            transition: border-color 0.2s;
                        }
                        input[type="text"]:focus, input[type="number"]:focus {
                            border-color: var(--primary);
                            box-shadow: 0 0 0 3px rgba(37,99,235,0.15);
                        }
                        .btn {
                            display: inline-flex;
                            align-items: center;
                            justify-content: center;
                            padding: 10px 18px;
                            border-radius: 8px;
                            font-size: 14px;
                            font-weight: 600;
                            cursor: pointer;
                            border: none;
                            transition: background-color 0.2s, transform 0.1s;
                            width: 100%;
                        }
                        .btn:active {
                            transform: scale(0.98);
                        }
                        .btn-primary {
                            background: var(--primary);
                            color: white;
                        }
                        .btn-primary:hover {
                            background: var(--primary-hover);
                        }
                        .btn-success {
                            background: var(--success);
                            color: white;
                        }
                        .btn-success:hover {
                            background: #15803d;
                        }
                        .btn-danger {
                            background: var(--danger);
                            color: white;
                        }
                        .btn-danger:hover {
                            background: #b91c1c;
                        }
                        .btn-secondary {
                            background: #f1f5f9;
                            color: #475569;
                            border: 1px solid var(--border);
                        }
                        .btn-secondary:hover {
                            background: #e2e8f0;
                        }
                        .action-grid {
                            display: grid;
                            grid-template-columns: 1fr 1fr;
                            gap: 16px;
                        }
                        .action-card {
                            border: 1px solid var(--border);
                            border-radius: 10px;
                            padding: 16px;
                            background: #fafafa;
                        }
                        .action-card h3 {
                            font-size: 14px;
                            font-weight: 600;
                            margin-bottom: 10px;
                        }
                        .alert {
                            padding: 12px 16px;
                            border-radius: 8px;
                            font-size: 13px;
                            margin-bottom: 16px;
                            display: none;
                            animation: fadeIn 0.3s ease;
                        }
                        .alert-success {
                            background: #ecfdf5;
                            color: #065f46;
                            border: 1px solid #a7f3d0;
                        }
                        .alert-danger {
                            background: #fef2f2;
                            color: #991b1b;
                            border: 1px solid #fecaca;
                        }
                        .history-list {
                            list-style: none;
                            max-height: 180px;
                            overflow-y: auto;
                        }
                        .history-item {
                            display: flex;
                            justify-content: space-between;
                            padding: 8px 0;
                            border-bottom: 1px solid #f1f5f9;
                            font-size: 13px;
                        }
                        .history-item:last-child {
                            border-bottom: none;
                        }
                        .tx-plus {
                            color: var(--success);
                            font-weight: 600;
                        }
                        .tx-minus {
                            color: var(--danger);
                            font-weight: 600;
                        }
                        @keyframes fadeIn {
                            from { opacity: 0; transform: translateY(-4px); }
                            to { opacity: 1; transform: translateY(0); }
                        }
                        @media (max-width: 520px) {
                            .action-grid {
                                grid-template-columns: 1fr;
                            }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <header>
                            <h1>🏦 Simple Bank Account</h1>
                            <p>Interactive Browser Dashboard &bull; Java 17 Web Server</p>
                        </header>

                        <!-- Notification Banner -->
                        <div id="alertBox" class="alert"></div>

                        <!-- Account Creation Screen -->
                        <div id="createSection" class="card">
                            <h2 class="card-title">Create New Bank Account</h2>
                            <div class="form-group">
                                <label for="accNum">Account Number</label>
                                <input type="text" id="accNum" placeholder="e.g. ACC1001" required>
                            </div>
                            <div class="form-group">
                                <label for="accHolder">Account Holder Name</label>
                                <input type="text" id="accHolder" placeholder="e.g. John Doe" required>
                            </div>
                            <div class="form-group">
                                <label for="initialDeposit">Initial Deposit ($)</label>
                                <input type="number" id="initialDeposit" placeholder="0.00" min="0" step="any" value="0.00">
                            </div>
                            <button class="btn btn-primary" onclick="createAccount()">Open Account</button>
                        </div>

                        <!-- Banking Dashboard Screen -->
                        <div id="dashboardSection" style="display: none;">
                            <!-- Balance Card -->
                            <div class="balance-display">
                                <div class="balance-label">Current Balance</div>
                                <div class="balance-amount" id="balanceText">$0.00</div>
                                <div class="account-meta">
                                    <span><strong>A/C No:</strong> <span id="dispAccNum">-</span></span>
                                    <span><strong>Holder:</strong> <span id="dispAccHolder">-</span></span>
                                </div>
                            </div>

                            <!-- Actions -->
                            <div class="card">
                                <div class="card-title">
                                    <span>Banking Operations</span>
                                    <button class="btn btn-secondary" style="width: auto; padding: 4px 10px; font-size: 12px;" onclick="resetAccount()">Switch Account</button>
                                </div>
                                <div class="action-grid">
                                    <!-- Deposit Form -->
                                    <div class="action-card">
                                        <h3>📥 Deposit Money</h3>
                                        <div class="form-group">
                                            <input type="number" id="depositAmount" placeholder="Amount ($)" min="0.01" step="any">
                                        </div>
                                        <button class="btn btn-success" onclick="depositMoney()">Deposit</button>
                                    </div>

                                    <!-- Withdraw Form -->
                                    <div class="action-card">
                                        <h3>📤 Withdraw Money</h3>
                                        <div class="form-group">
                                            <input type="number" id="withdrawAmount" placeholder="Amount ($)" min="0.01" step="any">
                                        </div>
                                        <button class="btn btn-danger" onclick="withdrawMoney()">Withdraw</button>
                                    </div>
                                </div>
                            </div>

                            <!-- Activity Log -->
                            <div class="card">
                                <div class="card-title">Recent Transactions</div>
                                <ul id="historyList" class="history-list">
                                    <li class="history-item" style="color: var(--text-muted); justify-content: center;">No transactions yet</li>
                                </ul>
                            </div>
                        </div>
                    </div>

                    <script>
                        let transactions = [];

                        function showAlert(msg, isSuccess = true) {
                            const alert = document.getElementById('alertBox');
                            alert.className = 'alert ' + (isSuccess ? 'alert-success' : 'alert-danger');
                            alert.textContent = msg;
                            alert.style.display = 'block';
                            setTimeout(() => { alert.style.display = 'none'; }, 4000);
                        }

                        async function checkAccountStatus() {
                            try {
                                const res = await fetch('/api/account');
                                const data = await res.json();
                                if (data.hasAccount) {
                                    showDashboard(data);
                                } else {
                                    showCreateForm();
                                }
                            } catch (err) {
                                showAlert("Failed to connect to server.", false);
                            }
                        }

                        function showDashboard(data) {
                            document.getElementById('createSection').style.display = 'none';
                            document.getElementById('dashboardSection').style.display = 'block';
                            document.getElementById('dispAccNum').textContent = data.accountNumber;
                            document.getElementById('dispAccHolder').textContent = data.accountHolder;
                            document.getElementById('balanceText').textContent = '$' + Number(data.balance).toFixed(2);
                        }

                        function showCreateForm() {
                            document.getElementById('createSection').style.display = 'block';
                            document.getElementById('dashboardSection').style.display = 'none';
                            transactions = [];
                            renderHistory();
                        }

                        async function createAccount() {
                            const accNum = document.getElementById('accNum').value.trim();
                            const accHolder = document.getElementById('accHolder').value.trim();
                            const initialDeposit = parseFloat(document.getElementById('initialDeposit').value) || 0.0;

                            if (!accNum || !accHolder) {
                                showAlert("Please provide both Account Number and Account Holder Name.", false);
                                return;
                            }

                            const res = await fetch('/api/create', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ accountNumber: accNum, accountHolder: accHolder, initialBalance: initialDeposit })
                            });
                            const data = await res.json();
                            if (data.success) {
                                showAlert(data.message, true);
                                checkAccountStatus();
                            } else {
                                showAlert(data.message, false);
                            }
                        }

                        async function depositMoney() {
                            const input = document.getElementById('depositAmount');
                            const amount = parseFloat(input.value);
                            if (isNaN(amount) || amount <= 0) {
                                showAlert("Please enter a valid positive deposit amount.", false);
                                return;
                            }

                            const res = await fetch('/api/deposit', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ amount: amount })
                            });
                            const data = await res.json();
                            if (data.success) {
                                showAlert(data.message, true);
                                input.value = '';
                                document.getElementById('balanceText').textContent = '$' + Number(data.balance).toFixed(2);
                                addTransaction('Deposit', '+$' + amount.toFixed(2), true);
                            } else {
                                showAlert(data.message, false);
                            }
                        }

                        async function withdrawMoney() {
                            const input = document.getElementById('withdrawAmount');
                            const amount = parseFloat(input.value);
                            if (isNaN(amount) || amount <= 0) {
                                showAlert("Please enter a valid positive withdrawal amount.", false);
                                return;
                            }

                            const res = await fetch('/api/withdraw', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ amount: amount })
                            });
                            const data = await res.json();
                            if (data.success) {
                                showAlert(data.message, true);
                                input.value = '';
                                document.getElementById('balanceText').textContent = '$' + Number(data.balance).toFixed(2);
                                addTransaction('Withdrawal', '-$' + amount.toFixed(2), false);
                            } else {
                                showAlert(data.message, false);
                            }
                        }

                        async function resetAccount() {
                            if (confirm("Are you sure you want to close this session and switch account?")) {
                                await fetch('/api/reset', { method: 'POST' });
                                showCreateForm();
                            }
                        }

                        function addTransaction(title, amountStr, isPositive) {
                            transactions.unshift({ title, amountStr, isPositive, time: new Date().toLocaleTimeString() });
                            renderHistory();
                        }

                        function renderHistory() {
                            const list = document.getElementById('historyList');
                            if (transactions.length === 0) {
                                list.innerHTML = '<li class="history-item" style="color: var(--text-muted); justify-content: center;">No transactions yet</li>';
                                return;
                            }
                            list.innerHTML = transactions.map(tx => `
                                <li class="history-item">
                                    <div>
                                        <strong>${tx.title}</strong>
                                        <div style="font-size: 11px; color: var(--text-muted);">${tx.time}</div>
                                    </div>
                                    <span class="${tx.isPositive ? 'tx-plus' : 'tx-minus'}">${tx.amountStr}</span>
                                </li>
                            `).join('');
                        }

                        // Initialize on page load
                        checkAccountStatus();
                    </script>
                </body>
                </html>
                """;

            sendResponse(exchange, 200, html, "text/html; charset=UTF-8");
        }
    }

    /**
     * GET /api/account
     */
    static class AccountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }

            if (currentAccount == null) {
                sendResponse(exchange, 200, "{\"hasAccount\": false}", "application/json");
            } else {
                String json = String.format(
                    "{\"hasAccount\": true, \"accountNumber\": \"%s\", \"accountHolder\": \"%s\", \"balance\": %.2f}",
                    escape(currentAccount.getAccountNumber()),
                    escape(currentAccount.getAccountHolder()),
                    currentAccount.getBalance()
                );
                sendResponse(exchange, 200, json, "application/json");
            }
        }
    }

    /**
     * POST /api/create
     */
    static class CreateAccountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String accNum = getJsonString(body, "accountNumber");
            String accHolder = getJsonString(body, "accountHolder");
            double initialBalance = getJsonDouble(body, "initialBalance", 0.0);

            if (accNum == null || accNum.isBlank() || accHolder == null || accHolder.isBlank()) {
                sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Account number and holder name are required.\"}", "application/json");
                return;
            }

            currentAccount = new BankAccount(accNum.trim(), accHolder.trim(), Math.max(0.0, initialBalance));
            String json = String.format(
                "{\"success\": true, \"message\": \"Account created successfully for %s!\", \"balance\": %.2f}",
                escape(currentAccount.getAccountHolder()),
                currentAccount.getBalance()
            );
            sendResponse(exchange, 200, json, "application/json");
        }
    }

    /**
     * POST /api/deposit
     */
    static class DepositHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }

            if (currentAccount == null) {
                sendResponse(exchange, 400, "{\"success\": false, \"message\": \"No active account found. Please create an account first.\"}", "application/json");
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            double amount = getJsonDouble(body, "amount", -1);

            if (amount <= 0) {
                sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Invalid deposit amount! Amount must be greater than 0.\"}", "application/json");
                return;
            }

            currentAccount.deposit(amount);
            String json = String.format(
                "{\"success\": true, \"message\": \"Deposited $%.2f successfully!\", \"balance\": %.2f}",
                amount,
                currentAccount.getBalance()
            );
            sendResponse(exchange, 200, json, "application/json");
        }
    }

    /**
     * POST /api/withdraw
     */
    static class WithdrawHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}", "application/json");
                return;
            }

            if (currentAccount == null) {
                sendResponse(exchange, 400, "{\"success\": false, \"message\": \"No active account found. Please create an account first.\"}", "application/json");
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            double amount = getJsonDouble(body, "amount", -1);

            if (amount <= 0) {
                sendResponse(exchange, 400, "{\"success\": false, \"message\": \"Invalid withdrawal amount! Amount must be greater than 0.\"}", "application/json");
                return;
            }

            if (amount > currentAccount.getBalance()) {
                String json = String.format(
                    "{\"success\": false, \"message\": \"Insufficient balance! Withdrawal of $%.2f failed.\", \"balance\": %.2f}",
                    amount,
                    currentAccount.getBalance()
                );
                sendResponse(exchange, 200, json, "application/json");
                return;
            }

            currentAccount.withdraw(amount);
            String json = String.format(
                "{\"success\": true, \"message\": \"Withdrawn $%.2f successfully!\", \"balance\": %.2f}",
                amount,
                currentAccount.getBalance()
            );
            sendResponse(exchange, 200, json, "application/json");
        }
    }

    /**
     * POST /api/reset
     */
    static class ResetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            currentAccount = null;
            sendResponse(exchange, 200, "{\"success\": true, \"message\": \"Session reset.\"}", "application/json");
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String getJsonString(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static double getJsonDouble(String json, String key, double defaultVal) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"?([0-9]+(?:\\.[0-9]+)?)\"?");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException ignored) {}
        }
        return defaultVal;
    }

    private static String escape(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
