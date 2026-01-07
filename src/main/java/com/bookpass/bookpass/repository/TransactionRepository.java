package com.bookpass.bookpass.repository;

import com.bookpass.bookpass.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByBuyer_UserId(UUID buyerId);
    List<Transaction> findBySeller_UserId(UUID sellerId);
    List<Transaction> findByBook_BookId(UUID bookId);
    
    // For Dashboard: Find transactions where the book is assigned to this bookstore and status is SOLD
    List<Transaction> findByBook_AssignedBookstore_UserIdAndBook_StatusOrderByCreatedAtDesc(UUID bookstoreId, String status);
}
