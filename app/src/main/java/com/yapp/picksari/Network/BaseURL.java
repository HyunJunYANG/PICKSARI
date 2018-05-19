package com.yapp.picksari.Network;

/**
 * Created by myeong on 2018. 3. 26..
 */

public class BaseURL {
    private String url = "http:/ec2-13-125-181-226.ap-northeast-2.compute.amazonaws.com:8080/Picksari_war";
    public BaseURL(){

    }
    public String getURL(){
        return url;
    }
}
