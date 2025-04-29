package com.example.SirRegLogin.service;

import com.example.SirRegLogin.model.Account;
import com.example.SirRegLogin.model.TransactionLog;
import com.example.SirRegLogin.model.TransactionType;
import com.example.SirRegLogin.model.User;
import com.example.SirRegLogin.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    // Logs a single transaction (already used in your deposits/withdrawals)
    public TransactionLog logTransaction(Account account, BigDecimal amount, TransactionType type) {
        TransactionLog transaction = new TransactionLog();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTransactionDate(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    // Get all transaction logs for a user
    public List<TransactionLog> getTransactionsForUser(User user) {
        return transactionRepository.findByAccountUser(user);
    }
}