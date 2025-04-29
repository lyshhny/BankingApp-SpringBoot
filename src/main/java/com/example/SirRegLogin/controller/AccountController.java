package com.example.SirRegLogin.controller;

import com.example.SirRegLogin.model.Account;
import com.example.SirRegLogin.model.AccountType;
import com.example.SirRegLogin.model.User;
import com.example.SirRegLogin.service.AccountService;
import com.example.SirRegLogin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Account Controller
 * Handles HTTP requests related to account operations including:
 * - Displaying account dashboard
 * - Processing money deposits
 * - Processing money withdrawals
 * 
 * Security:
 * - All endpoints require authentication
 * - Uses Spring Security's Authentication object to identify users
 * - Redirects to dashboard after operations
 */
@Controller
@RequestMapping("/account")
public class AccountController {

    /** Service for handling account-related business logic */
    @Autowired
    private AccountService accountService;

    /** Service for handling user-related business logic */
    @Autowired
    private UserService userService;

    /**
     * Displays the user's account dashboard
     * Shows current balance and account information
     *
     * @param authentication Spring Security authentication object
     * @param model Spring MVC model for view data
     * @return the dashboard view name
     */
    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        List<Account> accounts = accountService.getAllAccountsForUser(user);
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        model.addAttribute("accounts", accounts);
        model.addAttribute("accountTypes", AccountType.values());
        return "dashboard";
    }

    /**
     * Processes a money deposit request
     * Adds the specified amount to the user's account
     * @param accountType the type of account to deposit money into
     * @param amount the amount to deposit
     * @param authentication Spring Security authentication object
     * @return redirect to dashboard after successful deposit
     */
    @PostMapping("/add-money")
    public String addMoney(@RequestParam AccountType accountType, @RequestParam BigDecimal amount, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName());
        try {
            accountService.addMoney(user, accountType, amount);
        } catch (RuntimeException ex) {
            // Check if trying to add balance for the wrong account type
            if ("Account not found for the specified type".equals(ex.getMessage())) {
                redirectAttributes.addFlashAttribute("depositError",
                        "No " + accountType + " account found. Please create the account first.");
            } else {
                redirectAttributes.addFlashAttribute("depositError",
                        "An error occurred: " + ex.getMessage());
            }
            return "redirect:/account/dashboard";
        }
        return "redirect:/account/dashboard";
    }

    /**
     * Processes a money withdrawal request
     * Deducts the specified amount from the user's account
     * @param accountType the type of account to withdraw
     * @param amount the amount to withdraw
     * @param authentication Spring Security authentication object
     * @return redirect to dashboard after successful withdrawal
     */
    @PostMapping("/withdraw-money")
    public String withdrawMoney(@RequestParam AccountType accountType, @RequestParam BigDecimal amount, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName());
        try {
            accountService.withdrawMoney(user, accountType, amount);
        } catch (RuntimeException ex) {
            // Check if it's due to insufficient funds
            if ("Insufficient funds".equals(ex.getMessage())) {
                redirectAttributes.addFlashAttribute("withdrawError", 
                    "Insufficient funds: You cannot withdraw more than your current balance.");
            // Check if trying to remove balance for the wrong account type
            } else if ("Account not found for the specified type".equals(ex.getMessage())) {
                redirectAttributes.addFlashAttribute("withdrawError", 
                    "No " + accountType + " account found. Please create the account first.");
            } else {
                // Optionally handle any other errors.
                redirectAttributes.addFlashAttribute("withdrawError", 
                    "An error occurred: " + ex.getMessage());
            }
            return "redirect:/account/dashboard";
        }
        return "redirect:/account/dashboard";
    }

    /**
     * Processes the creation of a new type of account
     * Based on the option selected it adds the balance on the dashboard
     * @param accountType the type of account
     * @param authentication Spring Security authentication object
     * @return redirect to dashboard after successful withdrawal
     */
    @PostMapping("/create")
    public String createAccount(@RequestParam AccountType accountType,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName());
        try {
            accountService.createAccount(user, accountType);
        } catch (RuntimeException ex) {
            // Use flash attribute to pass the error message back to the dashboard.
            redirectAttributes.addFlashAttribute("accountCreationError", ex.getMessage());
        }
        return "redirect:/account/dashboard";
    }
} 