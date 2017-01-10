package myapp;

import java.io.File;

public class FileList {

    String path;

    File root;

    File[] filelist;

    public FileList(String path) {
        this.path = path;
        root = new File(path);
        filelist = root.listFiles();
    }

    public String getList() {
        String files = "";
        for (File f : filelist) {
            if (f.isDirectory()) {
            } else {
                files += f.getName() + "\n";
            }
        }
        return files;
    }

    public String getList(String name) {
        String files = "";
        for (File f : filelist) {
            if (f.isDirectory()) {
            } else {
                if (f.getAbsolutePath().contains(name))
                    files = f.getAbsolutePath();
            }
        }
        return files;
    }

    public String getFile(int index) {
        int length = filelist.length;
        if (index < 0 || index >= length) {
            System.out.println("invalid index, please check input");
            return null;
        }
        if (filelist[index].isDirectory()) {
            System.out.println("Folder is not supported, please choose a file");
            return null;
        }
        return filelist[index].getAbsolutePath();
    }
}
