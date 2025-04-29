package com.example.SirRegLogin.controller;

import com.example.SirRegLogin.model.TransactionLog;
import com.example.SirRegLogin.model.User;
import com.example.SirRegLogin.service.TransactionService;
import com.example.SirRegLogin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/account")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping("/transactions")
    public String showTransactionHistory(Authentication authentication, Model model) {
        // Get the logged-in user.
        User user = userService.findByUsername(authentication.getName());
        // Retrieve all transactions for this user's accounts.
        List<TransactionLog> transactions = transactionService.getTransactionsForUser(user);
        model.addAttribute("transactions", transactions);
        return "transactionHistory";  // This corresponds to transactionHistory.html in your templates.
    }
}
