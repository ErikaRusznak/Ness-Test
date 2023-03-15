package com.example.demo;

public class BookNotFoundByTitleException extends RuntimeException {
    BookNotFoundByTitleException(String title) {
        super("Could not find book with title " + title);
    }
}
