package com.bookpass.bookpass.service;

import com.bookpass.bookpass.dto.request.AddBookRequest;
import com.bookpass.bookpass.dto.request.ReviewBookRequest;
import com.bookpass.bookpass.dto.response.BookResponse;
import com.bookpass.bookpass.entity.Book;
import com.bookpass.bookpass.entity.User;
import com.bookpass.bookpass.repository.BookRepository;
import com.bookpass.bookpass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final com.bookpass.bookpass.repository.TransactionRepository transactionRepository;

    /**
     * Student adds a book for sale
     */
    public BookResponse addBook(String userEmail, AddBookRequest request) {
        User seller = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = new Book();
        book.setSeller(seller);
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setPrice(request.getPrice());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setUniversity(request.getUniversity());
        book.setUniversity(request.getUniversity());
        book.setBookCondition("PENDING"); // Default condition until reviewed
        book.setBookImages(request.getBookImages());
        book.setStatus("PENDING"); // Needs review before being available

        // Auto-assign to University Reviewer
        if (request.getUniversity() != null) {
            String uniName = request.getUniversity();
            // Find allowed university from constants (simple case-insensitive match on name or acronym)
            java.util.Optional<com.bookpass.bookpass.constants.SaudiUniversities> match = java.util.Arrays.stream(com.bookpass.bookpass.constants.SaudiUniversities.values())
                    .filter(u -> u.getName().equalsIgnoreCase(uniName) || u.getAcronym().equalsIgnoreCase(uniName))
                    .findFirst();

            if (match.isPresent()) {
                String reviewerEmail = "reviewer@" + match.get().getEmailDomain();
                userRepository.findByEmail(reviewerEmail).ifPresent(book::setAssignedBookstore);
            }
        }

        bookRepository.save(book);

        return mapToBookResponse(book);
    }

    /**
     * Get all available books for home page
     * Only books that are AVAILABLE and reviewed by bookstore
     */
    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findAllAvailableBooks()
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all active books for marketplace
     */
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAllActiveBooks()
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get books by seller email
     */
    public List<BookResponse> getBooksBySellerEmail(String email) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookRepository.findBySeller_UserId(seller.getUserId())
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get books by seller
     */
    public List<BookResponse> getBooksBySeller(UUID sellerId) {
        return bookRepository.findBySeller_UserId(sellerId)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get pending books (for bookstore review)
     */
    public List<BookResponse> getPendingBooks() {
        return bookRepository.findByStatus("PENDING")
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get pending books for current Bookstore Reviewer
     */
    public List<BookResponse> getStorePendingBooks(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // If ADMIN, return all pending books
        if ("ADMIN".equals(user.getRole())) {
            return bookRepository.findByStatus("PENDING")
                    .stream()
                    .map(this::mapToBookResponse)
                    .collect(Collectors.toList());
        }

        return bookRepository.findByAssignedBookstore_UserIdAndStatusOrderByCreatedAtDesc(user.getUserId(), "PENDING")
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get sold books for current Bookstore Reviewer (with buyer info)
     */
    public List<BookResponse> getStoreSoldBooks(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // If ADMIN, return all sold books (COMPLETED transactions)
        if ("ADMIN".equals(user.getRole())) {
             return transactionRepository.findByStatusOrderByCreatedAtDesc("COMPLETED")
                .stream()
                .map(this::mapTransactionToBookResponse)
                .collect(Collectors.toList());
        }

        // Fetch transactions for this store's sold books
        return transactionRepository.findByBook_AssignedBookstore_UserIdAndBook_StatusOrderByCreatedAtDesc(user.getUserId(), "SOLD")
                .stream()
                .map(transaction -> mapTransactionToBookResponse(transaction))
                .collect(Collectors.toList());
    }

    /**
     * Review a book (Store Owner)
     */
    public BookResponse reviewBook(UUID bookId, String userEmail, ReviewBookRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Verify assignment (Allow ADMIN to bypass)
        boolean isAssigned = book.getAssignedBookstore() != null && book.getAssignedBookstore().getUserId().equals(user.getUserId());
        boolean isAdmin = "ADMIN".equals(user.getRole());

        if (!isAssigned && !isAdmin) {
             throw new RuntimeException("Not authorized to review this book");
        }

        book.setBookCondition(request.getCondition());
        book.setReviewNotes(request.getReviewNotes());
        book.setReviewedBy(user);
        book.setReviewedAt(java.time.LocalDateTime.now());
        
        // Once reviewed, it becomes AVAILABLE
        book.setStatus("AVAILABLE");

        bookRepository.save(book);
        return mapToBookResponse(book);
    }

    /**
     * Mark a book as PICKED (Store Owner) - for books that were SOLD
     */
    public BookResponse markAsPicked(UUID bookId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Verify assignment (Allow ADMIN to bypass)
        boolean isAssigned = book.getAssignedBookstore() != null && book.getAssignedBookstore().getUserId().equals(user.getUserId());
        boolean isAdmin = "ADMIN".equals(user.getRole());

        if (!isAssigned && !isAdmin) {
            throw new RuntimeException("Not authorized to manage this book");
        }

        // Only SOLD books can be picked up
        if (!"SOLD".equals(book.getStatus())) {
            throw new RuntimeException("Only SOLD books can be marked as PICKED");
        }

        book.setStatus("PICKED");
        book.setUpdatedAt(java.time.LocalDateTime.now());

        bookRepository.save(book);
        return mapToBookResponse(book);
    }

    /**
     * Search books by title
     */
    public List<BookResponse> searchByTitle(String query) {
        return bookRepository.searchByTitle(query)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search books by author
     */
    public List<BookResponse> searchByAuthor(String query) {
        return bookRepository.searchByAuthor(query)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search books by ISBN
     */
    public List<BookResponse> searchByIsbn(String query) {
        return bookRepository.searchByIsbn(query)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search books with filters
     */
    public List<BookResponse> searchBooks(String query, String searchType, String university) {
        List<Book> books;

        if (query != null && !query.isEmpty()) {
            switch (searchType != null ? searchType : "title") {
                case "author":
                    books = bookRepository.searchByAuthor(query);
                    break;
                case "isbn":
                    books = bookRepository.searchByIsbn(query);
                    break;
                default:
                    books = bookRepository.searchByTitle(query);
            }
        } else if (university != null && !university.isEmpty()) {
            books = bookRepository.findByUniversity(university);
        } else {
            books = bookRepository.findAllAvailableBooks();
        }

        // Filter by university if provided and query was used
        if (university != null && !university.isEmpty() && query != null && !query.isEmpty()) {
            books = books.stream()
                    .filter(b -> university.equals(b.getUniversity()))
                    .collect(Collectors.toList());
        }

        return books.stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map Book entity to BookResponse DTO
     */
    private BookResponse mapToBookResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setBookId(book.getBookId());
        response.setTitle(book.getTitle());
        response.setDescription(book.getDescription());
        response.setPrice(book.getPrice());
        response.setStatus(book.getStatus());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setUniversity(book.getUniversity());
        response.setBookImages(book.getBookImages());
        response.setBookCondition(book.getBookCondition());
        response.setReviewNotes(book.getReviewNotes());
        response.setSold("SOLD".equals(book.getStatus()));
        response.setSellerId(book.getSeller().getUserId());
        response.setSellerName(book.getSeller().getFirstName() + " " + book.getSeller().getLastName());
        response.setSellerPhone(book.getSeller().getPhoneNumber());
        response.setCreatedAt(book.getCreatedAt());

        // Note: Buyer info is no longer on the Book entity.
        // It is fetched via Transaction for specific endpoints.

        return response;
    }

    public BookResponse mapTransactionToBookResponse(com.bookpass.bookpass.entity.Transaction transaction) {
        BookResponse response = mapToBookResponse(transaction.getBook());
        // Override buyer info from transaction
        if (transaction.getBuyer() != null) {
            response.setBuyerId(transaction.getBuyer().getUserId());
            response.setBuyerName(transaction.getBuyer().getFirstName() + " " + transaction.getBuyer().getLastName());
            response.setBuyerPhone(transaction.getBuyer().getPhoneNumber());
        }
        return response;
    }
}