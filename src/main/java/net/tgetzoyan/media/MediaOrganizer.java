package net.tgetzoyan.media;

import net.tgetzoyan.media.organizer.Organizer;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class MediaOrganizer {
    private static Logger logger = LoggerFactory.getLogger(MediaOrganizer.class);

    private static Options commandLineOptions;
    private static CommandLineParser commandLineParser;

    public static void main(String[] args) {
        buildOptions();

        CommandLine commandLine;

        try {
            commandLine = commandLineParser.parse(commandLineOptions, args);
        } catch (ParseException e) {
            logger.error("Command line parsing failed: {}", e.getMessage());
            e.printStackTrace();
            return;
        }

        LocalDateTime start = LocalDateTime.now();
        logger.info("Starting Application");

        Path sourcePath = Paths.get(commandLine.getOptionValue("s"));
        Path destinationBasePath = Paths.get(commandLine.getOptionValue("d"));

        logger.info("Source directory set to {}", sourcePath.toAbsolutePath());
        logger.info("Destination base directory set to {}", destinationBasePath.toAbsolutePath());

        if (!Files.isDirectory(sourcePath)) {
            logger.error("Argument 1 must be the source directory. This directory must exist and be readable.");
        } else if (!Files.isDirectory(destinationBasePath) || !Files.isWritable(destinationBasePath)) {
            logger.error("Destination base directory either does not exist or is not writeable.");
        } else {
            logger.info("Directories have been vetted, proceeding.");
            (new Organizer(sourcePath, destinationBasePath)).run();
        }

        LocalDateTime end = LocalDateTime.now();
        logger.info("Application finished, total run time (in minutes): " + ChronoUnit.MINUTES.between(start, end));
    }

    private static void buildOptions() {
        Option source = Option.builder("s").longOpt("source")
                .argName("source")
                .hasArg()
                .type(String.class)
                .required(true)
                .desc("Source base directory")
                .build();

        Option destination = Option.builder("d").longOpt("destination")
                .argName("destination")
                .hasArg()
                .type(String.class)
                .required(true)
                .desc("Destination base directory")
                .build();

        commandLineOptions = new Options();
        commandLineOptions
                .addOption(source)
                .addOption(destination);

        commandLineParser = new DefaultParser();
    }
}
