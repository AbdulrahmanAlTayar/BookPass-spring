package com.bookpass.bookpass.controller;

import com.bookpass.bookpass.dto.request.CartCheckoutRequest;
import com.bookpass.bookpass.dto.response.BookResponse;
import com.bookpass.bookpass.dto.response.CartCheckoutResponse;
import com.bookpass.bookpass.entity.Transaction;
import com.bookpass.bookpass.service.BookService;
import com.bookpass.bookpass.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final TransactionService transactionService;
    private final BookService bookService;

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
