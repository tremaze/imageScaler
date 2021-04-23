import org.apache.commons.io.FilenameUtils;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import Exception.ImageTypeNotAllowedException;

public class Compressor {

    /**
     * Path to the directory where the scaled file will be stored.
     * The original file has to be in this directory
     */
    private Path compressDir;
    private String originalFilename;
    /**
     * Fixed width values for scaling
     */
    private Integer[] widths = {100, 200, 400, 600, 800, 1000, 1300, 1600, 1920};
    /**
     * compression needs a float value or null. If compression is not null it will be casted to a float.
     * If compression is null, the image will not be compressed. The compression value can be between  {@code 0} and  {@code 1}
     */
    private String compression = null;

    public Compressor(Path compressDir, String originalFilename) {
        this.compressDir = compressDir;
        this.originalFilename = originalFilename;
    }

    public Compressor(Path compressDir, String originalFilename, Integer[] widths) {
        this.compressDir = compressDir;
        this.originalFilename = originalFilename;
        this.widths = widths;
    }

    public Compressor(Path compressDir, String originalFilename, String compression) {
        this.compressDir = compressDir;
        this.originalFilename = originalFilename;
        this.compression = compression;
    }

    public Compressor(Path compressDir, String originalFilename, Integer[] widths, String compression) {
        this.compressDir = compressDir;
        this.originalFilename = originalFilename;
        this.widths = widths;
        this.compression = compression;
    }

    public List<ScaledImage> compress() throws ImageTypeNotAllowedException{
        boolean firstCompressionCheck = true;
        List<ScaledImage> images = new ArrayList<>();
        // Gets the original image
        File originalFile = new File(Paths.get(this.compressDir.toString(), this.originalFilename).toUri());

        String fileExtension = FilenameUtils.getExtension(originalFile.getAbsolutePath());
        if (fileExtension != null) {

            if (!Arrays.asList(AllowedExtensions.extensions).contains(fileExtension.toLowerCase())) {
                throw new ImageTypeNotAllowedException(fileExtension.toLowerCase() + " can not be scaled");
            }

            /*
                Converts the width array to a List. Later the list will be ordered, so the widest width will be the first width which will be scaled
            */
            List<Integer> widthOrdered = Arrays.asList(widths);
            Collections.reverse(widthOrdered);

            for (Integer width : widths) {
                try {
                    ImageReader reader = ImageIO.getImageReadersBySuffix(fileExtension).next();
                    reader.setInput(ImageIO.createImageInputStream(originalFile));
                    IIOMetadata metadata = reader.getImageMetadata(0);
                    BufferedImage image = reader.read(0);

                    if (width < image.getWidth()) {
                        double rescaleFactor = (double) image.getWidth() / (double) width;
                        double imageWidth = width;
                        double imageHeight = image.getHeight() / rescaleFactor;

                        image = scale(image, Math.round((float) imageWidth), Math.round((float) imageHeight));

                        String newFilename = "scaled_" + width + "_" + this.originalFilename;
                        Path newFile = Paths.get(this.compressDir.toString(), newFilename);

                        OutputStream os = new FileOutputStream(new File(newFile.toUri()));

                        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(fileExtension);
                        ImageWriter writer = writers.next();

                        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
                        writer.setOutput(ios);

                        ImageWriteParam param = writer.getDefaultWriteParam();

                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        if (compression != null) {
                            param.setCompressionQuality(Float.parseFloat(this.compression));
                        }

                        if (param instanceof JPEGImageWriteParam) {
                            ((JPEGImageWriteParam) param).setOptimizeHuffmanTables(true);
                        }

                        writer.write(null, new IIOImage(image, null, metadata), param);

                        os.close();
                        ios.close();
                        writer.dispose();

                        /**
                         * At the first scaling run the software checks if scaling is necessary.
                         * If the size of the largest compressed image is greater than the size of the original file the scaling function will stop
                         * and no scaled images will be return.
                         */
                        if (firstCompressionCheck) {
                            long fileSizeOriginal, fileSizeCompressed;

                            fileSizeOriginal = Files.size(originalFile.toPath());
                            fileSizeCompressed = Files.size(newFile);
                            if (fileSizeCompressed >= fileSizeOriginal) {
                                Files.delete(newFile);
                                break;
                            }
                            firstCompressionCheck = false;
                        }

                        images.add(new ScaledImage(this.compressDir, newFilename, image.getWidth(), image.getHeight(), Files.size(newFile)));
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return images;
    }

    private BufferedImage scale(BufferedImage originalImage,
                                int scaledWidth, int scaledHeight) {

        int imageType = (originalImage.getType() == 0) ? 5 : originalImage.getType();
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (originalImage.getType() == BufferedImage.TYPE_INT_ARGB) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }
}
