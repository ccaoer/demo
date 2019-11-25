package com.demo.file;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Java从服务器下载图片保存到本地
 */
public class DownloadImageUtil {
    public static void main(String[] args) {
        String imgUrl = "http://pic1.win4000.com/wallpaper/c/53cdd1f7c1f21.jpg";
        String imgDir = "/Users/cao/home/git/demo/images/test";
        String imgName = "test" + new Random(10).nextInt()+ imgUrl.substring(imgUrl.lastIndexOf("."));
        download(imgUrl, imgDir, imgName);
    }

    public static void download(String imgUrl, String saveDir, String saveName) {
        System.out.println("imgUrl=" + imgUrl);
        System.out.println("saveDir=" + saveDir);
        System.out.println("saveName=" + saveName);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        FileOutputStream out = null;
        try {
            File dir = new File(saveDir);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            out = new FileOutputStream(saveDir.endsWith("/") ? saveDir : (saveDir + "/") + saveName);
            // 建立连接
            URL url = new URL(imgUrl);
            connection = (HttpURLConnection)url.openConnection();
            // 以post方式提交表单，默认get方式
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // POST方式不能使用缓存
            connection.setUseCaches(false);
            // 连接指定的资源
            connection.connect();
            // 获取网络输入流
            inputStream = connection.getInputStream();
            bis = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            System.out.println("下载完成......");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if(inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
