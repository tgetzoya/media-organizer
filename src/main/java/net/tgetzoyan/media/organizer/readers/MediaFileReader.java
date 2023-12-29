package net.tgetzoyan.media.organizer.readers;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class MediaFileReader implements ItemReader<Path> {
    Logger logger = LoggerFactory.getLogger(MediaFileReader.class);

    private Path sourceDirectory;
    private Set<String> exclude;
    private List<Path> files;

    /* No default initialization */
    private MediaFileReader() {
    }

    public MediaFileReader(String sourceDirectory, Set<String> exclude) {
        this.sourceDirectory = Paths.get(sourceDirectory);
        this.exclude = exclude;
    }

    private boolean excludeFile(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        String extension = name.substring(name.lastIndexOf("."));

        return (this.exclude.contains(name) || this.exclude.contains(extension));
    }

    @PostConstruct
    private void populateInput() {
        /* Files.walk().parallel() does not actually make the stream parallel, because the underlying method for
         * parallelization needs to know how many elements are in a stream. By getting a list of all the file paths
         * first, the stream is already paralleled.
         */
        try {
            files = Files.walk(sourceDirectory)
                    .filter(Files::isRegularFile)
                    .filter(f -> !excludeFile(f))
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            logger.error("Exception on reading directory: {}", ex.getMessage());
            ex.printStackTrace();
            files = new ArrayList<>();
        }

        logger.info("On initial load, retrieved {} paths.", files.size());
    }


    @Override
    public Path read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return files.isEmpty() ? null : files.remove(0);
    }
}
