package com.librarydesk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class LibraryRepository {

    private ObjectMapper objectMapper;
    private String filePath;

    public LibraryRepository() {
        this("library.json");
    }

    public LibraryRepository(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void save(Library library) throws Exception {
        objectMapper.writeValue(new java.io.File(filePath), library);
    }

    public Library load() throws Exception {
        java.io.File file = new java.io.File(filePath);

        if (!file.exists()) {
            return new Library();
        }

        return objectMapper.readValue(file, Library.class);
    }
}