package com.bank;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BankServlet handles REST API requests for Tomcat WAR deployment.
 */
@WebServlet(urlPatterns = {"/api/account", "/api/create", "/api/deposit", "/api/withdraw", "/api/reset"})
public class BankServlet extends HttpServlet {
    private static BankAccount currentAccount = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String path = req.getServletPath();
        if ("/api/account".equals(path)) {
            if (currentAccount == null) {
                out.print("{\"hasAccount\": false}");
            } else {
                out.printf("{\"hasAccount\": true, \"accountNumber\": \"%s\", \"accountHolder\": \"%s\", \"balance\": %.2f}",
                        escape(currentAccount.getAccountNumber()),
                        escape(currentAccount.getAccountHolder()),
                        currentAccount.getBalance());
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\": \"Not Found\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String path = req.getServletPath();
        String body = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        switch (path) {
            case "/api/create" -> {
                String accNum = getJsonString(body, "accountNumber");
                String accHolder = getJsonString(body, "accountHolder");
                double initialBalance = getJsonDouble(body, "initialBalance", 0.0);

                if (accNum == null || accNum.isBlank() || accHolder == null || accHolder.isBlank()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"Account number and holder name are required.\"}");
                    return;
                }

                currentAccount = new BankAccount(accNum.trim(), accHolder.trim(), Math.max(0.0, initialBalance));
                out.printf("{\"success\": true, \"message\": \"Account created successfully for %s!\", \"balance\": %.2f}",
                        escape(currentAccount.getAccountHolder()),
                        currentAccount.getBalance());
            }
            case "/api/deposit" -> {
                if (currentAccount == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"No active account found. Please create an account first.\"}");
                    return;
                }
                double amount = getJsonDouble(body, "amount", -1);
                if (amount <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"Invalid deposit amount! Amount must be greater than 0.\"}");
                    return;
                }
                currentAccount.deposit(amount);
                out.printf("{\"success\": true, \"message\": \"Deposited $%.2f successfully!\", \"balance\": %.2f}",
                        amount, currentAccount.getBalance());
            }
            case "/api/withdraw" -> {
                if (currentAccount == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"No active account found. Please create an account first.\"}");
                    return;
                }
                double amount = getJsonDouble(body, "amount", -1);
                if (amount <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"Invalid withdrawal amount! Amount must be greater than 0.\"}");
                    return;
                }
                if (amount > currentAccount.getBalance()) {
                    out.printf("{\"success\": false, \"message\": \"Insufficient balance! Withdrawal of $%.2f failed.\", \"balance\": %.2f}",
                            amount, currentAccount.getBalance());
                    return;
                }
                currentAccount.withdraw(amount);
                out.printf("{\"success\": true, \"message\": \"Withdrawn $%.2f successfully!\", \"balance\": %.2f}",
                        amount, currentAccount.getBalance());
            }
            case "/api/reset" -> {
                currentAccount = null;
                out.print("{\"success\": true, \"message\": \"Session reset.\"}");
            }
            default -> {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Not Found\"}");
            }
        }
    }

    private static String getJsonString(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) return matcher.group(1);
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
