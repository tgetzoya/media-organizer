package net.tgetzoyan.media.organizer.processors;

import jakarta.annotation.PostConstruct;
import net.tgetzoyan.media.organizer.model.TransformData;
import net.tgetzoyan.media.organizer.processors.implementation.ImageProcessor;
import net.tgetzoyan.media.organizer.processors.implementation.VideoProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;

@Component
public class MediaFileProcessor implements ItemProcessor<Path, TransformData> {
    Logger logger = LoggerFactory.getLogger(MediaFileProcessor.class);

    @Value("${files.destination}")
    private String destinationPath;

    @Value("${file.photo.path}")
    private String photoPath;

    @Value("${file.video.path}")
    private String videoPath;

    @Value("${files.photo.types}")
    private Set<String> photoTypes;

    @Value("${files.video.types}")
    private Set<String> videoTypes;

    @Value("${hash.macos}")
    private String hashMacOS;

    @Value("${hash.unix}")
    private String hashUnix;

    private ImageProcessor imageProcessor;
    private VideoProcessor videoProcessor;

    /* These value fields will not be available to the constructor, so they must be set here. */
    @PostConstruct
    private void setProcessors() {
        String command = System.getProperty("os.name").toUpperCase(Locale.ROOT).contains("MAC") ? hashMacOS : hashUnix;

        this.imageProcessor = new ImageProcessor(Paths.get(destinationPath, photoPath).toString(), command);
        this.videoProcessor = new VideoProcessor(Paths.get(destinationPath, videoPath).toString(), command);
    }

    @Override
    public TransformData process(Path file) throws Exception {
        logger.info("Processing file: {}", file);
        String fileType = null;
        TransformData data = null;

        try {
            fileType = Files.probeContentType(file);
        } catch (IOException ex) {
            logger.error("Exception was triggered when probing file type: {}", ex.getMessage());
            ex.printStackTrace();
        }

        if (null != fileType) {
            fileType = fileType.toLowerCase(Locale.ROOT);

            if (photoTypes.contains(fileType)) {
                data = this.imageProcessor.process(file);
            } else if (videoTypes.contains(fileType)) {
                data = this.videoProcessor.process(file);
            } else {
                logger.error("Was not able to properly parse file {} of type {}. File will not be moved.", file, fileType);
            }
        } else {
            logger.error("Unknown file type for file {}. File will not be moved.", file);
        }

        return data;
    }
}
