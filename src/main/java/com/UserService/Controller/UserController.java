package com.UserService.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.UserService.Entity.BookSubscription;
import com.UserService.Entity.User;
import com.UserService.Exception.UserException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/digitalbooks/user/")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwt_token_security")
@CrossOrigin(origins = "*")
public class UserController {
    private final com.UserService.Services.UserService userService;
    private final com.UserService.Services.SubscriptionService subscriptionService;

    @GetMapping("/subscribe/{bookId}/{userId}")
    public ResponseEntity<Long> subscribeUserToBook(@PathVariable Long bookId, @PathVariable Long userId) {
        Long sizeOfSubscription = subscriptionService.subscribeToBook(userId, bookId);
        return ResponseEntity.ok(sizeOfSubscription);
    }

    @GetMapping("/unsubscribe/{bookId}/{userId}")
    public ResponseEntity<Long> unSubscribeUserToBook(@PathVariable Long bookId, @PathVariable Long userId) {
        Long sizeOfSubscription = subscriptionService.unSubscribeToBook(userId, bookId);
        return ResponseEntity.ok(sizeOfSubscription);
    }

    @GetMapping("/subscribe/{userId}")
    public ResponseEntity<List<BookSubscription>> retrieveSubscribedBookList(@PathVariable Long userId) {
        List<BookSubscription> sizeOfSubscription = subscriptionService.retrieveSubscribedBooksForUser(userId);
        return ResponseEntity.ok(sizeOfSubscription);
    }

    @CrossOrigin
    @GetMapping("/readers/{emailId}/books/{subscriptionId}")
    public ResponseEntity<BookSubscription> fetchSubscribedBookData(@PathVariable String emailId,@PathVariable Long subscriptionId){
        User user = userService.retrieveUserByEmail(emailId).orElseThrow(() -> new UserException("Read Data Error"));
        BookSubscription subscription = subscriptionService.retrieveSubscribedBooksForUser(user.getId()).stream()
                .filter(bookSubscription -> Objects.equals(bookSubscription.getSubscription().getId(), subscriptionId))
                .findFirst()
                .orElseThrow(() -> new UserException("Read Data Error"));
        return ResponseEntity.ok(subscription);
    }

    @CrossOrigin
    @GetMapping("/readers/{emailId}/books/{subscriptionId}/read")
    public ResponseEntity<String> fetchSubscribedBookContent(@PathVariable String emailId,@PathVariable Long subscriptionId){
        User user = userService.retrieveUserByEmail(emailId).orElseThrow(() -> new UserException(" Email is Invalid"));
        BookSubscription subscription = subscriptionService.retrieveSubscribedBooksForUser(user.getId()).stream()
                .filter(bookSubscription -> Objects.equals(bookSubscription.getSubscription().getId(), subscriptionId))
                .findFirst()
                .orElseThrow(() -> new UserException("Subscription Id is invalid"));
        return ResponseEntity.ok(subscription.getBook().getContent());
    }
}
