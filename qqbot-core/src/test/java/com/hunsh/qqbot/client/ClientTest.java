package com.hunsh.qqbot.client;

import com.hunsh.qqbot.contant.Constants;
import org.junit.Test;

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

        URL url = new URL(Constants.FETCH_QR_URL);

        URLConnection connection = url.openConnection();

    }




}
