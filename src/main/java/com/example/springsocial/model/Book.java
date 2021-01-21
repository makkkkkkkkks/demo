package com.example.springsocial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "books")
@Data
@ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bookName;
    private String bookAuthor;
    private String publishingHouse;
    private String bookLanguage;
    private String bookFormat;
    private long bookISBN;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "users_id")
    private User bookOwner;

    public Book() {
    }
}