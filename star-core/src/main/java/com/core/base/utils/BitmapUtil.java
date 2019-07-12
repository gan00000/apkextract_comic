package com.core.base.utils;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by gan on 2017/2/16.
 */

public class BitmapUtil {
    /*
    StaticLayout类介绍

    CharSequence source   需要换行显示的文本内容

    int bufstart  文字起始位置 就是文字开始写入的起点  一般是以0

    int bufend  文字结束位置  一般是文字的长度

    TextPaint paint  画笔对象

    int outerwidth 字符串超出宽度时自动换行

    Layout.Alignment align  对其方式，有ALIGN_CENTER， ALIGN_NORMAL， ALIGN_OPPOSITE 三种

    float spacingmult 相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度

    float spacingadd  在基础行距上添加多少

    boolean includepad  未知

    TextUtils.TruncateAt ellipsize  从什么位置开始省略

    int ellipsizedWidth  超过多少开始省略
    */

//    public static Bitmap bitmapAddText(Bitmap srcBitmap, String text) {
//        return bitmapAddText(srcBitmap, text,60.0f);
//    }

//    public static Bitmap bitmapAddText(Bitmap srcBitmap, String text, float textSize) {
//
//        int width = srcBitmap.getWidth();
//        int height = srcBitmap.getHeight();
//        Bitmap tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(tempBitmap);
//        Paint paint = new Paint(); // 建立画笔
//        paint.setDither(true);
//        paint.setFilterBitmap(true);
//
//        canvas.drawBitmap(srcBitmap, 0, 0, paint);
//
//        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
//        textPaint.setTextSize(textSize);
//        textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度
//        textPaint.setColor(Color.WHITE);
//
//        StaticLayout staticLayout = new StaticLayout(text, 0, text.length(), textPaint, width * 3 / 5,
//                Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.5F, true);
//
//        canvas.translate(width / 5, height / 4);
//        staticLayout.draw(canvas);
//
//        canvas.save(Canvas.ALL_SAVE_FLAG);
//        canvas.restore();
//
//        return tempBitmap;
//
//    }

    public static String saveImageToGallery(Context context, Bitmap bm) {
        String fileName = System.currentTimeMillis() + ".jpg";
        String bitmapPath = "";
        if (PermissionUtil.hasSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            bitmapPath = SdcardUtil.getPath();
        }else {
             bitmapPath = context.getExternalCacheDir().getAbsolutePath();
        }
        PL.i("save bitmap:" + bitmapPath);
        if (!TextUtils.isEmpty(bitmapPath)){
            return saveImageToGallery(context, bm,fileName, bitmapPath + File.separator + "starpy" + File.separator + context.getPackageName());
        }
        return null;
    }

    public static String saveImageToGallery(Context context, Bitmap bm, String fileName, String fileDirStr) {

        File fileDir = new File(fileDirStr);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        final File imageFile = new File(fileDir, fileName);
        try {
            if (!imageFile.exists()) {
                imageFile.createNewFile();
            }
            FileOutputStream  bos = new FileOutputStream (imageFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();

            String insertImage = MediaStore.Images.Media.insertImage(context.getContentResolver(), imageFile.getAbsolutePath(), fileName, null);
            PL.i("mImageUrl:" + insertImage);
            if (SStringUtil.isNotEmpty(insertImage)){
                //如果图片内容相同，即使文件名称不同，在相册里面只会显示一个
                mediaScannerConnection = new MediaScannerConnection(context, new MediaScannerConnection.MediaScannerConnectionClient() {

                    public void onMediaScannerConnected() {
                        try {
                            PL.i("onMediaScannerConnected");
                            mediaScannerConnection.scanFile(imageFile.getAbsolutePath(), "image/jpeg");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onScanCompleted(String path, Uri uri) {
                        PL.i("onScanCompleted");
                        mediaScannerConnection.disconnect();
                    }
                });
                mediaScannerConnection.connect();
                return insertImage;
            }

        }catch (Exception e) {
            // TODO Auto-generated catch block
            if (e != null)
                e.printStackTrace();
        }
        return null;

        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), imageFile.getAbsolutePath(), fileName, null);
//
//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri uri = Uri.fromFile(imageFile);
//            intent.setData(uri);
//            context.sendBroadcast(intent);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }


//        if (SStringUtil.isNotEmpty(imageFilePath)){
//            msc = new MediaScannerConnection(context, new MediaScannerConnection.MediaScannerConnectionClient() {
//
//                public void onMediaScannerConnected() {
//                    try {
//                        PL.i("onMediaScannerConnected");
//                        msc.scanFile(imageFilePath, "image/jpeg");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                public void onScanCompleted(String path, Uri uri) {
//                    PL.i("onScanCompleted");
//                    msc.disconnect();
//                }
//            });
//            msc.connect();

    }

    private static MediaScannerConnection mediaScannerConnection;

    public static int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
