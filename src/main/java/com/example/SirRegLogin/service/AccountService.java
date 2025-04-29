package com.example.SirRegLogin.service;

import com.example.SirRegLogin.model.Account;
import com.example.SirRegLogin.model.AccountType;
import com.example.SirRegLogin.model.TransactionType;
import com.example.SirRegLogin.model.User;
import com.example.SirRegLogin.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
/**
 * Account Service
 * Handles business logic for account-related operations including:
 * - Account creation and retrieval
 * - Money deposits and withdrawals
 * - Balance management
 * - Transaction log
 * 
 * Features:
 * 1. Automatic account creation for new users
 * 2. Transaction management with @Transactional annotation
 * 3. Balance validation for withdrawals
 * 4. Many-to-one relationship with User entity
 * 
 * Security:
 * - All operations are transactional
 * - Prevents negative balance withdrawals
 * - Throws RuntimeException for insufficient funds
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;  // Repository for database operations

    @Autowired
    private TransactionService transactionService;  // Service for transaction log

    @Transactional
    public Account createAccount(User user, AccountType accountType) {  // Creates a new account for a user
        if (accountRepository.findByUserAndAccountType(user, accountType).isPresent()) {
            throw new RuntimeException("This Type of account already exists");
        }
        Account account = new Account();  // Initialize new account instance
        account.setUser(user);  // Set the user for the account
        account.setAccountType(accountType);
        return accountRepository.save(account);  // Save and return the account
    }

    public Account getAccount(User user, AccountType accountType) {  // Retrieves existing account or creates new one
        return accountRepository.findByUserAndAccountType(user, accountType)  // Try to find existing account
        .orElseThrow(() -> new RuntimeException("Account not found for the specified type"));  // Create new if not found
    }

    public List<Account> getAllAccountsForUser(User user) {
        return accountRepository.findAllByUser(user);
    }

    @Transactional
    public Account addMoney(User user, AccountType accountType, BigDecimal amount) {  // Adds money to user's account
        Account account = getAccount(user, accountType);  // Fetch the specific account
        account.setBalance(account.getBalance().add(amount));  // Add amount to balance
        Account updatedAccount = accountRepository.save(account); // Saves the update
        transactionService.logTransaction(updatedAccount, amount, TransactionType.DEPOSIT); // Log the deposit transaction
        return updatedAccount;
    }

    @Transactional
    public Account withdrawMoney(User user, AccountType accountType, BigDecimal amount) {  // Withdraws money from account
        Account account = getAccount(user, accountType);  // Fetch the specific account
        if (account.getBalance().compareTo(amount) < 0) {  // Check if sufficient funds
            throw new RuntimeException("Insufficient funds");  // Throw exception if insufficient
        }
        account.setBalance(account.getBalance().subtract(amount));  // Subtract amount from balance
        Account updatedAccount = accountRepository.save(account); // Saves updated account
        transactionService.logTransaction(updatedAccount, amount, TransactionType.WITHDRAWAL); // Log the withdrawn transaction
        return updatedAccount;
    }
} 