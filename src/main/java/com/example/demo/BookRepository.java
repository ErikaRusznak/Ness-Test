package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

//public interface BookRepository extends CrudRepository<Book, Long> {
public interface BookRepository extends JpaRepository<Book, Long> {
        List<Book> deleteByTitle(String title);
        Book findByTitle(String title);
}
