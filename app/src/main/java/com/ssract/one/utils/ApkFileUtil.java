package com.ssract.one.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

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



    public static void openDir(Activity activity,String dirPath){

        //getUrl()获取文件目录，例如返回值为/storage/sdcard1/MIUI/music/mp3_hd/单色冰淇凌_单色凌.mp3
//        File file = new File(filePath);
        //获取父目录
        File dirFlie = new File(dirPath);

        if (dirFlie.exists()){


            Uri mUri = FileProvider.getUriForFile(activity,activity.getPackageName() + ".provider",dirFlie);

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(mUri, "file/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
//        activity.startActivity(intent);
            activity.startActivity(Intent.createChooser(intent, "Open Folder"));
        }

    }

}
