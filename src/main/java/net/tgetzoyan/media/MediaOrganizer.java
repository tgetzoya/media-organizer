package net.tgetzoyan.media;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MediaOrganizer {
    private static Logger logger = LoggerFactory.getLogger(MediaOrganizer.class);

    public static void main(String[] args) {
        logger.info("Starting Application");
        System.exit(SpringApplication.exit(SpringApplication.run(MediaOrganizer.class, args)));
    }
}
