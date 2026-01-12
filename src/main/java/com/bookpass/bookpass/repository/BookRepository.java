package com.bookpass.bookpass.repository;

import com.bookpass.bookpass.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    // الكتب حسب الحالة
    List<Book> findByStatus(String status);

    // كتب البائع (الطالب)
    List<Book> findBySeller_UserId(UUID sellerId);

    // كتب مخصصة لمكتبة معينة (للمراجعة)
    List<Book> findByAssignedBookstore_UserIdAndStatusOrderByCreatedAtDesc(UUID bookstoreId, String status);

    // الكتب المتاحة والمراجعة للـ Home Page (مرتبة من الأحدث)
    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' AND b.bookCondition IS NOT NULL ORDER BY b.createdAt DESC")
    List<Book> findAvailableReviewedBooks();

    // جميع الكتب المتاحة (بدون شرط المراجعة - للـ Marketplace)
    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' ORDER BY b.createdAt DESC")
    List<Book> findAllAvailableBooks();

    // جميع الكتب (للعرض في الـ Marketplace)
    @Query("SELECT b FROM Book b WHERE b.status != 'DELETED' ORDER BY b.createdAt DESC")
    List<Book> findAllActiveBooks();

    // البحث بالعنوان
    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' AND LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY b.createdAt DESC")
    List<Book> searchByTitle(@Param("query") String query);

    // البحث بالمؤلف
    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' AND LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY b.createdAt DESC")
    List<Book> searchByAuthor(@Param("query") String query);

    // البحث بـ ISBN
    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' AND b.isbn LIKE CONCAT('%', :query, '%') ORDER BY b.createdAt DESC")
    List<Book> searchByIsbn(@Param("query") String query);

    // البحث بالجامعة
    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' AND b.university = :university ORDER BY b.createdAt DESC")
    List<Book> findByUniversity(@Param("university") String university);

    // البحث المتقدم
    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' " +
           "AND (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
           "AND (:university IS NULL OR b.university = :university) " +
           "ORDER BY b.createdAt DESC")
    List<Book> advancedSearch(@Param("title") String title,
                              @Param("author") String author,
                              @Param("university") String university);
}