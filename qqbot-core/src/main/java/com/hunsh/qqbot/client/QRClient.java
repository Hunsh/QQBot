package com.hunsh.qqbot.client;

import com.hunsh.qqbot.contant.Constants;
import com.hunsh.qqbot.entity.Msg;
import com.hunsh.qqbot.types.Os;
import com.hunsh.qqbot.util.Utils;
import io.reactivex.Observable;
import net.dongliu.requests.RawResponse;
import net.dongliu.requests.Requests;
import net.dongliu.requests.Session;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

/**
 * @Author : Edward Jia
 * @Description :
 * @Date : 17/4/26
 * @Version :
 */
public class QRClient {
    private Session             session = Requests.session();
    private Os os;

    public QRClient() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("mac")) {
            os = Os.Mac;
        } else {
            os = os.Windows;
        }
    }


    public void init () throws InterruptedException, ExecutionException{
        CountDownLatch pollLatch = new CountDownLatch(1);

        QRInitTask qrInitTask = new QRInitTask(pollLatch);
        new Thread(qrInitTask).start();
        pollLatch.await();


        FutureTask<Msg> pollFutureTask = new FutureTask<Msg>(new Callable<Msg>() {
            @Override
            public Msg call() throws Exception {
                return qrInitTask.getQrPollUtil().poll();
            }
        });

        new Thread(pollFutureTask).start();
        if(pollFutureTask.get().getMsgId() == 0){
            new QRClient().init();
        }else if(pollFutureTask.get().getMsgId() == 1){
            System.out.println("------------------登录成功---------------------");
            System.out.println("url:"+pollFutureTask.get().getMsgConent());

            String url = pollFutureTask.get().getMsgConent();

            FutureTask<Msg> request4PtWebqqFutureTask = new FutureTask<Msg>(new Callable<Msg>() {
                @Override
                public Msg call() throws Exception {
                    return new request4PtWebqqTask(url).request();
                }
            });

            System.out.println("ptwebqqxxx:"+new request4PtWebqqTask(url).request());

            //new Thread(request4PtWebqqFutureTask).start();

            if(request4PtWebqqFutureTask.get().getMsgId() == 1){
                System.out.println("ptwebqq:"+request4PtWebqqFutureTask.get().getMsgConent());

                String ptwebqq = request4PtWebqqFutureTask.get().getMsgConent();

                FutureTask<Msg> request4VfWebqqFutureTask = new FutureTask<Msg>(new Callable<Msg>() {
                    @Override
                    public Msg call() throws Exception {
                        return new request4VfWebqqTask(ptwebqq).request();
                    }
                });

                new Thread(request4VfWebqqFutureTask).start();

                if(request4VfWebqqFutureTask.get().getMsgId() == 1){
                    System.out.println("json.vfwebqq:"+request4VfWebqqFutureTask.get().getMsgConent());

                }


            }

        }



    }


    /**
     *
     *  msg : -1 unExpected
     *        0 已失效
     *        1 认证成功
     */
    private class QRPollUtil{
        private Map dataMap;

        private QRPollUtil(Map dataMap) {
            this.dataMap = dataMap;
        }

        public Msg poll() {
            return Observable.just(dataMap.get("qrsig"))
                    .flatMap(qrsig -> Observable.just(Constants.ApiURL.VERIFY_QR_CODE.getUrl().replace("{1}", String.valueOf(Utils.hash33(String.valueOf(qrsig))))))
                    .flatMap(verifyUrl -> {
                        boolean repeat = true;
                        boolean readyToIdentity = false;
                        String url = "";

                        while (repeat) {
                            Map<String, Object> headers2 = new HashMap();
                            headers2.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
                            headers2.put("Referer", Constants.ApiURL.VERIFY_QR_CODE.getReferer());
                            String result = session.get(verifyUrl).headers(headers2).send().readToText();

                            Thread.sleep(100L);

                            if (result.contains("已失效")) {
                                LOGGER.info("已失效: " + result);

                                repeat = false;
                            }else if(result.contains("认证中")){
                                LOGGER.info("认证中: " + result);

                                readyToIdentity = true;
                            }else if (readyToIdentity && result.contains("http")){
                                LOGGER.info("认证成功: " + result);

                                url = result.substring(result.indexOf("(")+1, result.indexOf(")")).split(",")[2];
                                url = url.substring(1, url.length()-1);

                                repeat = false;
                            }
                        }

                        Msg msg = new Msg();
                        if(readyToIdentity){
                            msg.setMsgId(Long.valueOf(1));
                            msg.setMsgConent(url);
                        }else{
                            msg.setMsgId(Long.valueOf(0));
                        }

                        return Observable.just(msg);
                    }).elementAt(0).blockingGet();
        }
    }

    /**
     *
     * init task
     */
   private class QRInitTask implements Runnable {
        private CountDownLatch pollLatch;

        private Map<String, String> dataMap = new HashMap<>();
        private QRPollUtil qRPollUtil;

        private QRInitTask(CountDownLatch pollLatch){
            this.pollLatch = pollLatch;
        }

        public QRPollUtil getQrPollUtil() {
            return qRPollUtil;
        }

        public void run() {
            try {
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
                        });
            } catch (Throwable t) {
                t.printStackTrace(System.err);
            } finally {
                pollLatch.countDown();
                qRPollUtil = new QRPollUtil(dataMap);
            }
        }
    }


    private class request4PtWebqqTask {
        private String url;

        private request4PtWebqqTask(String url) {
            this.url = url;
        }

        public Msg request() {
            Msg msg = new Msg();
            msg.setMsgId(Long.valueOf(0));
            Optional.ofNullable(url).ifPresent(urlOfString -> {


                Map<String, Object> headers = new HashMap();
                headers.put("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
                //headers.put("Referer", Constants.ApiURL.GET_PTWEBQQ_CODE.getReferer());

                RawResponse response = Requests.session().get(url).headers(headers).send();
                response.getCookies().forEach(cookie -> {
                    System.out.println("get ptWebqq cookie:"+cookie.getValue());
                    if (Objects.equals(cookie.getName(), "ptwebqq")) {
                        msg.setMsgId(Long.valueOf(1));
                        msg.setMsgConent(cookie.getValue());
                    }
                });
            });
            
            return msg;
        }
    }

    private class request4VfWebqqTask {
        private String ptWebqq;

        private request4VfWebqqTask(String ptWebqq) {
            this.ptWebqq = ptWebqq;
        }

        public Msg request()  {
            Msg msg = new Msg();
            msg.setMsgId(Long.valueOf(0));

            Optional.ofNullable(ptWebqq).ifPresent(ptWebQQOfString -> {


                Map<String, Object> headers = new HashMap();
                headers.put("Referer", Constants.ApiURL.GET_VFWEBQQ_CODE.getReferer());

                RawResponse response =  Requests.session().get(Constants.ApiURL.GET_VFWEBQQ_CODE.getUrl().replace("{1}", ptWebqq)).headers(headers).send();
                try{
                    byte[] bytes = new byte[1024];
                    response.getInput().read(bytes);
                    String jsonString = new String(bytes);

                    msg.setMsgId(Long.valueOf(1));
                    msg.setMsgConent(jsonString);
                }catch (Exception e){

                }



                response.getCookies().forEach(cookie -> {
                    System.out.println("get ptWebqq cookie:"+cookie.getValue());
                    if (Objects.equals(cookie.getName(), "ptwebqq")) {
                        msg.setMsgId(Long.valueOf(1));
                        msg.setMsgConent(cookie.getValue());
                    }
                });
            });

            return msg;
        }
    }




    public Os getOs() {
        return os;
    }

}
