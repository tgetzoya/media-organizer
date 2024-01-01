package net.tgetzoyan.media.organizer.processors.implementation;

import net.tgetzoyan.media.organizer.model.TransformData;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

abstract class Processor {
    private static final String EXTENSION_JPEG = ".jpg";

    protected static final DateTimeFormatter FILE_NAME_FORMAT = DateTimeFormatter.ofPattern(
            "EEEE LLLL dd yyyy HHmmss",
            Locale.US
    );

    protected Logger logger;

    private String basePath;

    private String hashCommand;

    public Processor(Logger logger, String basePath, String hashCommand) {
        this.logger = logger;
        this.basePath = basePath;
        this.hashCommand = hashCommand;
    }

    public TransformData process(Path file) {
        Path newFilePath = formatFilePath(file);
        TransformData data = new TransformData(file, newFilePath);

        try {
            if (fileExists(file, newFilePath)) {
                data.setDestinationFileExists(true);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("Exception was thrown when comparing hashes for {} and {}.", file, newFilePath);
            e.printStackTrace();
        }

        return data;
    }

    protected boolean fileExists(Path file, Path destFilePath) throws IOException, NoSuchAlgorithmException {
        if (!Files.exists(destFilePath)) {
            return false;
        }

        return MessageDigest.isEqual(getHash(file), getHash(destFilePath));
    }

    protected Path formatFilePath(Path file) {
        LocalDateTime fileDate = getDate(file);

        String fileExtension = getExtension(file.getFileName().toString());

        if (null == fileDate) {
            UUID uuid;

            try {
                uuid = UUID.nameUUIDFromBytes(getHash(file));
            } catch (IOException e) {
                uuid = UUID.randomUUID();
                e.printStackTrace();
            }

            return Paths.get(basePath, "Unknown", uuid + fileExtension);
        }

        String fileName = FILE_NAME_FORMAT.format(fileDate.atZone(ZoneId.systemDefault()));

        return Paths.get(basePath,
                fileDate.getYear() + "",
                fileDate.getMonthValue() + "",
                fileDate.getDayOfMonth() + "",
                fileName + fileExtension
        );
    }

    private String getExtension(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase(Locale.ROOT);

        switch (fileExtension) {
            case ".jpg", ".jpeg" -> fileExtension = EXTENSION_JPEG;
        }

        return fileExtension;
    }

    public byte[] getHash(Path file) throws IOException {
        /* Because it would be flat-out stupid to pull gigabytes of data into memory, let the system handle it! */
        ProcessBuilder builder = new ProcessBuilder();

        /* Arrays.asList() will not work here. */
        List<String> command = Arrays.stream(this.hashCommand.split(" ")).collect(Collectors.toList());
        command.add(file.toString());
        builder.command(command);

        Process process = builder.start();
        String result = new String(process.getInputStream().readAllBytes());
        return result.split(" ")[0].trim().toLowerCase(Locale.ROOT).getBytes();
    }

    protected abstract LocalDateTime getDate(Path file);
}
