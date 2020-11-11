/**
 * 
 */
package main.frameWork.beans;

import java.io.ByteArrayOutputStream;

public class Frame {

    private byte opcode;

    private boolean fin;
    private boolean isMasked;

    private byte[] maskingKey;
    private ByteArrayOutputStream byteArrayOutputStream;

    // %x0 : 代表連續的幀
    // %x1 : text幀
    // %x2 ： binary幀
    // %x3-7 ： 為非控制幀而預留的
    // %x8 ： 關閉握手幀
    // %x9 ： ping幀
    public byte getOpcode() {
        return opcode;
    }

    public boolean isMasked() {
        return isMasked;
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }

    public boolean isFin() {
        return fin;
    }

    /**
     * @param opcode the opcode to set
     */
    public void setOpcode(byte opcode) {
        this.opcode = opcode;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(boolean fin) {
        this.fin = fin;
    }

    /**
     * @param isMasked the isMasked to set
     */
    public void setMasked(boolean isMasked) {
        this.isMasked = isMasked;
    }

    /**
     * @param maskingKey the maskingKey to set
     */
    public void setMaskingKey(byte[] maskingKey) {
        this.maskingKey = maskingKey;
    }

    /**
     * @param byteArrayOutputStream the byteArrayOutputStream to set
     */
    public void setByteArrayOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    /**
     * @return the maskingKey
     */
    public byte[] getMaskingKey() {
        return maskingKey;
    }

}
