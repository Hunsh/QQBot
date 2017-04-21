package com.hunsh.qqbot.client;

import com.hunsh.qqbot.contant.Constants;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * @Author : Edward Jia
 * @Description :
 * @Date : 17/4/21
 * @Version :
 */
public class ClientTest {

    @Test
    public void test1 () throws Exception{
        //t=0.09821339275864471
        //System.out.println( Math.pow(Math.random(), 18d));

        URL url = new URL(Constants.FETCH_QR_URL+Math.pow(Math.random(), 18d));

        URLConnection conn = url.openConnection();

        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
        conn.connect();


        BufferedImage image = ImageIO.read(conn.getInputStream());

        ImageIO.write(image, "jpg", new File(File.separator + "Users" + File.separator + "hunsh" + File.separator
                + "myfiles" + File.separator + "test/qr.jpg"));

    }
}
