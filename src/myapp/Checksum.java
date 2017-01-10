package myapp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Checksum {

    public MessageDigest md;

    private FileInputStream fis;

    public Checksum() {
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String generateChecksum(String filepath, String filename) {
        try {
            fis = new FileInputStream(filepath);
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] mdbytes = md.digest();
        String result = byteToHex(mdbytes);
        return result;
    }

    public String chunking(byte[] dataBytes) {
        md.update(dataBytes);
        byte[] mdbytes = md.digest();
        String result = byteToHex(mdbytes);
        return result;
    }

    public String byteToHex(byte[] mdbytes) {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
