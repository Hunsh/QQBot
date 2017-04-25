package com.hunsh.qqbot.login;

import com.hunsh.qqbot.contant.Constants;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by Jdz on 2017/4/23.
 */
public class LoginListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("-------------------QQBot init");
        try {
            URL url = new URL(Constants.ApiURL.GET_QR_CODE.getUrl() + Math.pow(Math.random(), 18d));

            URLConnection conn = url.openConnection();

            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.connect();
            BufferedImage image = ImageIO.read(conn.getInputStream());

            String osName = System.getProperty("os.name");
            if(osName.contains ("Windows")){
                ImageIO.write(image, "jpg", new File(Constants.QR_LOCALE_WINDOWS));
            }else if(osName.contains ("Mac")){
                ImageIO.write(image, "jpg", new File(Constants.QR_LOCALE_MAC));
            }

            String sessionVal = "";
            String cookieVal = "";
            String key = null;

            for(int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++){
                //System.out.println("key: "+key+"; value: "+conn.getHeaderField(i));
                if(key.equalsIgnoreCase("set-cookie")){
                    cookieVal = conn.getHeaderField(i);
                    cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));

                    //sessionVal = sessionVal + cookieVal + ";";
                }
            }

            if(StringUtils.isNotBlank(sessionVal)){
                //String[] cookieArray = sessionVal.split(";")




            }


        } catch (Exception e) {


        }
    }

        @Override
        public void contextDestroyed (ServletContextEvent servletContextEvent){
            System.out.println("-------------------destroy");
        }
    }
