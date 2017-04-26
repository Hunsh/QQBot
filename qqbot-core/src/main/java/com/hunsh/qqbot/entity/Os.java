package com.hunsh.qqbot.entity;

/**
 * @Author : Edward Jia
 * @Description :
 * @Date : 17/4/26
 * @Version :
 */
public enum  Os {
    Mac("Mac"),
    Windows("Windows");

    private String osName;
    Os (String osName){
        this.osName = osName;
    }


    @Override
    public String toString() {
        return osName;
    }
}
