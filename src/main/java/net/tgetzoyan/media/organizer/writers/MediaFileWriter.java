package net.tgetzoyan.media.organizer.writers;

import net.tgetzoyan.media.organizer.model.TransformData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.io.IOException;
import java.nio.file.Files;

public class MediaFileWriter implements ItemWriter<TransformData> {
    Logger logger = LoggerFactory.getLogger(MediaFileWriter.class);

    @Override
    public void write(Chunk<? extends TransformData> chunk) {
        chunk.forEach(c -> {
            logger.info("Copying file {} to {}", c.getSourcePath(), c.getDestinationPath());
            try {
                if (!Files.isDirectory(c.getDestinationPath().getParent())) {
                    Files.createDirectories(c.getDestinationPath().getParent());
                }

                if (!c.destinationFileExists() && Files.notExists(c.getDestinationPath())) {
                    Files.copy(c.getSourcePath(), c.getDestinationPath());
                } else {
                    logger.error("This file already exists. Removing original.");
                }

                Files.deleteIfExists(c.getSourcePath());
            } catch (IOException ex) {
                logger.error("Could not copy file {} to {}. Reason: {}",
                        c.getSourcePath(),
                        c.getDestinationPath(),
                        ex.getMessage()
                );
                ex.printStackTrace();
            }
        });
    }
}
