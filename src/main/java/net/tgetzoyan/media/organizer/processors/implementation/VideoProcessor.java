package net.tgetzoyan.media.organizer.processors.implementation;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QuickTimeDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class VideoProcessor extends Processor {
    private static final int QUICKTIME_CREATION_DATE = 256;

    private static Logger logger = LoggerFactory.getLogger(VideoProcessor.class);

    public VideoProcessor(String basePath, String hashCommand) {
        super(logger, basePath, hashCommand);
    }

    @Override
    protected LocalDateTime getDate(Path file) {
        Metadata metadata = null;

        try {
            metadata = ImageMetadataReader.readMetadata(file.toFile());
        } catch (IOException | ImageProcessingException e) {
            logger.error("Exception was thrown when extracting metadata for file: {}. Message: {}", file, e.getMessage());
            e.printStackTrace();
        }

        if (null != metadata) {
            logger.info("Metadata extracted for file {}", file);

            QuickTimeDirectory directory = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);
            if (null != directory) {
                Date date = directory.getDate(QUICKTIME_CREATION_DATE);

                if (null != date) {
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                }
            }
        }

        return null;
    }
}
