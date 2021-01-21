package com.example.springsocial.controller;


import com.example.springsocial.exception.ResourceNotFoundException;
import com.example.springsocial.model.Book;
import com.example.springsocial.model.User;
import com.example.springsocial.repository.BookRepository;
import com.example.springsocial.repository.UserRepository;
import com.example.springsocial.security.CurrentUser;
import com.example.springsocial.security.UserPrincipal;
import com.example.springsocial.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
/*@RequestMapping("book")*/
public class BookController {
    private final BookRepository bookRepository;
    private UserRepository userRepository;
    private BookService bookService;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    public BookController(BookRepository bookRepository, UserRepository userRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
    }

    @GetMapping
    public Iterable<Book> booksList() {
        return bookRepository.findAll();
    }
    @GetMapping("book/getAllUserBooks")
    public List<Book> getAllUserBooks(@CurrentUser UserPrincipal userPrincipal) {
        System.out.println("BOOK - > " + bookService.getAllUserBooks(userPrincipal.getId()));

        return bookService.getAllUserBooks(userPrincipal.getId());
    }

    @GetMapping("{id}")
    public Book getBook(@PathVariable Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book id", "id", id));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/book")
    public Book addBook(@RequestBody Book book, @CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User id", "id", userPrincipal.getId()));
        book.setBookOwner(user);
        return bookRepository.save(book);
    }

    @PutMapping("{id}")
    public Book updateBook(@PathVariable("id") Book bookFromDB, @RequestBody Book bookFromUser) {
        BeanUtils.copyProperties(bookFromUser, bookFromDB, "id");
        return bookRepository.save(bookFromUser);
    }

    @DeleteMapping("{id}")
    public void deleteBook(@PathVariable("id") Book book) {
        bookRepository.delete(book);
    }
}
