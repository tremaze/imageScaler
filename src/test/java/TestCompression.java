import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import Exception.ImageTypeNotAllowedException;

/**
 * The test class tests different test cases to check if the scaling function works with different image types.
 * All scaled images will be generated in build/resources/test/testImages
 */
public class TestCompression {

    /**
     * Test will pass if there are scaled images for 4k.jpg
     */
    @Test
    public void testCompressionJPG() {
        try {
            testCompression("4k.jpg", false);
            Assertions.assertTrue(true);
        } catch (ImageTypeNotAllowedException ex) {
            ex.printStackTrace();
            Assertions.assertTrue(false);
        }
    }

    /**
     * Test will pass if there are scaled images for 4k.png
     */
    @Test
    public void testCompressionPNG() {
        try {
            testCompression("4k.png", false);
            Assertions.assertTrue(true);
        } catch (ImageTypeNotAllowedException ex) {
            ex.printStackTrace();
            Assertions.assertTrue(false);
        }
    }

    /**
     * The test scales a transparent png image. To check if the test is passed you have to check if the scaled images still has a transparent background
     */
    @Test
    public void testCompressionPNGTransparent() {
        try {
            testCompression("transparent.png", false);
            Assertions.assertTrue(true);
        } catch (ImageTypeNotAllowedException ex) {
            ex.printStackTrace();
            Assertions.assertTrue(false);
        }
    }

    /**
     * Test will pass if no scaled images are generated
     */
    @Test
    public void testCompressionScaleNotNeeded() {
        try {
            testCompression("scale_not_needed.png", true);
            Assertions.assertTrue(true);
        } catch (ImageTypeNotAllowedException ex) {
            ex.printStackTrace();
            Assertions.assertTrue(false);
        }
    }

    /**
     * Test will only pass if ImageTypeNotAllowedException is thrown
     */
    @Test
    public void testCompressionNotAllowed() {
        try {
            testCompression("test.test", false);
            Assertions.assertThrows(
                    ImageTypeNotAllowedException.class,
                    null
            );
        } catch (ImageTypeNotAllowedException ex) {
            ex.printStackTrace();
        }
    }

    public void testCompression(String filename, boolean scaleNotNeeded) throws ImageTypeNotAllowedException {
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("testImages").getFile());
        Compressor compressor = new Compressor(file.toPath(), filename);
        try {
            List<ScaledImage> images = compressor.compress();
            Assertions.assertTrue(scaleNotNeeded || images.size() > 0, "No images scaled");
            for (ScaledImage image : images) {
                File diskImage = new File(Paths.get(image.getFilePath().toString(), image.getImageName()).toUri());
                if (!diskImage.exists()) {
                    Assertions.assertFalse(true, image.getImageName() + "not found ");
                } else {
                    try {
                        ImageReader reader = ImageIO.getImageReadersBySuffix(FilenameUtils.getExtension(diskImage.getAbsolutePath())).next();
                        reader.setInput(ImageIO.createImageInputStream(diskImage));
                        BufferedImage bImage = reader.read(0);

                        if (image.getWidth() != bImage.getWidth()) {
                            Assertions.assertFalse(true, "Image with width " + image.getWidth() + " not found");
                        }
                        if (image.getSize() == 0) {
                            Assertions.assertFalse(true, "Image size can not be 0");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Assertions.assertTrue(true);
        } catch (Exception ex) {
            throw ex;
        }
    }
}
