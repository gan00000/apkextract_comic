package com.ssract.one.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ApkFileUtil {

    public static File copyApp(String srcPath, String destDir, String outname) throws IOException {
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

        return outFile;
    }



//    public static void openDir(Activity activity,String dirPath){
//
//        //getUrl()获取文件目录，例如返回值为/storage/sdcard1/MIUI/music/mp3_hd/单色冰淇凌_单色凌.mp3
////        File file = new File(filePath);
//        //获取父目录
//        File dirFlie = new File(dirPath);
//
//        if (dirFlie.exists()){
//
//            final Uri data = FileProvider.getUriForFile(activity,activity.getPackageName() + ".provider",dirFlie);
//            activity.grantUriPermission(activity.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            final Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(data, "*/*");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            activity.startActivity(intent);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            activity.startActivity(Intent.createChooser(intent, "Open Folder"));
//
////            Uri mUri = FileProvider.getUriForFile(activity,activity.getPackageName() + ".provider",dirFlie);
////
////            Intent intent = new Intent(Intent.ACTION_VIEW);
////            intent.setDataAndType(mUri, "file/*");
//////            intent.addCategory(Intent.CATEGORY_OPENABLE);
//////        activity.startActivity(intent);
////            activity.startActivity(Intent.createChooser(intent, "Open Folder"));
//////            activity.startActivity(Intent.createChooser(intent, "Open Folder"));
//        }
//
//    }

}
