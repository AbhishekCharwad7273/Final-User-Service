package com.UserService.Controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/digitalbooks/books/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DigitalBooksController {
    private final com.UserService.Services.BookService bookService;

    @GetMapping
    public ResponseEntity<List<com.UserService.Entity.Book>> retrieveAllBooks() {
        return ResponseEntity.ok(bookService.callBookServiceToGetAllBooks());
    }

    @GetMapping("/search")
    public ResponseEntity<List<com.UserService.Entity.Book>> searchQuery(@RequestParam String category, @RequestParam String title,   @RequestParam String publisher) {
        return ResponseEntity.ok(bookService.searchUsingQuery(category, title, publisher));
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<List<com.UserService.Entity.Book>> searchBooksByAuthorId(@PathVariable Long authorId) {
        return ResponseEntity.ok(bookService.searchBooksByAuthorId(authorId));
    }
}
