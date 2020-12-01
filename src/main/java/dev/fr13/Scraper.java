package dev.fr13;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Scraper {

    public static void main(String[] args) {
        SpringApplication.run(Scraper.class, args);
    }
}
