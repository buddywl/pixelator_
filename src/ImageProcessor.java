import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ImageProcessor {
    private int[][][] pixels;
    private char[][] asciiPixels;
    private int width;
    private int height;

    private static final char[] ASCII_CHARS = {'@', '#', '%', '+', '=', '-', '.', ' '};

    public ImageProcessor(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        this.width = image.getWidth();
        this.height = image.getHeight();
        pixels = new int[height][width][4];
        asciiPixels = new char[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y), true);
                pixels[y][x][0] = color.getRed();
                pixels[y][x][1] = color.getGreen();
                pixels[y][x][2] = color.getBlue();
                pixels[y][x][3] = color.getAlpha();
            }
        }
    }

    public void applyGreyscale() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = pixels[y][x][0];
                int green = pixels[y][x][1];
                int blue = pixels[y][x][2];
                int grey = (red + green + blue) / 3;
                pixels[y][x][0] = grey;
                pixels[y][x][1] = grey;
                pixels[y][x][2] = grey;
            }
        }
    }

    public void reduceResolution() {
        int newWidth = width / 2;
        int newHeight = height / 2;
        int[][][] newPixels = new int[newHeight][newWidth][4];

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int rSum = 0, gSum = 0, bSum = 0, aSum = 0;
                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) {
                        int srcY = y * 2 + dy;
                        int srcX = x * 2 + dx;
                        rSum += pixels[srcY][srcX][0];
                        gSum += pixels[srcY][srcX][1];
                        bSum += pixels[srcY][srcX][2];
                        aSum += pixels[srcY][srcX][3];
                    }
                }
                newPixels[y][x][0] = rSum / 4;
                newPixels[y][x][1] = gSum / 4;
                newPixels[y][x][2] = bSum / 4;
                newPixels[y][x][3] = aSum / 4;
            }
        }

        this.pixels = newPixels;
        this.width = newWidth;
        this.height = newHeight;
    }

    public void convertToAscii() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (pixels[y][x][3] == 0) { // Check transparency
                    asciiPixels[y][x] = ' ';
                } else {
                    int grey = pixels[y][x][0];
                    int index = grey / 32; // Split into 8 categories (0-255 range / 8 levels)
                    asciiPixels[y][x] = ASCII_CHARS[index];
                }
            }
        }
    }

    public void printAsciiArt() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(asciiPixels[y][x] + " ");
            }
            System.out.println();
        }
    }

    public void saveImage(String outputPath) throws IOException {
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(pixels[y][x][0], pixels[y][x][1], pixels[y][x][2], pixels[y][x][3]);
                outputImage.setRGB(x, y, color.getRGB());
            }
        }

        ImageIO.write(outputImage, "png", new File(outputPath));
    }

    public static void main(String[] args) {
        try {
            ImageProcessor processor = new ImageProcessor("C:\\Users\\buddy\\Desktop\\CS-Projects\\pixelator_\\src\\img.jpg");
            processor.applyGreyscale();
            processor.reduceResolution();
            processor.reduceResolution();
            processor.reduceResolution();
            processor.reduceResolution();

            processor.convertToAscii();
            processor.printAsciiArt();
            processor.saveImage("output.jpg");
            System.out.println("Greyscale, reduced-resolution image saved as output.png");

        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
        }
    }
}