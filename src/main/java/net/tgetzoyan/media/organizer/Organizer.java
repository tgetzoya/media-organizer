package net.tgetzoyan.media.organizer;

import net.tgetzoyan.media.organizer.processors.ImageProcessor;
import net.tgetzoyan.media.organizer.processors.VideoProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Organizer {
    private static final String PHOTOS = "Photos";
    private static final String VIDEOS = "Videos";

    private static Logger logger = LoggerFactory.getLogger(Organizer.class);

    public Path sourceBase;
    public Path destinationBase;

    private Path photosPath;
    private Path videosPath;

    public Organizer(Path sourceBase, Path destinationBase) {
        this.sourceBase = sourceBase;
        this.destinationBase = destinationBase;
    }

    public void run() {
        logger.info("Running organization.");

        try {
            createDestinationDirectories();
            Files.walk(sourceBase).parallel().filter(Files::isRegularFile).forEach(this::process);
        } catch (IOException ex) {
            logger.error("Exception was triggered: {}", ex.getMessage());
            ex.printStackTrace();
        }

        logger.info("Finished running organization.");
    }

    private void createDestinationDirectories() throws IOException {
        photosPath = Paths.get(destinationBase.toString(), PHOTOS);
        videosPath = Paths.get(destinationBase.toString(), VIDEOS);

        if (Files.notExists(photosPath)) {
            logger.info("Directory {} does not exist, creating.", photosPath);
            Files.createDirectory(photosPath);
        }

        if (Files.notExists(videosPath)) {
            logger.info("Directory {} does not exist, creating.", videosPath);
            Files.createDirectory(videosPath);
        }
    }

    private void process(Path file) {
        logger.info("Processing file: {}", file);
        String fileType = null;

        try {
            fileType = Files.probeContentType(file);
        } catch (IOException ioex) {
            logger.error("Exception was triggered when probing file type: {}", ioex.getMessage());
            ioex.printStackTrace();
        }

        if (null != fileType) {
            fileType = fileType.toLowerCase(Locale.ROOT);
        }

        if (ImageProcessor.TYPES.contains(fileType)) {
            (new ImageProcessor(file, photosPath.toString())).process();
        } else if (VideoProcessor.TYPES.contains(fileType)) {
            (new VideoProcessor(file, videosPath.toString())).process();
        } else {
            logger.info("Unhandled/Unknown file type {}. File will be ignored.", fileType);
        }
    }
}
