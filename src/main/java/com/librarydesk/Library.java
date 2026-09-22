package com.librarydesk;

import com.librarydesk.model.Book;
import com.librarydesk.model.IssueRecord;
import com.librarydesk.model.Student;

import java.util.ArrayList;
import java.util.List;

public class Library {

    private List<Book> books;
    private List<Student> students;
    private List<IssueRecord> issueRecords;
    private double lateFeePerDay;
    private int loanPeriodDays;

    public Library() {
        this.books = new ArrayList<>();
        this.students = new ArrayList<>();
        this.issueRecords = new ArrayList<>();
        this.loanPeriodDays = 14;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<IssueRecord> getIssueRecords() {
        return issueRecords;
    }

    public void setIssueRecords(List<IssueRecord> issueRecords) {
        this.issueRecords = issueRecords;
    }
    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }

    public void setLoanPeriodDays(int loanPeriodDays) {
        this.loanPeriodDays = loanPeriodDays;
    }

    @Override
    public String toString() {
        return "Library{" +
                "books=" + books +
                ", students=" + students +
                ", issueRecords=" + issueRecords +
                '}';
    }
    public double getLateFeePerDay() {
        return lateFeePerDay;
    }

    public void setLateFeePerDay(double lateFeePerDay) {
        this.lateFeePerDay = lateFeePerDay;
    }


}