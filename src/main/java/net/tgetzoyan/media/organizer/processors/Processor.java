package net.tgetzoyan.media.organizer.processors;

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
import java.util.UUID;

abstract class Processor {
    private static final String HASHING_ALGORITHM = "SHA-256";
    protected static final DateTimeFormatter FILE_NAME_FORMAT = DateTimeFormatter.ofPattern("EEEE LLLL dd yyyy A");

    protected Logger logger;

    protected Path file;
    private String basePath;

    public Processor(Logger logger, Path file, String basePath) {
        this.logger = logger;
        this.file = file;
        this.basePath = basePath;
    }

    public void process() {
        Path newFilePath = formatFilePath(getDate());

        try {
            if (fileExists(newFilePath)) {
                logger.info("File already exists. Ignoring");
            } else {

                logger.info("Moving file from {} to {}", file, newFilePath);

                if (Files.notExists(newFilePath.getParent())) {
                    Files.createDirectories(newFilePath.getParent());
                }

                Files.copy(file, newFilePath);
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("Exception was thrown when comparing hashes for {} and {}", file, newFilePath);
            e.printStackTrace();
        }

        logger.info("New file name for {} is {}", file, newFilePath);
    }

    protected byte[] getHash() throws IOException, NoSuchAlgorithmException {
        return MessageDigest.getInstance(HASHING_ALGORITHM).digest(Files.readAllBytes(file));
    }

    protected boolean fileExists(Path destFilePath) throws IOException, NoSuchAlgorithmException {
        if (!Files.exists(destFilePath)) {
            return false;
        }

        byte[] existingFileHash = MessageDigest.getInstance(HASHING_ALGORITHM).digest(Files.readAllBytes(destFilePath));

        return MessageDigest.isEqual(getHash(), existingFileHash);
    }

    protected Path formatFilePath(LocalDateTime fileDate) {
        String fileExtension = file.getFileName().toString();
        fileExtension = fileExtension.substring(fileExtension.lastIndexOf("."));

        if (null == fileDate) {
            UUID uuid;

            try {
                uuid = UUID.nameUUIDFromBytes(getHash());
            } catch (IOException | NoSuchAlgorithmException e) {
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

    protected abstract LocalDateTime getDate();
}
