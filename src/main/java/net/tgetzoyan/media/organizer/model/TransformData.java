package net.tgetzoyan.media.organizer.model;

import java.nio.file.Path;

public class TransformData {
    private Path destinationPath;
    private Path sourcePath;

    /* No default instantiation. */
    private TransformData() {
    }

    ;

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

    public Path getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }
}
