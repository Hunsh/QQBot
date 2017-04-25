package com.hunsh.qqbot.client;

import com.hunsh.qqbot.contant.Constants;
import com.hunsh.qqbot.util.Utils;
import com.sun.tools.internal.jxc.ap.Const;
import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import net.dongliu.requests.Response;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


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

        URL url = new URL(Constants.ApiURL.GET_QR_CODE.getUrl() + Math.pow(Math.random(), 18d));

        URLConnection conn = url.openConnection();

        //conn.setRequestProperty("accept", "*/*");
        //conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        //conn.setRequestProperty("referer", Constants.ApiURL.GET_QR_CODE.getReferer());

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
                    e.onNext(Constants.ApiURL.VERIFY_QR_CODE.getUrl().replace("{1}", String.valueOf(Utils.hash33("I-bEdvD0Cx38wS*CSUf-5fHR6rS7bfQ5jRYkIo6r0BdFVElFVi6C6Llmf5LHd8WK"))));
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String urlStr) throws Exception {
                URL url = new URL(urlStr);

                URLConnection conn = url.openConnection();

                //conn.setRequestProperty("accept", "*/*");
                //conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
                conn.setRequestProperty("Referer", Constants.ApiURL.VERIFY_QR_CODE.getReferer());
                conn.connect();

                System.out.println("abc");

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

    @Test
    public void test3(){
        System.out.println(Utils.hash33("FMnJkfPaA0XmAnIvGVkXxWRV1MXj6xqRsaJLZ3mbmD7qB7YQtOS1nZQrdGFwNruk"));
    }

    @Test
    public void test4(){
        LOGGER.debug("开始获取二维码");

        String filePath;
        try {
            filePath = (new File("qrcode.png")).getCanonicalPath();
        } catch (IOException var6) {
            throw new IllegalStateException("二维码保存失败");
        }

        Response response = ((HeadOnlyRequestBuilder)this.session.get(ApiURL.GET_QR_CODE.getUrl()).addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36")).file(filePath);
        Iterator desk = response.getCookies().iterator();

        while(desk.hasNext()) {
            Cookie e = (Cookie)desk.next();
            if(Objects.equals(e.getName(), "qrsig")) {
                this.qrsig = (String)e.getValue();
                break;
            }
        }

        LOGGER.info("二维码已保存在 " + filePath + " 文件中，请打开手机QQ并扫描二维码");
        Desktop desk1 = Desktop.getDesktop();

        try {
            File e1 = new File(filePath);
            desk1.open(e1);
        } catch (Exception var5) {
            var5.printStackTrace();
        }




    }
}
