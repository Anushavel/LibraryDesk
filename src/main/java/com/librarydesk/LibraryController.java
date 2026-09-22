package com.librarydesk;

import com.librarydesk.model.Book;
import com.librarydesk.model.Student;
import org.springframework.web.bind.annotation.*;
import com.librarydesk.model.IssueRecord;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/books")
    public String addBook(@RequestBody Book book) {
        libraryService.addBook(book);

        try {
            libraryService.saveLibrary();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save library", e);
        }

        return "Book added successfully";
    }

    @GetMapping("/books")
    public List<Book> getBooks() {
        return libraryService.getAllBooks();
    }

    @PostMapping("/students")
    public String addStudent(@RequestBody Student student) {
        libraryService.addStudent(student);

        try {
            libraryService.saveLibrary();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save library", e);
        }

        return "Student added successfully";
    }
    @GetMapping("/students")
    public List<Student> getStudents() {
        return libraryService.getAllStudents();
    }

    @GetMapping("/issues")
    public List<IssueRecord> getIssueRecords() {
        return libraryService.getAllIssueRecords();
    }
    @PostMapping("/issue")
    public String issueBook(
            @RequestParam String isbn,
            @RequestParam String studentId) {

        Book book = libraryService.getAllBooks()
                .stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found"));

        Student student = libraryService.getAllStudents()
                .stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        libraryService.issueBook(book, student);

        return "Book issued successfully";
    }
    @PostMapping("/return")
    public String returnBook(
            @RequestParam String isbn,
            @RequestParam String studentId) {

        Book book = libraryService.getAllBooks()
                .stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found"));

        Student student = libraryService.getAllStudents()
                .stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        double lateFee = libraryService.returnBook(book, student);

        return "Book returned successfully. Late fee: " + lateFee;
    }
}
