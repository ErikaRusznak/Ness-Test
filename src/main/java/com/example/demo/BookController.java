package com.example.demo;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class BookController {

    private final BookRepository repository;
    private final BookModelAssembler assembler;
    public BookController(BookRepository repository, BookModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    // ex 1
    // add new book
    @PostMapping("/addNewBook")
    ResponseEntity<?> newBook() {
        Scanner scanner = new Scanner(System.in); // creating scanner object
        System.out.println("Enter title for the new book: ");
        String title = scanner.nextLine();
        System.out.println("Enter author for the new book");
        String author = scanner.nextLine();

        ExampleMatcher titleMatcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("title", ignoreCase());
        Book newBook = new Book(title, author);

        EntityModel<Book> entityModel = assembler.toModel(repository.save(newBook));
            return ResponseEntity //
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                    .body(entityModel);

    }

    // ex 2
    // get all books sorted by author and title
    @GetMapping("/books")
    CollectionModel<EntityModel<Book>> all() {
        List<EntityModel<Book>> books = repository.findAll(Sort.by("author").ascending().and(Sort.by("title")))
                .stream()
                .map(assembler::toModel).collect(Collectors.toList());

        return CollectionModel.of(books, linkTo(methodOn(BookController.class).all()).withSelfRel());

    }

    // ex 3
    // delete book by title
    @DeleteMapping("/deleteBooks")
    ResponseEntity<?> deleteBook() {
        Scanner scanner = new Scanner(System.in); // creating scanner object
        System.out.println("Enter title of the book you want to delete: ");
        String title = scanner.nextLine();
        repository.deleteByTitle(title);
        return ResponseEntity.noContent().build();
    }

    // ex 4
    // search by title
    @GetMapping("/searchBooks")
    EntityModel<Book> search() {

        Scanner scanner = new Scanner(System.in); // creating scanner object
        System.out.println("Enter title of the book you want to search: ");
        String title = scanner.nextLine();

        Book book = repository.findByTitle(title);
                // .orElseThrow(() -> new BookNotFoundByTitleException(title));

        return assembler.toModel(book);
    }

    // Single item
    // EntityModel<T> is a generic container from Spring HATEOAS that includes not only the data but a collection of links.
    @GetMapping("/books/{id}")
    EntityModel<Book> one(@PathVariable Long id) {

        Book book = repository.findById(id) //
                .orElseThrow(() -> new BookNotFoundException(id));

        return assembler.toModel(book);
    }

//    @PutMapping("/books/{title}")
//    ResponseEntity<?> replaceBook(@PathVariable String title) {
//
//        Scanner scanner = new Scanner(System.in); // creating scanner object
//        System.out.println("Enter new author of the book you want to update: ");
//        String author = scanner.nextLine();
//
//        Book updatedBook = repository.findByTitle(title)
//                .map(book -> {
//                    book.setTitle(title);
//                    book.setAuthor(author);
//                    return repository.save(book);
//                })
//                .orElseGet(() -> {
//                    newBook.setId(id);
//                    return repository.save(newBook);
//                });
//        EntityModel<Book> entityModel = assembler.toModel(updatedBook);
//
//        return ResponseEntity //
//                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
//                .body(entityModel);
//    }
}
