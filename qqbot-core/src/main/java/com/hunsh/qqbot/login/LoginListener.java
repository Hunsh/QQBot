package com.hunsh.qqbot.login;

import com.hunsh.qqbot.contant.Constants;

import javax.imageio.ImageIO;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Jdz on 2017/4/23.
 */
public class LoginListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("-------------------QQBot init");
        try {
            URL url = new URL(Constants.FETCH_QR_URL + Math.pow(Math.random(), 18d));

            URLConnection conn = url.openConnection();

            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.connect();
            BufferedImage image = ImageIO.read(conn.getInputStream());

            String osName = System.getProperty("os.name");

            ImageIO.write(image, "jpg", new File(Constants.QR_LOCALE_WINDOWS));
        } catch (Exception e) {


        }
    }

        @Override
        public void contextDestroyed (ServletContextEvent servletContextEvent){
            System.out.println("-------------------destroy");
        }
    }
