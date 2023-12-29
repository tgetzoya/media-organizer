package net.tgetzoyan.media.organizer.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class JobListener implements JobExecutionListener {
    Logger logger = LoggerFactory.getLogger(JobListener.class);

    @Value("${files.source}")
    private String sourceDirectory;

    @Value("${files.destination}")
    private String destinationDirectory;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Source directory set to {}", sourceDirectory);
        logger.info("Destination base directory set to {}", destinationDirectory);

        Path sourcePath = Paths.get(sourceDirectory);
        Path destinationPath = Paths.get(destinationDirectory);

        if (!Files.isDirectory(sourcePath) || !Files.isReadable(sourcePath)) {
            logger.error("Source directory must exist and be readable.");
        } else if (!Files.isDirectory(destinationPath) || !Files.isWritable(destinationPath)) {
            logger.error("Destination directory either does not exist or is not writeable.");
        } else {
            logger.info("Directories have been vetted.");
        }

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Job finished: {}", jobExecution.getEndTime());
    }
}
