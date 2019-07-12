package com.core.base.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 上传文件的HttpRequest类
 * Created by gan on 2016/10/27.
 */

public class HttpFileUploadRequest {

    private static final String BOUNDARY = "----WebKitFormBoundary";

    /**
     * 上传文件
     * @param params
     *            传递的普通参数
     * @param uploadFile
     *            需要上传的文件名
     * @param newFileName
     *            上传的文件名称，不填写将为uploadFile的名称
     * @param urlStr
     *            上传的服务器的路径
     */
    public static String uploadFile(Map<String, String> params, File uploadFile, String newFileName, String urlStr){
        if (newFileName == null || newFileName.trim().equals("")) {
            newFileName = uploadFile.getName();
        }

        String response = null;
        HttpURLConnection conn = null;
        OutputStream out = null;
        FileInputStream in = null;
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        /**
         * 普通的表单数据
         */
        if(params != null && params.size() >0) {
            for (String key : params.keySet()) {
                sb.append("--" + BOUNDARY + "\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + key + "\""
                        + "\r\n");
                sb.append("\r\n");
                sb.append(params.get(key) + "\r\n");
            }
        }
        /**
         * 上传文件的头
         */
        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + newFileName + "\"" + "\r\n");
        sb.append("Content-Type: text/plain" + "\r\n");// 如果服务器端有文件类型的校验，必须明确指定ContentType
        sb.append("\r\n");
        try {
            byte[] headerInfo = sb.toString().getBytes("UTF-8");
            byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Content-Length", String
                    .valueOf(headerInfo.length + uploadFile.length()
                            + endInfo.length));
            conn.setDoOutput(true);

            out = conn.getOutputStream();
            in = new FileInputStream(uploadFile);
            out.write(headerInfo);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.write(endInfo);

            if (conn.getResponseCode() == 200) {
                /* 取得Response内容 */
                is = conn.getInputStream();
                int ch;
                StringBuilder b = new StringBuilder();
                while ((ch = is.read()) != -1) {
                    b.append((char) ch);
                }
                response = b.toString();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            try {
                if(in != null){
                    in.close();
                }
                if(out != null) {
                    out.close();
                }
                if(null != is){
                    is.close();
                }
                if(conn != null) {
                    conn.disconnect();
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
        return response;
    }
}
