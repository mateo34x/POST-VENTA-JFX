package com.example.tars01.Printer;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

public class PrinterCommandsAct {

    private final static char ESC_CHAR = 0x1B;
    private final static char GS = 0x1D;
    private final static byte[] LINE_FEED = new byte[]{0x0A};
    private final static byte[] CUT_PAPER = new byte[]{GS, 0x56, 0x00};
    private final static byte[] INIT_PRINTER = new byte[]{ESC_CHAR, 0x40};
    private static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33};
    public static final byte[] CTL_LF = {0x0a};          // Print and line feed
    private final static byte[] SET_LINE_SPACE_24 = new byte[]{ESC_CHAR, 0x33, 24};
    private final static byte[] SET_LINE_SPACE_30 = new byte[]{ESC_CHAR, 0x33, 30};
    ;// Cambiar a OEM850
    byte[] blackBack = new byte[] {0x1D, 0x42, 0x01};// Cambiar a fondo negro
    byte[] whiteBack = new byte[] {0x1D, 0x42, 0x00};// Cambiar a fondo blanco



    public static byte[] IniciarImpresora(){
        return new byte[]{0x1B, 0x40};
    }

    public static byte[] setBold(boolean bold) {
        byte[] command = new byte[3];
        command[0] = 0x1B;  // ESC
        command[1] = 0x45;  // E
        command[2] = (byte) (bold ? 1 : 0);  // 1 para activar, 0 para desactivar
        return command;
    }

    public static byte[] printMixedText(String boldText, String normalText) {
        try {
            byte[] boldTextBytes = boldText.getBytes("GBK");
            byte[] normalTextBytes = normalText.getBytes("GBK");

            // Activar bold
            byte[] boldCommand = setBold(true);

            // Desactivar bold
            byte[] normalCommand = setBold(false);

            // Combinar los comandos y textos
            byte[] combined = new byte[boldCommand.length + boldTextBytes.length + normalCommand.length + normalTextBytes.length];

            // Copiar todo en el array combinado
            System.arraycopy(boldCommand, 0, combined, 0, boldCommand.length);
            System.arraycopy(boldTextBytes, 0, combined, boldCommand.length, boldTextBytes.length);
            System.arraycopy(normalCommand, 0, combined, boldCommand.length + boldTextBytes.length, normalCommand.length);
            System.arraycopy(normalTextBytes, 0, combined, boldCommand.length + boldTextBytes.length + normalCommand.length, normalTextBytes.length);

            return combined;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] SetCodePageOEM850(){
        return new byte[] {0x1B, 0x74, 0x02};
    }
    public static byte[] Aling(int option){

        return switch (option) {
            case 0 -> new byte[]{Command.ESC_Align[2] = 0x00};
            case 1 -> new byte[]{Command.ESC_Align[2] = 0x01};
            default -> null;
        };

    }

    public static void sendData(FileOutputStream out, byte[] data) throws IOException {
        if (data.length > 0) {
            out.write(data);
        }
    }


    public static void printImage(BufferedImage image, FileOutputStream out, boolean banner) throws IOException {
        int[][] pixels = new ImageP().getPixelsSlow(image);
        for (int y = 0; y < pixels.length; y += 24) {
            sendData(out, SET_LINE_SPACE_24);
            sendData(out, SELECT_BIT_IMAGE_MODE);
            sendData(out, new byte[]{(byte) (0x00ff & pixels[y].length), (byte) ((0xff00 & pixels[y].length) >> 8)});
            for (int x = 0; x < pixels[y].length; x++) {
                sendData(out, new ImageP().recollectSlice(y, x, pixels));
            }
            sendData(out, CTL_LF);
        }

        if (banner){
            for (int i = 0; i < 3; i++) {  // Añade 5 líneas vacías (puedes ajustar este número)
                sendData(out, CTL_LF);
            }
            sendData(out, Command.GS_i);//Comando para cortar el papel por completo

        }





    }


    public static String RandomImageBannerDown(ArrayList<String> imgs){
        return imgs.get(new Random().nextInt(imgs.size()));
    }
}
