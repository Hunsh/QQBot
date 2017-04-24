package com.hunsh.qqbot.contant;

import java.io.File;

/**
 * @Author : Edward Jia
 * @Description :
 * @Date : 17/4/21
 * @Version :
 */
public class Constants {
    public static final String FETCH_QR_URL = "https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=";

    public static final String QR_LOCALE_MAC =File.separator + "Users" + File.separator + "hunsh" + File.separator + "myfiles" + File.separator + "test/qr.jpg";

    public static final String QR_LOCALE_WINDOWS = "D:"+File.separator +"qr.jpg";

    public static final String QR_POLLING_URL = "https://ssl.ptlogin2.qq.com/ptqrlogin?ptqrtoken=12345678&webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-0-9604&mibao_css=m_webqq&t=undefined&g=1&js_type=0&js_ver=10181&login_sig=&pt_randsalt=0 ";

    public static final String QR_REFER_URL = "https://ui.ptlogin2.qq.com/cgi-bin/login?daid=164&target=self&style=16&mibao_css=m_webqq&appid=501004106&enable_qlogin=0&no_verifyimg=1&s_url=http%3A%2F%2Fw.qq.com%2Fproxy.html&f_url=loginerroralert&strong_login=1&login_state=10&t=20131024001";


}
