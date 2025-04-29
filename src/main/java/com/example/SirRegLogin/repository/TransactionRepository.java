package com.example.SirRegLogin.repository;

import com.example.SirRegLogin.model.TransactionLog;
import com.example.SirRegLogin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionLog, Long> {
    List<TransactionLog> findByAccountUser(User user);
}