package com.librarydesk;
import com.librarydesk.model.Book;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.librarydesk.model.Student;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import com.librarydesk.model.IssueRecord;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LibraryTest {
    @Test
    void shouldAddBook() {

        LibraryService service = new LibraryService("test-library.json");

        Book book = new Book(
                "Java Programming",
                "James Gosling",
                "ISBN001",
                5
        );

        service.addBook(book);

        assertEquals(1, service.getAllBooks().size());
        assertEquals("Java Programming", service.getAllBooks().get(0).getTitle());
    }
    @Test
    void shouldAddStudent() {

        LibraryService service = new LibraryService("test-library.json");

        Student student = new Student(
                "S001",
                "Anusha"
        );

        service.addStudent(student);

        assertEquals(1, service.getAllStudents().size());
        assertEquals("S001", service.getAllStudents().get(0).getStudentId());
        assertEquals("Anusha", service.getAllStudents().get(0).getStudentName());
    }
    @Test
    void shouldIssueBook() {

        LibraryService service = new LibraryService("test-library.json");

        Book book = new Book(
                "Java Programming",
                "James Gosling",
                "ISBN001",
                5
        );

        Student student = new Student(
                "S001",
                "Anusha"
        );

        service.addBook(book);
        service.addStudent(student);

        service.issueBook(book, student);

        assertEquals(4, book.getCopiesAvailable());
        assertEquals(1, service.getAllIssueRecords().size());
    }
    @Test
    void shouldNotIssueBookWhenNoCopiesAvailable() {

        LibraryService service = new LibraryService("test-library.json");

        Book book = new Book(
                "Java Programming",
                "James Gosling",
                "ISBN001",
                0
        );

        Student student = new Student(
                "S001",
                "Anusha"
        );

        service.addBook(book);
        service.addStudent(student);

        assertThrows(
                IllegalStateException.class,
                () -> service.issueBook(book, student)
        );
    }
    @Test
    void shouldReturnBook() {

        LibraryService service = new LibraryService("test-library.json");
        Book book = new Book(
                "Java Programming",
                "James Gosling",
                "ISBN001",
                5
        );

        Student student = new Student(
                "S001",
                "Anusha"
        );

        service.addBook(book);
        service.addStudent(student);

        service.issueBook(book, student);

        double lateFee = service.returnBook(book, student);

        assertEquals(5, book.getCopiesAvailable());
        assertEquals(0, service.getAllIssueRecords().size());
        assertEquals(0.0, lateFee);
    }
    @Test
    void shouldCalculateLateFee() {

        LibraryService service = new LibraryService("test-library.json");

        Book book = new Book(
                "Java Programming",
                "James Gosling",
                "ISBN001",
                5
        );

        Student student = new Student(
                "S001",
                "Anusha"
        );

        service.addBook(book);
        service.addStudent(student);
        service.setLateFeePerDay(10.0);
        service.setLoanPeriodDays(14);

        service.issueBook(book, student);

        LocalDate returnDate = LocalDate.now().plusDays(17);

        double lateFee = service.returnBook(book, student, returnDate);

        assertEquals(30.0, lateFee);
    }

    @Test
    void shouldNotAllowDuplicateISBN() {

        LibraryService service = new LibraryService("test-library.json");

        Book firstBook = new Book(
                "Java Programming",
                "James Gosling",
                "ISBN001",
                5
        );

        Book secondBook = new Book(
                "Advanced Java",
                "James Gosling",
                "ISBN001",
                3
        );

        service.addBook(firstBook);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addBook(secondBook)
        );
    }



    @Test
    void shouldNotAllowDuplicateStudentId() {

        LibraryService service = new LibraryService("test-library.json");

        Student firstStudent = new Student("S010", "Anusha");
        Student secondStudent = new Student("S010", "Ravi");

        service.addStudent(firstStudent);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStudent(secondStudent)
        );
    }

    @Test
    void shouldNotAllowEmptyBookTitle() {

        LibraryService service = new LibraryService("test-library.json");

        Book book = new Book(
                "",
                "James Gosling",
                "ISBN005",
                5
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addBook(book)
        );
    }

    @Test
    void shouldNotAllowNegativeBookCopies() {

        LibraryService service = new LibraryService("test-library.json");

        Book book = new Book(
                "Java Programming",
                "James Gosling",
                "ISBN006",
                -1
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addBook(book)
        );
    }
    @Test
    void shouldNotAllowEmptyStudentId() {

        LibraryService service = new LibraryService("test-library.json");

        Student student = new Student(
                "",
                "Anusha"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStudent(student)
        );
    }


    @Test
    void shouldNotAllowEmptyStudentName() {

        LibraryService service = new LibraryService("test-library.json");

        Student student = new Student(
                "S006",
                ""
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addStudent(student)
        );
    }



    @Test
    void shouldSaveAndLoadLibrary() throws Exception {

        LibraryRepository repository = new LibraryRepository("test-library.json");

        Library library = new Library();

        Book book = new Book(
                "Java Programming",
                "James Gosling",
                "ISBN001",
                5
        );

        Student student = new Student(
                "S001",
                "Anusha"
        );

        library.getBooks().add(book);
        library.getStudents().add(student);

        repository.save(library);

        Path filePath = Path.of("test-library.json");

        assertTrue(Files.exists(filePath));

        Library loadedLibrary = repository.load();

        assertEquals(1, loadedLibrary.getBooks().size());
        assertEquals(1, loadedLibrary.getStudents().size());

        assertEquals(
                "Java Programming",
                loadedLibrary.getBooks().get(0).getTitle()
        );

        assertEquals(
                "S001",
                loadedLibrary.getStudents().get(0).getStudentId()
        );
    }

    @Test
    void shouldPersistIssueAndReturn() throws Exception {

        LibraryService service = new LibraryService("test-library.json");

        Book book = new Book(
                "Java Programming",
                "James Gosling",
                "ISBN002",
                5
        );

        Student student = new Student(
                "S002",
                "Test Student"
        );

        service.addBook(book);
        service.addStudent(student);

        service.issueBook(book, student);

        LibraryService loadedService =
                new LibraryService("test-library.json");

        loadedService.loadLibrary();

        assertEquals(
                1,
                loadedService.getAllIssueRecords().size()
        );

        assertEquals(
                4,
                loadedService.getAllBooks().get(0).getCopiesAvailable()
        );

        assertEquals(
                "ISBN002",
                loadedService.getAllIssueRecords()
                        .get(0)
                        .getBook()
                        .getIsbn()
        );

        assertEquals(
                "S002",
                loadedService.getAllIssueRecords()
                        .get(0)
                        .getStudent()
                        .getStudentId()
        );

        loadedService.returnBook(
                loadedService.getAllBooks().get(0),
                loadedService.getAllStudents().get(0)
        );

        LibraryService finalService =
                new LibraryService("test-library.json");

        finalService.loadLibrary();

        assertEquals(
                0,
                finalService.getAllIssueRecords().size()
        );

        assertEquals(
                5,
                finalService.getAllBooks().get(0).getCopiesAvailable()
        );
    }


    @Test
    void shouldSaveAndLoadUsingLibraryService() throws Exception {

        LibraryService service = new LibraryService("test-library.json");

        Book book = new Book(
                "Spring Boot",
                "Rod Johnson",
                "ISBN003",
                4
        );

        Student student = new Student(
                "S003",
                "Test Student"
        );

        service.addBook(book);
        service.addStudent(student);

        service.saveLibrary();

        LibraryService loadedService = new LibraryService("test-library.json");
        loadedService.loadLibrary();

        assertEquals(
                1,
                loadedService.getAllBooks().size()
        );

        assertEquals(
                1,
                loadedService.getAllStudents().size()
        );

        assertEquals(
                "Spring Boot",
                loadedService.getAllBooks().get(0).getTitle()
        );

        assertEquals(
                "S003",
                loadedService.getAllStudents().get(0).getStudentId()
        );
    }

}
