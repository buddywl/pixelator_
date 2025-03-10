import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ImageProcessor {
    private int[][][] pixels; // 3D array to store RGB values
    private char[][] asciiPixels;
    private int width;
    private int height;

    private static final char[] ASCII_CHARS = {'@', '#', '%', '+', '=', '-', '.', ' '};

    public ImageProcessor(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        this.width = image.getWidth();
        this.height = image.getHeight();
        pixels = new int[height][width][3];
        asciiPixels = new char[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                pixels[y][x][0] = color.getRed();
                pixels[y][x][1] = color.getGreen();
                pixels[y][x][2] = color.getBlue();
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
        int[][][] newPixels = new int[newHeight][newWidth][3];

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int rSum = 0, gSum = 0, bSum = 0;
                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) {
                        int srcY = y * 2 + dy;
                        int srcX = x * 2 + dx;
                        rSum += pixels[srcY][srcX][0];
                        gSum += pixels[srcY][srcX][1];
                        bSum += pixels[srcY][srcX][2];
                    }
                }
                newPixels[y][x][0] = rSum / 4;
                newPixels[y][x][1] = gSum / 4;
                newPixels[y][x][2] = bSum / 4;
            }
        }

        this.pixels = newPixels;
        this.width = newWidth;
        this.height = newHeight;
    }

    public void convertToAscii() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grey = pixels[y][x][0];
                int index = grey / 32; // Split into 8 categories (0-255 range / 8 levels)
                asciiPixels[y][x] = ASCII_CHARS[index];
            }
        }
    }

    public void printAsciiArt() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(asciiPixels[y][x]);
            }
            System.out.println();
        }
    }

    public void saveImage(String outputPath) throws IOException {
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grey = new Color(pixels[y][x][0], pixels[y][x][1], pixels[y][x][2]).getRGB();
                outputImage.setRGB(x, y, grey);
            }
        }

        ImageIO.write(outputImage, "jpg", new File(outputPath));
    }

    public static void main(String[] args) {
        try {
            ImageProcessor processor = new ImageProcessor("C:\\Users\\buddy\\Desktop\\CS-Projects\\pixelator_\\out\\production\\pixelator_\\input.jpg");
            processor.applyGreyscale();
            processor.reduceResolution();
            processor.reduceResolution();
            processor.reduceResolution();

            processor.convertToAscii();
            processor.printAsciiArt();

            processor.saveImage("output.jpg");
            System.out.println("Greyscale, reduced-resolution image saved as output.jpg");
        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
        }
    }
}
