package main.frameWork.webSocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import main.frameWork.beans.Frame;

public class FrameHandler {

    public Frame parse(InputStream in, byte[] frameBuf) throws Exception {
        Frame frame = new Frame();

        int rDataEnd = in.read(frameBuf);

        if (rDataEnd != -1) {
            printFrameBuf(false, frameBuf);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            frame.setByteArrayOutputStream(byteArrayOutputStream);
            int payLoadEndIndex = 0;
            int rDataStart = 0;

            // Get FIN + RSV + Opcode as bytes
            frame.setFin((frameBuf[0] & 0x80) != 0);
            frame.setOpcode((byte) (frameBuf[0] & 0x0f));

            // System.out.println(b2s(frameBuf[0]));
            if ((frameBuf[0] & 0x40) != 0 || // rsv1
                    (frameBuf[0] & 0x20) != 0 || // rsv2
                    (frameBuf[0] & 0x10) != 0) // rsv3
            {
                throw new Exception("Reserved bytes should not be set!");
            }

            // Get Mask and payload length
            frame.setMasked((frameBuf[1] & 0x80) != 0);

            byte rLength = 0;
            // 通過 & 0111 1111 的運算 移除第一個 bit, 取得payload length
            rLength = (byte) (frameBuf[1] & 0x7F);
            // 求取 extended length range
            if (rLength == 0x7F) {// 如果 length_indicator 等於 127 ，那之後的 8 個 bytes 將會被解析為 64-bit 的 unsigned integer 用來獲得長度
                payLoadEndIndex = 10; // 2+8

            } else if (rLength == 0x7E) {// 如果 length_indicator 等於 126，那下兩個 bytes 必須被解析成 16-bit unsigned integer(i.e 沒有負數的值) 用來獲得數值的長度
                payLoadEndIndex = 4; // 2+2

            } else {
                payLoadEndIndex = 2; // 2+0
            }
            int leftDataToSendLength = 0;// 剩多少byte沒傳
            for (int i = 2; i < payLoadEndIndex; i++) {

                leftDataToSendLength = leftDataToSendLength * 256 + (frameBuf[i] & 0xFF);// to unsigned int

            }

            byte[] message;
            if (frame.isMasked()) {// 有Mask, 則下4個byte是Masking-key
                byte[] maskingKey = new byte[4];
                for (int i = payLoadEndIndex, j = 0; i < (payLoadEndIndex + 4); i++, j++) {
                    maskingKey[j] = frameBuf[i];
                }

                rDataStart = payLoadEndIndex + 4; // 再加上 Masking-key 4個byte
                message = parseBinaryByMasks(rDataStart, rDataEnd, maskingKey, frameBuf);
            } else {
                rDataStart = payLoadEndIndex;
                message = parseBinaryNoMasks(rDataStart, rDataEnd, frameBuf);
            }
            byteArrayOutputStream.write(message);

            leftDataToSendLength = leftDataToSendLength + rDataStart - rDataEnd;

            while (leftDataToSendLength > 0) {// 若資料多到第一個buf讀不完,繼續讀
                rDataEnd = in.read(frameBuf);
                readData(frameBuf, leftDataToSendLength, rDataEnd, frame);

                if (leftDataToSendLength < 0) {
                    throw new Exception("非期待的LeftDataToSendLength");
                }
            }
        }

        return frame;
    }

    /**
     * 印出buf
     */
    private void printFrameBuf(boolean b, byte[] frameBuf) {
        if (b) {
            for (byte a : frameBuf) {
                System.out.println("frameBuf:" + a + "/" + b2s(a));
            }
        }
    }

    private String b2s(Byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    private byte[] parseBinaryByMasks(int rDataStart, int rDataEnd, byte[] masks, byte[] data) {
        byte[] message = new byte[rDataEnd - rDataStart];
        for (int i = rDataStart, j = 0; i < rDataEnd; i++, j++) {
            message[j] = (byte) (data[i] ^ masks[j % 4]);
        }
        return message;
    }

    private byte[] parseBinaryNoMasks(int rDataStart, int rDataEnd, byte[] data) {
        byte[] message = new byte[rDataEnd - rDataStart];
        for (int i = rDataStart, j = 0; i < rDataEnd; i++, j++) {
            message[j] = (byte) (data[i]);
        }
        return message;
    }

    private int readData(byte[] frameBuf, int leftDataToSendLength, int rDataEnd, Frame frame) throws IOException {

        byte[] message;
        if (frame.isMasked()) {
            message = parseBinaryByMasks(0, rDataEnd, frame.getMaskingKey(), frameBuf);
        } else {
            message = parseBinaryNoMasks(0, rDataEnd, frameBuf);
        }
        frame.getByteArrayOutputStream().write(message);
        return leftDataToSendLength - rDataEnd;
    }

    /**
     * i= <br>
     * 10000001 text <br>
     * 10000010 binary <br>
     * 10001000 close
     * 
     */
    public byte[] createReplyByte(String mess, int frame0code) {
        byte[] reply;
        byte[] rawData = mess.getBytes();

        int frameCount = 0;
        byte[] frame = new byte[10];

        frame[0] = (byte) frame0code;

        if (rawData.length <= 125) {
            frame[1] = (byte) rawData.length;
            frameCount = 2;
        } else if (rawData.length >= 126 && rawData.length <= 65535) {
            frame[1] = (byte) 126;
            int len = rawData.length;
            frame[2] = (byte) ((len >> 8) & (byte) 255);
            frame[3] = (byte) (len & (byte) 255);
            frameCount = 4;
        } else {
            frame[1] = (byte) 127;
            int len = rawData.length;
            frame[2] = (byte) ((len >> 56) & (byte) 255);
            frame[3] = (byte) ((len >> 48) & (byte) 255);
            frame[4] = (byte) ((len >> 40) & (byte) 255);
            frame[5] = (byte) ((len >> 32) & (byte) 255);
            frame[6] = (byte) ((len >> 24) & (byte) 255);
            frame[7] = (byte) ((len >> 16) & (byte) 255);
            frame[8] = (byte) ((len >> 8) & (byte) 255);
            frame[9] = (byte) (len & (byte) 255);
            frameCount = 10;
        }

        int bLength = frameCount + rawData.length;

        reply = new byte[bLength];

        int bLim = 0;
        for (int i = 0; i < frameCount; i++) {
            reply[bLim] = frame[i];
            bLim++;
        }
        for (int i = 0; i < rawData.length; i++) {
            reply[bLim] = rawData[i];
            bLim++;
        }

        return reply;
    }
}
