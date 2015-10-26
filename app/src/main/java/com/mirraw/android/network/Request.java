package com.mirraw.android.network;


import com.mirraw.android.Mirraw;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.sharedresources.AppConfig;
import com.mirraw.android.sharedresources.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by pavitra on 4/8/15.
 */
public class Request {

    public static enum RequestTypeEnum {
        GET, POST, DELETE, PATCH, PUT
    }

    //Required Parameters
    private String Url;
    private RequestTypeEnum requestType = RequestTypeEnum.GET;


    private int requestTimeout;

    //Optional Parameters
    private HashMap<String, String> headers, body;
    private JSONObject bodyJson;


    public String getUrl() {
        return Url;
    }

    public String getRequestType() {
        switch (requestType) {
            case GET:
                return "GET";
            case POST:
                return "POST";
            case DELETE:
                return "DELETE";
            case PATCH:
                return "PATCH";
            case PUT:
                return "PUT";
            default:
                return "GET";
        }
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public HashMap<String, String> getBody() {
        return body;
    }

    public JSONObject getBodyJson() {
        return bodyJson;
    }

    private Request(RequestBuilder builder) {
        this.Url = builder.urlBuilder;
        this.requestType = builder.requestTypeBuilder;
        this.headers = builder.headersBuilder;
        this.body = builder.bodyBuilder;
        this.bodyJson = builder.bodyJsonBuilder;
        this.requestTimeout = builder.requestTimeout;
    }


    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }


    private int mResponseCode;

    public int getResponseCode() {
        return mResponseCode;
    }


    public String execute() throws IOException {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try {
            url = new URL(getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(getRequestType());
            connection.setRequestProperty("token", "7d7b91da5458f42241981143a09ffc0b9f298a7cb6d65aa5c2d94fb6d199a51f");

            connection.setReadTimeout(15 * 1000);
            connection.connect();

            mResponseCode = connection.getResponseCode();
            Logger.v("", "Response Code: " + mResponseCode);

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();
        } catch (Exception e) {

            e.printStackTrace();
            throw e;
        } finally {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }

        }
    }


    //Builder Class
    public static class RequestBuilder {

        // required parameters
        private String urlBuilder;
        private RequestTypeEnum requestTypeBuilder;
        private int requestTimeout = 15 * 1000;

        // optional parameters
        private HashMap<String, String> headersBuilder, bodyBuilder;
        private JSONObject bodyJsonBuilder;

        public RequestBuilder(String url, RequestTypeEnum requestType) {
            if (AppConfig.testForStaging()) {
                if (new SharedPreferencesManager(Mirraw.getAppContext()).getStagingApkTest()) {
                    url = url.replaceAll(ApiUrls.BASE_URL_PRODUCTION, ApiUrls.BASE_URL_STAGING);
                } else {
                    url = url.replaceAll(ApiUrls.BASE_URL_STAGING, ApiUrls.BASE_URL_PRODUCTION);
                }
            }
            this.urlBuilder = url;
            this.requestTypeBuilder = requestType;
        }

        public RequestBuilder setHeaders(HashMap<String, String> header) {
            if (AppConfig.testForStaging()) {
                if (new SharedPreferencesManager(Mirraw.getAppContext()).getStagingApkTest()) {
                    header.put(Headers.TOKEN, Mirraw.getAppContext().getString(R.string.staging_token));
                } else {
                    header.put(Headers.TOKEN, Mirraw.getAppContext().getString(R.string.token));
                }
            }
            this.headersBuilder = header;
            return this;
        }

        public RequestBuilder setBody(HashMap<String, String> body) {
            this.bodyBuilder = body;
            return this;
        }

        public RequestBuilder setBodyJson(JSONObject bodyJson) {
            this.bodyJsonBuilder = bodyJson;
            return this;
        }

        public int getRequestTimeout() {
            return requestTimeout;
        }

        public void setRequestTimeout(int timeout) {
            this.requestTimeout = timeout;
        }

        public Request build() {
            return new Request(this);
        }

    }

}