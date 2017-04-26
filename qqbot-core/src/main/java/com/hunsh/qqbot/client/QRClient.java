package com.hunsh.qqbot.client;

import com.hunsh.qqbot.contant.Constants;
import com.hunsh.qqbot.entity.Os;
import com.hunsh.qqbot.util.Utils;
import com.sun.tools.internal.jxc.ap.Const;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import net.dongliu.requests.RawResponse;
import net.dongliu.requests.Requests;
import net.dongliu.requests.Session;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

/**
 * @Author : Edward Jia
 * @Description :
 * @Date : 17/4/26
 * @Version :
 */
public class QRClient {
    private Map<String, String> dataMap = new HashMap<>();
    private Session             session = Requests.session();
    private Os                  os;

    public QRClient() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("mac")) {
            os = Os.Mac;
        } else {
            os = os.Windows;
        }
    }

    private class QRPollingClient {
        private QRInitClient qrInitClient;

        public QRPollingClient(QRInitClient qrInitClient) {
            this.qrInitClient = qrInitClient;
        }

        public void run() {
            Observable.just(dataMap.get("qrsig"))
                    .flatMap(qrsig -> Observable.just(Constants.ApiURL.VERIFY_QR_CODE.getUrl().replace("{1}",
                            String.valueOf(Utils.hash33(qrsig)))))
                    //.observeOn(Schedulers.newThread())
                    .subscribe(verifyUrl -> {
                        boolean repeat = true;

                        while (repeat) {
                            Map<String, Object> headers2 = new HashMap();
                            headers2.put("User-Agent",
                                    "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
                            headers2.put("Referer", Constants.ApiURL.VERIFY_QR_CODE.getReferer());
                            String result = session.get(verifyUrl).headers(headers2).send().readToText();

                            Thread.sleep(100L);

                            if (result.contains("已失效")) {
                                LOGGER.info("已失效: "+result);
                                repeat = false;
                            }
                        }
                    });
            qrInitClient.run();
        }
    }

    class QRInitClient {

        public QRInitClient() {
        }

        public void run() {
            Observable.just(Constants.ApiURL.GET_QR_CODE.getUrl())
                    //.observeOn(Schedulers.newThread())
                    .subscribe(getQrUrl -> {
                        Map<String, Object> headers = new HashMap();
                        headers.put("User-Agent",
                                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");

                        RawResponse response = session.get(Constants.ApiURL.GET_QR_CODE.getUrl()).headers(headers)
                                .send();
                        response.getCookies().forEach(cookie -> {
                            //System.out.println("cookie:"+cookie.getValue());
                            if (Objects.equals(cookie.getName(), "qrsig")) {
                                dataMap.put("qrsig", cookie.getValue());
                            }
                        });

                        switch (os) {
                            case Mac:
                                response.writeToFile(Constants.QR_LOCALE_MAC);
                                break;
                            case Windows:
                                response.writeToFile(Constants.QR_LOCALE_WINDOWS);
                        }

                        LOGGER.info("二维码已保存在 " + Constants.QR_LOCALE_MAC + " 文件中，请打开手机QQ并扫描二维码");
                        Desktop desk1 = Desktop.getDesktop();

                        try {
                            File e1 = new File(Constants.QR_LOCALE_MAC);
                            desk1.open(e1);
                        } catch (Exception var5) {
                            var5.printStackTrace();
                        }

                        new QRPollingClient(this).run();

                    });
        }
    }

    public Os getOs() {
        return os;
    }

}
