/**
 * 
 */
package HttpServer.notUse;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author ESB20663
 * @date 2020年10月7日
 * @remark
 */
public class RawGenerate {
    /**
     * The overhead (maximum) for a framing header. Assuming a maximum sized payload with masking key.
     */
    public static final int OVERHEAD = 28;

    public ByteBuffer asClientInitiatedTextFrame(String msg) {
        ByteBuffer buf = ByteBuffer.allocate(msg.length() + OVERHEAD);
        putOpFin(buf, (byte) 0x01, true);
        byte mask[] = new byte[] {1, 2, 3, 4 }; // Needs to be random
        byte payload[] = msg.getBytes(Charset.forName("UTF-8")); // must be UTF-8 (per spec)
        putLengthAndMask(buf, payload.length, mask);
        for (int i = 0; i < payload.length; i++) {
            buf.put((byte) (payload[i] ^= mask[i % 4]));
        }
        buf.flip();
        return buf;
    }

    public static void putOpFin(ByteBuffer buf, byte opcode, boolean fin) {
        byte b = 0x00;
        if (fin) {
            b |= 0x80;
        }
        b |= opcode & 0x0F;
        buf.put(b);
    }

    public static void putLengthAndMask(ByteBuffer buf, int length, byte mask[]) {
        if (mask != null) {
            assert (mask.length == 4);
            putLength(buf, length, (mask != null));
            buf.put(mask);
        } else {
            putLength(buf, length, false);
        }
    }

    public static void putLength(ByteBuffer buf, int length, boolean masked) {
        if (length < 0) {
            throw new IllegalArgumentException("Length cannot be negative");
        }
        byte b = (masked ? (byte) 0x80 : 0x00);

        if (length > 0xFF_FF) {
            buf.put((byte) (b | 0x7F));
            buf.put((byte) 0x00);
            buf.put((byte) 0x00);
            buf.put((byte) 0x00);
            buf.put((byte) 0x00);
            buf.put((byte) ((length >> 24) & 0xFF));
            buf.put((byte) ((length >> 16) & 0xFF));
            buf.put((byte) ((length >> 8) & 0xFF));
            buf.put((byte) (length & 0xFF));
        } else if (length >= 0x7E) {
            buf.put((byte) (b | 0x7E));
            buf.put((byte) (length >> 8));
            buf.put((byte) (length & 0xFF));
        } else {
            buf.put((byte) (b | length));
        }
    }
}