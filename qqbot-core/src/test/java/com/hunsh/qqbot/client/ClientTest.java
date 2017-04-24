package com.hunsh.qqbot.client;

import com.hunsh.qqbot.contant.Constants;
import com.sun.tools.internal.jxc.ap.Const;
import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


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

        URL url = new URL(Constants.FETCH_QR_URL + Math.pow(Math.random(), 18d));

        URLConnection conn = url.openConnection();

        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
        conn.setRequestProperty("referer", Constants.QR_REFER_URL);
        conn.connect();
        BufferedImage image = ImageIO.read(conn.getInputStream());

        String osName = System.getProperty("os.name");
        if(osName.contains ("Windows")){
            ImageIO.write(image, "jpg", new File(Constants.QR_LOCALE_WINDOWS));
        }else if(osName.contains ("Mac")){
            ImageIO.write(image, "jpg", new File(Constants.QR_LOCALE_MAC));
        }

        String sessionId = "";
        String cookieVal = "";
        String key = null;


        for(int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++){
            System.out.println("key: "+key+"; value:"+conn.getHeaderField(i));
            if(key.equalsIgnoreCase("set-cookie")){
                cookieVal = conn.getHeaderField(i);
                cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));

                sessionId = sessionId + cookieVal + ";";
            }
        }

        Iterator iterator = conn.getRequestProperties().entrySet().iterator();


        while(iterator.hasNext()){
            Map.Entry<String, List<String>> entry = iterator.next();

        }



    }


    @Test
    public void test2() {

        /*Flowable.just("Hello world").subscribe(string -> {
            System.out.println(string);
        });*/

        int testSize = 0;
        boolean xxx = false;
        Map<String, Integer> map = new HashMap<>();
        map.put("testSize", 0);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                while(map.get("testSize") < 10){
                    e.onNext(Constants.QR_POLLING_URL);
                }
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String urlStr) throws Exception {
                URL url = new URL(urlStr);

                URLConnection conn = url.openConnection();

                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
                conn.setRequestProperty("Set-Cookie","c6SGEe2Zx1UMaEDaAUieGj-HlMy1tgkKoRtsvP3-6PHnW87bR8rmGmZVH8NRvo2o; PATH=/; DOMAIN=ptlogin2.qq.com;");
                conn.connect();

                byte[] bytes = new byte[1024];

                String content = "";
                String line = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while(( line = br.readLine()) != null){
                    content = line+"/n";
                }

                System.out.println(content);
                map.put("testSize", map.get("testSize")+1);
            }
        });

    }
}
