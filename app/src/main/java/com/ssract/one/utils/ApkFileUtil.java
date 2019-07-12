package com.ssract.one.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ApkFileUtil {

    public static void copyApp(String srcPath, String destDir, String outname) throws IOException {
        File in = new File(srcPath);

        File parentFile = new File(destDir);

        if (!parentFile.exists()) parentFile.mkdirs();

        File outFile = new File(parentFile, outname + ".apk");
        if (!outFile.exists()) outFile.createNewFile();
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(outFile);

        int count;
        byte[] buffer = new byte[256 * 1024];
        while ((count = fis.read(buffer)) > 0) {
            fos.write(buffer, 0, count);
        }

        fis.close();
        fos.flush();
        fos.close();
    }

}
