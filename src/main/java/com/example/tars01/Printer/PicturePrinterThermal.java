package com.example.tars01.Printer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class PicturePrinterThermal {

    // Método para convertir una imagen a escala de grises
    public static WritableImage toGrayscale(WritableImage originalImage) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        WritableImage grayscaleImage = new WritableImage(width, height);
        PixelWriter writer = grayscaleImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = originalImage.getPixelReader().getColor(x, y);
                double gray = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
                writer.setColor(x, y, new Color(gray, gray, gray, color.getOpacity()));
            }
        }
        return grayscaleImage;
    }

    // Método para redimensionar una imagen
    public static Image resizeImage(WritableImage image, int width, int height) {
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0, width, height);
        return canvas.snapshot(null, null);
    }

    // Método para convertir una imagen a blanco y negro usando un umbral
    public static byte[] thresholdToBWPic(WritableImage grayscaleImage, int threshold) {
        int width = (int) grayscaleImage.getWidth();
        int height = (int) grayscaleImage.getHeight();
        byte[] data = new byte[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = grayscaleImage.getPixelReader().getColor(x, y);
                double gray = color.getRed();
                data[y * width + x] = (byte) (gray > threshold / 255.0 ? 0 : 1);
            }
        }
        return data;
    }

    // Método para convertir la imagen en datos de comando para la impresora
    public static byte[] eachLinePixToCmd(byte[] src, int nWidth, int nMode) {
        int nHeight = src.length / nWidth;
        int nBytesPerLine = nWidth / 8;
        byte[] data = new byte[nHeight * (8 + nBytesPerLine)];
        int k = 0;

        for (int i = 0; i < nHeight; i++) {
            int offset = i * (8 + nBytesPerLine);
            data[offset + 0] = 29;
            data[offset + 1] = 118;
            data[offset + 2] = 48;
            data[offset + 3] = (byte) (nMode & 1);
            data[offset + 4] = (byte) (nBytesPerLine % 256);
            data[offset + 5] = (byte) (nBytesPerLine / 256);
            data[offset + 6] = 1;
            data[offset + 7] = 0;

            for (int j = 0; j < nBytesPerLine; j++) {
                data[offset + 8 + j] = (byte) (
                        (src[k] << 7) | (src[k + 1] << 6) | (src[k + 2] << 5) | (src[k + 3] << 4) |
                                (src[k + 4] << 3) | (src[k + 5] << 2) | (src[k + 6] << 1) | src[k + 7]
                );
                k += 8;
            }
        }

        return data;
    }
}
