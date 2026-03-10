package com.pricetracker.engine;

import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MessageParser {

    /**
     * Reads a message from Chrome via Native Messaging Protocol.
     * The first 4 bytes indicate the length of the incoming JSON string.
     */
    public static String readMessage(InputStream in) throws IOException {
        byte[] lengthBytes = new byte[4];
        int bytesRead = in.read(lengthBytes, 0, 4);

        if (bytesRead == -1) {
            return null; // Stream closed (Chrome closed the extension)
        }

        // Convert the 4 bytes into an Integer (Little Endian format)
        int messageLength = getInt(lengthBytes);
        
        if (messageLength == 0) return null;

        byte[] messageBytes = new byte[messageLength];
        int totalRead = 0;
        
        while (totalRead < messageLength) {
            int read = in.read(messageBytes, totalRead, messageLength - totalRead);
            if (read == -1) break;
            totalRead += read;
        }

        return new String(messageBytes, StandardCharsets.UTF_8);
    }

    /**
     * Writes a JSON response back to Chrome Native Messaging.
     * Must prepend the 4-byte length integer before the actual JSON string.
     */
    public static void sendMessage(OutputStream out, JSONObject jsonMessage) throws IOException {
        String messageStr = jsonMessage.toString();
        byte[] messageBytes = messageStr.getBytes(StandardCharsets.UTF_8);
        
        // Write exactly 4 bytes representing the length (Little Endian format)
        out.write(getBytes(messageBytes.length));
        
        // Write the actual JSON String bytes
        out.write(messageBytes);
        out.flush();
    }

    // Helper: Convert little endian bytes to int
    private static int getInt(byte[] bytes) {
        return (bytes[3] << 24) & 0xff000000 |
               (bytes[2] << 16) & 0x00ff0000 |
               (bytes[1] << 8) & 0x0000ff00 |
               (bytes[0] << 0) & 0x000000ff;
    }

    // Helper: Convert int to little endian bytes
    private static byte[] getBytes(int length) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (length & 0xFF);
        bytes[1] = (byte) ((length >> 8) & 0xFF);
        bytes[2] = (byte) ((length >> 16) & 0xFF);
        bytes[3] = (byte) ((length >> 24) & 0xFF);
        return bytes;
    }
}
