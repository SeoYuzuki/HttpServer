/**
 * 
 */
package HttpServer.beans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Frame {
    private byte opcode;
    private boolean fin;
    private boolean isMasked;

    private byte[] maskingKey;
    private ByteArrayOutputStream byteArrayOutputStream;

    private int leftDataToSendLength = 0;// 剩多少byte沒傳
    private boolean isShow = false;
    // %x0 : 代表連續的幀
    // %x1 : text幀
    // %x2 ： binary幀
    // %x3-7 ： 為非控制幀而預留的
    // %x8 ： 關閉握手幀
    // %x9 ： ping幀

    public Frame(byte[] frameBuf, int rDataEnd) throws Exception {
        if (isShow) {
            for (byte a : frameBuf) {
                System.out.println("frameBuf:" + a + "/" + b2s(a));
            }
        }

        this.byteArrayOutputStream = new ByteArrayOutputStream();

        int payLoadEndIndex = 0;
        int rDataStart = 0;

        // Get FIN + RSV + Opcode as bytes
        this.fin = (frameBuf[0] & 0x80) != 0;
        this.opcode = (byte) (frameBuf[0] & 0x0f);

        if ((frameBuf[0] & 0x40) != 0 || // rsv1
                (frameBuf[0] & 0x20) != 0 || // rsv2
                (frameBuf[0] & 0x10) != 0) // rsv3
        {
            throw new Exception("Reserved bytes should not be set!");
        }

        // Get Mask and payload length
        this.isMasked = (frameBuf[1] & 0x80) != 0;

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
        for (int i = 2; i < payLoadEndIndex; i++) {
            leftDataToSendLength = leftDataToSendLength * 256 + frameBuf[i];
        }

        byte[] message;
        if (isMasked) {// 有Mask, 則下4個byte是Masking-key
            maskingKey = new byte[4];
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

    private String b2s(Byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public void readData(byte[] frameBuf, int rDataEnd) throws IOException {

        byte[] message;
        if (isMasked) {
            message = parseBinaryByMasks(0, rDataEnd, maskingKey, frameBuf);
        } else {
            message = parseBinaryNoMasks(0, rDataEnd, frameBuf);
        }
        byteArrayOutputStream.write(message);
        leftDataToSendLength = leftDataToSendLength - rDataEnd;
    }

    public byte getOpcode() {
        return opcode;
    }

    public boolean isMasked() {
        return isMasked;
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }

    public int getLeftDataToSendLength() {
        return leftDataToSendLength;
    }

    public void show(boolean b) {
        isShow = b;
    }

    public boolean isFin() {
        return fin;
    }
}
