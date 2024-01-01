package net.tgetzoyan.media.organizer.model;

import java.nio.file.Path;

public class TransformData {
    private Path destinationPath;
    private Path sourcePath;

    private boolean destinationFileExists = false;

    /* No default instantiation. */
    private TransformData() {
    }

    public TransformData(Path sourcePath, Path destinationPath) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
    }

    public Path getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(Path destinationPath) {
        this.destinationPath = destinationPath;
    }

    public boolean destinationFileExists() {
        return destinationFileExists;
    }

    public void setDestinationFileExists(boolean destinationFileExists) {
        this.destinationFileExists = destinationFileExists;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }
}
