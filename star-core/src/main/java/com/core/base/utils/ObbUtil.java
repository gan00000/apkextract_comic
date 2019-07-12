package com.core.base.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Created by gan on 2017/11/17.
 */

public class ObbUtil {

    public static File getObbFile(Context context){
        String obbPath = context.getObbDir().getAbsolutePath() + File.separator + "main." + ApkInfoUtil.getVersionCode(context) + "." +
                context.getPackageName() + ".obb";
        File obbFile = new File(obbPath);
        return obbFile;
    }

    public static void unZipObb(Context context, String desUnzipPath) {
        try {
            if (SdcardUtil.isExternalStorageExist() && context.getObbDir() != null && context.getObbDir().exists()){
                File obbFile = getObbFile(context);
                if (obbFile.isFile() && obbFile.exists()){
                    ZipUtil.upZipFile(obbFile,desUnzipPath);
                    //upZipFile finish
                    PL.i("upZip OBB File finish");
                }else {
                    PL.i("upZip OBB File not find");
                }
            }else {
                PL.i("OBB dir not find");
            }
        } catch (IOException e) {
            PL.i("upZip OBB File IOException occoure");
            e.printStackTrace();
        }
    }

}
