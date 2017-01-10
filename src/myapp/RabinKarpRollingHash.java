package myapp;

import myapp.Checksum;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import myapp.FileList;

public class RabinKarpRollingHash {

    public static final int hconst = 69069;

    public int mult = 1;

    int[] buffer;

    int buffptr = 0;

    int segment = 0;

    InputStream is;

    int window;

    public Hashtable<String, String> indexTable;

    public FileList list;

    public Checksum sum;

    public RabinKarpRollingHash(String directory) {
        indexTable = new Hashtable<String, String>();
        sum = new Checksum();
        list = new FileList(directory);
        window = 1024;
    }

    public void initialize() {
        mult = 1;
        buffptr = 0;
    }

    public void setAll(File[] fileList) {
        for (File file : fileList) {
            if (file.isFile() && !file.isHidden()) {
                initialize();
                displayChunks(file);
            }
        }
    }

    public void displayChunks(File filelocation) {
        int mask = 1 << 13;
        mask--;
        File f = filelocation;
        FileInputStream fs = null;
        FileInputStream fsChunk = null;
        BufferedInputStream bis = null;
        try {
            fs = new FileInputStream(f);
            fsChunk = new FileInputStream(f);
            bis = new BufferedInputStream(fs);
            this.is = bis;
            long length = bis.available();
            long curr = length;
            int hash = inithash(window);
            curr -= bis.available();
            byte[] chunk = null;
            String hashvalue = null;
            boolean firstChunk = true;
            int count = 0;
            int duplicate = 0;
            while (curr < length) {
                if ((hash & mask) == 0) {
                    if (firstChunk == true) {
                        chunk = new byte[(int) curr];
                        firstChunk = false;
                    } else {
                        chunk = new byte[segment];
                    }
                    if (fsChunk.read(chunk) != -1) {
                        hashvalue = sum.chunking(chunk);
                        if (!indexTable.containsKey(hashvalue)) {
                            indexTable.put(hashvalue, f.getName());
                        } else {
                            duplicate++;
                        }
                    }
                    segment = 0;
                    count++;
                }
                hash = nexthash(hash);
                curr++;
                segment++;
            }
            System.out.println(count + " chunks generated for: " + f.getName());
            if (duplicate != 0) {
                System.out.println(duplicate + " duplicated chunks in: " + f.getName());
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    is.close();
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int nexthash(int prevhash) throws IOException {
        int c = is.read();
        prevhash -= mult * buffer[buffptr];
        prevhash *= hconst;
        prevhash += c;
        buffer[buffptr] = c;
        buffptr++;
        buffptr = buffptr % buffer.length;
        return prevhash;
    }

    public int inithash(int length) throws IOException {
        buffer = new int[length];
        int hash = 0;
        for (int i = 0; i < length; i++) {
            int c = is.read();
            if (c == -1)
                break;
            buffer[buffptr] = c;
            buffptr++;
            buffptr = buffptr % buffer.length;
            hash *= hconst;
            hash += c;
            if (i > 0)
                mult *= hconst;
        }
        return hash;
    }
}
