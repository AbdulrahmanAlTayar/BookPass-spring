package com.bookpass.bookpass.controller;

import com.bookpass.bookpass.dto.request.AddToCartRequest;
import com.bookpass.bookpass.dto.request.CartCheckoutRequest;
import com.bookpass.bookpass.dto.response.BookResponse;
import com.bookpass.bookpass.dto.response.CartCheckoutResponse;
import com.bookpass.bookpass.entity.Transaction;
import com.bookpass.bookpass.service.BookService;
import com.bookpass.bookpass.service.CartService;
import com.bookpass.bookpass.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final TransactionService transactionService;
    private final BookService bookService;
    private final CartService cartService;

    /**
     * Get user's cart
     */
    @GetMapping
    public ResponseEntity<List<BookResponse>> getCart(Principal principal) {
        return ResponseEntity.ok(cartService.getCart(principal.getName()));
    }

    /**
     * Add data to cart
     */
    @PostMapping
    public ResponseEntity<Void> addToCart(@RequestBody AddToCartRequest request, Principal principal) {
        cartService.addToCart(principal.getName(), request.getBookId());
        return ResponseEntity.ok().build();
    }

    /**
     * Remove from cart
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable UUID bookId, Principal principal) {
        cartService.removeFromCart(principal.getName(), bookId);
        return ResponseEntity.ok().build();
    }

    /**
     * Checkout cart - purchase multiple books with a single payment
     * 
     * @param request Contains list of book IDs and Moyasar payment ID
     * @param principal The authenticated buyer
     * @return CartCheckoutResponse with purchased books and total
     */
    @PostMapping("/checkout")
    public ResponseEntity<CartCheckoutResponse> checkout(
            @Valid @RequestBody CartCheckoutRequest request,
            Principal principal) {

        // Delegate to TransactionService
        List<Transaction> transactions = transactionService.checkoutCart(
                request.getBookIds(),
                principal.getName(),
                request.getPaymentId()
        );

        // Map transactions to book responses
        List<BookResponse> purchasedBooks = transactions.stream()
                .map(bookService::mapTransactionToBookResponse)
                .collect(Collectors.toList());

        // Calculate total amount
        BigDecimal totalAmount = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Build response
        CartCheckoutResponse response = new CartCheckoutResponse();
        response.setPurchasedBooks(purchasedBooks);
        response.setTotalAmount(totalAmount);
        response.setTotalBooks(purchasedBooks.size());
        response.setPaymentId(request.getPaymentId());
        response.setStatus("COMPLETED");

        return ResponseEntity.ok(response);
    }
}
