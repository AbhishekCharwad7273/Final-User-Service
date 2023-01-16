package com.UserService.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.UserService.Entity.Book;

@RestController
@RequestMapping("/api/v1/digitalbooks/author/")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwt_token_security")
@CrossOrigin("*")
public class AuthorController {
    private final com.UserService.Services.BookService bookService;

    @PostMapping("/{authorId}/books")
    public ResponseEntity<Long> createBookByAuthor(@PathVariable Long authorId, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.addBook(authorId, book));
    }

    @GetMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<Long> toggleBookBlockStatus(@PathVariable Long authorId, @PathVariable Long bookId, @RequestParam boolean block) {
        return ResponseEntity.ok(bookService.toggleBookBlock(authorId, bookId, block));
    }

    @PutMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<Long> updateBook(@PathVariable Long authorId, @PathVariable Long bookId, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(authorId, bookId, book));
    }
}
