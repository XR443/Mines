package com.github.raspopov;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MinesMain {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MinesMain.class).headless(false).run(args);
        // Throws headless exception
        // ApplicationContext context = SpringApplication.run(Mines.class);
    }
}
