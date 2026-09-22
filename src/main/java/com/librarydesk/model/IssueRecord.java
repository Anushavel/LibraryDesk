package com.librarydesk.model;

import java.time.LocalDate;

public class IssueRecord {

    private Book book;
    private Student student;
    private LocalDate issueDate;
    private LocalDate dueDate;

    public IssueRecord() {
    }

    public IssueRecord(Book book, Student student, LocalDate issueDate, LocalDate dueDate) {
        this.book = book;
        this.student = student;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "IssueRecord{" +
                "book=" + book +
                ", student=" + student +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                '}';
    }
}
