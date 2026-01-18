package com.bookpass.bookpass.service;

import com.bookpass.bookpass.dto.response.BookResponse;
import com.bookpass.bookpass.entity.Book;
import com.bookpass.bookpass.entity.Cart;
import com.bookpass.bookpass.entity.User;
import com.bookpass.bookpass.repository.BookRepository;
import com.bookpass.bookpass.repository.CartRepository;
import com.bookpass.bookpass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public void addToCart(String userEmail, UUID bookId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!"AVAILABLE".equals(book.getStatus())) {
            throw new RuntimeException("Book is not available for purchase");
        }
        
        // Prevent adding own book to cart?
        if (book.getSeller().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Cannot add your own book to cart");
        }

        if (cartRepository.existsByUserAndBook(user, book)) {
            // Already in cart, do nothing or throw exception?
            // Idempotent: just return
            return;
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setBook(book);
        cartRepository.save(cart);
    }

    public List<BookResponse> getCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Cart> cartItems = cartRepository.findByUser(user);

        // Filter out books that are no longer available (sold or picked)
        // And map to BookResponse
        return cartItems.stream()
                .map(Cart::getBook)
                .filter(book -> "AVAILABLE".equals(book.getStatus()))
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    public void removeFromCart(String userEmail, UUID bookId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Use findByUserAndBook to ensure we have the entity to delete
        // However, deleteByUserAndBook is easier if transaction is handled.
        // Spring Data JPA delete methods often require @Transactional
        Optional<Cart> cartItem = cartRepository.findByUserAndBook(user, book);
        cartItem.ifPresent(cartRepository::delete);
    }
    
    // Copying mapping logic from BookService to avoid dependency/refactoring
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

        return response;
    }
}
