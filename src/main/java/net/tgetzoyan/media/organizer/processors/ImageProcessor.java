package net.tgetzoyan.media.organizer.processors;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ImageProcessor extends Processor {
    public static final List<String> TYPES = Arrays.asList("image/gif", "image/heic", "image/jpeg", "image/png");

    private static Logger logger = LoggerFactory.getLogger(ImageProcessor.class);

    public ImageProcessor(Path file, String basePath) {
        super(logger, file, basePath);
    }

    @Override
    protected LocalDateTime getDate() {
        Metadata metadata = null;

        try {
            metadata = ImageMetadataReader.readMetadata(file.toFile());
        } catch (IOException | ImageProcessingException e) {
            logger.error("Exception was thrown when extracting metadata for file: {}", file);
            e.printStackTrace();
        }

        if (null != metadata) {
            logger.info("Metadata extracted for file {}", file);

            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (null != directory) {
                Date date = null;

                if (null != directory.getDateOriginal()) {
                    date = directory.getDateOriginal();
                } else if (null != directory.getDateDigitized()) {
                    date = directory.getDateDigitized();
                } else if (null != directory.getDateModified()) {
                    date = directory.getDateModified();
                }

                if (null != date) {
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                }
            }
        }

        return null;
    }
}
