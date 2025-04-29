package com.example.SirRegLogin.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // The type of transaction (deposit or withdrawal)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    // The amount involved
    @Column(nullable = false)
    private BigDecimal amount;
    
    // The date and time the transaction occurred
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();
    
    // The account related to this transaction
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
