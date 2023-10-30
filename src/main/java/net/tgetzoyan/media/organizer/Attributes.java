package net.tgetzoyan.media.organizer;

import java.math.BigInteger;
import java.nio.file.Path;

public class Attributes {
    private String fileType;
    private BigInteger hash;
    private Path path;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public BigInteger getHash() {
        return hash;
    }

    public void setHash(BigInteger hash) {
        this.hash = hash;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
