package com.mirraw.android.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pavitra on 4/8/15.
 */
public class Response {

    private int mResponseCode = 0;
    private Map mHeaders;
    private String mBody;

    private Request mRequest;

    public Request getRequest() {
        return mRequest;
    }

    public void setRequest(Request mRequest) {
        this.mRequest = mRequest;
    }

    public Map getHeaders() {
        return mHeaders;
    }

    public String getBody() {
        return mBody;
    }

    public void setHeaders(Map headers) {
        this.mHeaders = headers;
    }

    public void setBody(String body) {
        this.mBody = body;
    }

    public void setResponseCode(int responseCode){
        mResponseCode = responseCode;
    }

    public int getResponseCode(){
        return mResponseCode;
    }


}
