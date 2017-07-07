package com.hunsh.qqbot.entity;

import java.io.Serializable;

/**
 * @Author : Edward Jia
 * @Description :
 * @Date : 17/7/7
 * @Version :
 */
public class Msg implements Serializable{

    private Long msgId;
    private String msgConent;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getMsgConent() {
        return msgConent;
    }

    public void setMsgConent(String msgConent) {
        this.msgConent = msgConent;
    }
}
