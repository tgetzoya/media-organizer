package net.tgetzoyan.media;

import net.tgetzoyan.media.organizer.listeners.JobListener;
import net.tgetzoyan.media.organizer.model.TransformData;
import net.tgetzoyan.media.organizer.processors.MediaFileProcessor;
import net.tgetzoyan.media.organizer.readers.MediaFileReader;
import net.tgetzoyan.media.organizer.writers.MediaFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Set;

@Configuration
public class BatchConfiguration {
    Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

    @Value("${files.source}")
    private String sourceDirectory;

    @Value("${files.exclude}")
    private Set<String> exclude;

    @Bean
    public ItemReader<Path> reader() {
        return new MediaFileReader(sourceDirectory, exclude);
    }

    @Bean
    public MediaFileProcessor processor() {
        return new MediaFileProcessor();
    }

    @Bean
    public ItemWriter<TransformData> writer() {
        return new MediaFileWriter();
    }

    @Bean
    public ResourcelessTransactionManager resourcelessTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public Job organizeMediaJob(
            JobRepository jobRepository,
            Step step1,
            JobListener listener
    ) {
        return new JobBuilder("organizeMediaJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step jobStep(
            JobRepository jobRepository,
            ResourcelessTransactionManager transactionManager,
            ItemReader<Path> reader,
            MediaFileProcessor processor,
            ItemWriter<TransformData> writer
    ) {
        return new StepBuilder("jobStep", jobRepository)
                .<Path, TransformData>chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
