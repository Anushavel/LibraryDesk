package com.librarydesk;

import com.librarydesk.model.Book;
import com.librarydesk.model.Student;
import com.librarydesk.model.IssueRecord;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service
public class LibraryService {

    private Library library;

    private LibraryRepository repository;

    public LibraryService() {
        this.library = new Library();
        this.repository = new LibraryRepository();
    }

    public LibraryService(String filePath) {
        this.library = new Library();
        this.repository = new LibraryRepository(filePath);
    }

    public void loadLibrary() throws Exception {
        this.library = repository.load();
    }

    public void addBook(Book book) {

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }

        if (book.getCopiesAvailable() < 0) {
            throw new IllegalArgumentException("Copies available cannot be negative.");
        }

        for (Book existingBook : library.getBooks()) {

            if (existingBook.getIsbn().equals(book.getIsbn())) {
                throw new IllegalArgumentException(
                        "Book with this ISBN already exists."
                );
            }
        }

        library.getBooks().add(book);
    }

    public void addStudent(Student student) {

        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }

        if (student.getStudentName() == null || student.getStudentName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }

        for (Student existingStudent : library.getStudents()) {
            if (existingStudent.getStudentId().equals(student.getStudentId())) {
                throw new IllegalArgumentException("Student with this ID already exists.");
            }
        }

        library.getStudents().add(student);
    }

    public List<Book> getAllBooks() {
        return library.getBooks();
    }

    public List<Student> getAllStudents() {
        return library.getStudents();
    }

    public List<IssueRecord> getAllIssueRecords() {
        return library.getIssueRecords();
    }

    public void setLateFeePerDay(double lateFeePerDay) {
        library.setLateFeePerDay(lateFeePerDay);
    }

    public void setLoanPeriodDays(int loanPeriodDays) {
        library.setLoanPeriodDays(loanPeriodDays);
    }

    public void issueBook(Book book, Student student) {

        if (book.getCopiesAvailable() <= 0) {
            throw new IllegalStateException(
                    "No copies available for this book."
            );
        }

        book.setCopiesAvailable(
                book.getCopiesAvailable() - 1
        );

        LocalDate issueDate = LocalDate.now();

        LocalDate dueDate = issueDate.plusDays(
                library.getLoanPeriodDays()
        );

        IssueRecord issueRecord = new IssueRecord(
                book,
                student,
                issueDate,
                dueDate
        );

        library.getIssueRecords().add(issueRecord);

        try {
            saveLibrary();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save library", e);
        }
    }

    public double returnBook(Book book, Student student) {
        return returnBook(book, student, LocalDate.now());
    }

    public double returnBook(
            Book book,
            Student student,
            LocalDate returnDate) {

        for (IssueRecord record : library.getIssueRecords()) {

            if (record.getBook().getIsbn().equals(book.getIsbn())
                    && record.getStudent().getStudentId().equals(student.getStudentId())) {

                book.setCopiesAvailable(
                        book.getCopiesAvailable() + 1
                );

                long daysLate = ChronoUnit.DAYS.between(
                        record.getDueDate(),
                        returnDate
                );

                double lateFee = 0.0;

                if (daysLate > 0) {
                    lateFee = daysLate
                            * library.getLateFeePerDay();
                }

                library.getIssueRecords().remove(record);

                try {
                    saveLibrary();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to save library", e);
                }

                return lateFee;
            }
        }

        throw new IllegalArgumentException(
                "No issue record found for this book and student."
        );

    }
    public void saveLibrary() throws Exception {
        repository.save(library);
    }

}