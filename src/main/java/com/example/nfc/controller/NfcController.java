package com.example.nfc.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

@Controller

public class NfcController {

    @Value("${web.upload-path}")
    private String path;

    @RequestMapping("/test")
    public ModelAndView testVideo(HttpServletRequest request){
        String path = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        ModelAndView mav = new ModelAndView();
        mav.setViewName("test");
        mav.addObject("video3","理想.mp4");
        return mav;
    }

    /** 文件上传测试 */
    public void uploadTest() throws Exception {
        File f = new File("D:/pic.jpg");
        FileCopyUtils.copy(f, new File(path+"/1.jpg"));
    }

    //文件下载
    @RequestMapping("/a")
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = "nfc-release.apk";

        String transcoding = new String(fileName.getBytes("iso8859-1"), "utf-8");
        String downloadName = URLEncoder.encode(transcoding, "UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.android.package-archive");
        response.setHeader("Content-Disposition", "attachment;fileName=" + downloadName);

        InputStream is = null;
        OutputStream os = null;
        try {
//            String path = request.getServletContext().getContextPath();
            is = new FileInputStream(new File(path+transcoding));
            os = response.getOutputStream();
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            os.close();
            is.close();
        }

    }



    //下载远程文件
    public HttpServletResponse download(String path, HttpServletResponse response) {
        try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    // 下载本地文件
    @RequestMapping("/downloadLocal")
    public void downloadLocal(HttpServletResponse response) throws FileNotFoundException {
        String fileName = "nfc-release.apk".toString(); // 文件的默认保存名

        // 设置输出的格式
        response.reset();
        response.setContentType("application/vnd.android.package-archive");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // 读到流中
        InputStream is = new FileInputStream("D:/"+fileName);// 文件的存放路径
        // 循环取出流中的数据
        byte[] b = new byte[1024];
        int len;
        try {
            OutputStream os = response.getOutputStream();
            while ((len = is.read(b)) > 0)
                os.write(b, 0, len);
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 下载网络文件
    public void downloadNet(HttpServletResponse response) throws MalformedURLException {
        int bytesum = 0;
        int byteread = 0;
        URL url = new URL("windine.blogdriver.com/logo.gif");
        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream("c:/abc.gif");
            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //支持在线打开文件的一种方式
    public void downLoad(String filePath, HttpServletResponse response, boolean isOnLine) throws Exception {
        File f = new File(filePath);
        if (!f.exists()) {
            response.sendError(404, "File not found!");
            return;
        }
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
        byte[] buf = new byte[1024];
        int len = 0;
        response.reset(); // 非常重要
        if (isOnLine) { // 在线打开方式
            URL u = new URL("file:///" + filePath);
            response.setContentType(u.openConnection().getContentType());
            response.setHeader("Content-Disposition", "inline; filename=" + f.getName());
            // 文件名应该编码成UTF-8
        } else { // 纯下载方式
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
        }
        OutputStream out = response.getOutputStream();
        while ((len = br.read(buf)) > 0)
            out.write(buf, 0, len);
        br.close();
        out.close();
    }



}
