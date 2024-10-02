package com.example.tars01;

import javax.usb.*;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class TextToBinaryConverter {

    private static UsbPipe usbPipe;
    private static UsbInterface usbInterface;
    private static UsbDevice usbDevice;
    //static UsbDevice dev = null;

    public static void main(String[] args) {

    }


    public static void SendDataByte(byte[] data) throws UsbException {
        if(data.length>0)
            sendByte(data,usbDevice);
    }
    public static void sendByte(byte[] data, UsbDevice device) throws UsbException {
        if (data != null) {
            if (usbPipe != null && usbPipe.isOpen()) {
                usbPipe.syncSubmit(data);
            } else {
                if (usbDevice == null) {
                    usbDevice = device;
                }

                UsbConfiguration configuration = usbDevice.getActiveUsbConfiguration();
                List<UsbInterface> interfaces = configuration.getUsbInterfaces();
                if (interfaces.isEmpty()) {
                    return;
                }

                usbInterface = interfaces.get(0);
                usbInterface.claim(usbInterface -> true);

                List<UsbEndpoint> endpoints = usbInterface.getUsbEndpoints();
                if (endpoints.isEmpty()) {
                    return;
                }

                for (UsbEndpoint endpoint : endpoints) {
                    if (endpoint.getType() == UsbConst.ENDPOINT_TYPE_BULK &&
                            endpoint.getDirection() == UsbConst.ENDPOINT_DIRECTION_OUT) {
                        usbPipe = endpoint.getUsbPipe();
                        usbPipe.open();
                        usbPipe.syncSubmit(data);
                        usbPipe.close();
                        break;
                    }
                }
            }
        }
    }

    public static synchronized void sendMsg(String msg, String charset, UsbDevice device) {
        if (msg.length() != 0) {
            byte[] send;
            try {
                send = msg.getBytes(charset);
            } catch (UnsupportedEncodingException e) {
                send = msg.getBytes(); // Usa el conjunto de caracteres predeterminado si el especificado no es compatible
            }

            try {
                sendByte(send, device);
                sendByte(new byte[]{13, 10, 0}, device); // Enviar caracteres de nueva línea y retorno de carro
            } catch (UsbException e) {
                e.printStackTrace(); // Manejo de errores en la comunicación USB
            }
        }
    }



    public static void SendDataString(String data){
        if(data.length()>0)
            sendMsg(data, "GBK", usbDevice);
    }




}
