package com.librarydesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryDeskApplication {

    public static void main(String[] args) throws Exception {

        var context = SpringApplication.run(
                LibraryDeskApplication.class,
                args
        );

        LibraryService service = context.getBean(LibraryService.class);

        service.loadLibrary();
    }
}