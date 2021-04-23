import java.nio.file.Path;

public class ScaledImage {
    private Path filePath;
    private String imageName;
    private int width;
    private int height;
    private long size;

    public ScaledImage(Path filePath, String imageName, int width, int height, long size) {
        this.filePath = filePath;
        this.imageName = imageName;
        this.width = width;
        this.height = height;
        this.size = size;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
