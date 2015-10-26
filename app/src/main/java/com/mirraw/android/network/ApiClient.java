package com.mirraw.android.network;

import com.google.gson.Gson;
import com.mirraw.android.Mirraw;
import com.mirraw.android.R;
import com.mirraw.android.Utils.LoginManager;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.sharedresources.Logger;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vihaan on 1/7/15.
 */
public class ApiClient {

    public static final String tag = ApiClient.class.getSimpleName();

    private Request mRequest;
    private SharedPreferencesManager mSharedPreferencesManager = new SharedPreferencesManager(Mirraw.getAppContext());


    public Response execute(Request request) throws IOException {
        mRequest = request;
        Response response = new Response();
        try {
            response = getResponse(mRequest);
            //response.setResponseCode(401);
            if (response.getResponseCode() == 401) {
                response = refreshToken();

                if (response.getResponseCode() == 401) {
                    Logger.v(tag, "REFRESH LOGOUT CALLED");
                    LoginManager.onServerLogout();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //response.setResponseCode();
        } finally {
            response.setRequest(mRequest);
            return response;
        }
    }

    private Response refreshToken() throws Exception {
        Logger.v(tag, "REFRESH TOKEN CALLED ");
        Response response;

        String url = ApiUrls.API_REFRESH_ACCESS_TOKEN;
        HashMap<String, String> body = new HashMap<>();
        HashMap<String, String> headerMap = new HashMap<>();

        JSONObject head;
        head = new JSONObject(mSharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");

        body.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
        body.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());

        headerMap.put(Headers.TOKEN, Mirraw.getAppContext().getString(R.string.token));
        headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(Mirraw.getAppContext()));

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setBody(body).setHeaders(headerMap).build();

        response = getResponse(request);

        if (response.getResponseCode() == 200) {
            modifyInitialRequest(response);
            Logger.v(tag, "REFRESH- SEND OLD REQUEST AGAIN");
            response = getResponse(mRequest);
            //response.setResponseCode(401);
        }

        return response;
    }

    private void modifyInitialRequest(Response response) throws Exception {
        Logger.v(tag, "REFRESH- MODIFY HEADERS");

        Gson gson = new Gson();
        mSharedPreferencesManager = new SharedPreferencesManager(Mirraw.getAppContext());
        mSharedPreferencesManager.setLoginResponse(gson.toJson(response));

        JSONObject head = new JSONObject(mSharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
        mRequest.getHeaders().put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
    }

    public Response getResponse(Request request) {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;
        Response response = new Response();
        Map map;
        int responseCode = 0;

        HttpURLConnection connection = null;
        try {
            url = new URL(request.getUrl());

            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod(request.getRequestType());

            if (request.getHeaders() != null) {
                Boolean usApkTest = mSharedPreferencesManager.getUsApkTest();
                if (usApkTest) {
                    request.getHeaders().put("COUNTRY", "US");
                }
                setConnectionHeaders(connection, request.getHeaders());
            }

            if (request.getBody() != null) {
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                JSONObject body;
                body = new JSONObject(request.getBody());
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(body.toString());
                wr.close();
            } else if (request.getBodyJson() != null) {
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(request.getBodyJson().toString());
                wr.close();
            }

            connection.setReadTimeout(request.getRequestTimeout());
            connection.connect();

            responseCode = connection.getResponseCode();
            Logger.v("RESPONSE CODE", "RESPONSE CODE: " + responseCode);

            stringBuilder = new StringBuilder();
            String line = null;

            if (responseCode == 401 || responseCode == 422 || responseCode == 404 || responseCode == 403) {
                if (connection.getErrorStream() != null)
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            Logger.v(tag, "\n" + "=================================================================================");

            Logger.v(tag, "New Request" + "\n" + "=================================================================================");

            Logger.v(tag, "Request method: " + request.getRequestType());
            Logger.v(tag, "Request URL: " + url.toString());
            if (request.getHeaders() != null) {
                Gson gson = new Gson();
                String headers = gson.toJson(request.getHeaders());
                Logger.v(tag, "Request Headers: " + "\n" + headers);

            }
            if (request.getBody() != null) {
                Logger.v(tag, "Request Body: " + "\n" + request.getBody().toString());
            }
            if (request.getBodyJson() != null) {
                Logger.v(tag, "Request Body Json " + request.getBodyJson());
            }
            Logger.v(tag, "Response Code: " + responseCode);
            map = connection.getHeaderFields();

            if (map != null) {
                Gson gson = new Gson();
                String headers = gson.toJson(map);
                Logger.v(tag, "Response Headers: " + "\n" + headers);
            }

            Logger.v(tag, "Response Message: " + "\n" + stringBuilder.toString());

            response.setResponseCode(responseCode);
            response.setHeaders(map);
            response.setBody(stringBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();

            responseCode = connection.getResponseCode();

            JSONObject json = new JSONObject();
            json.put("errors", e.getMessage().toString());
            response.setResponseCode(responseCode);
            response.setBody(json.toString());


            //throw e;
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
            response.setRequest(request);

            return response;
        }
    }

    private void setConnectionHeaders(HttpURLConnection httpURLConnection, HashMap<String, String> headers) {

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            httpURLConnection.setRequestProperty(key, value);
        }

    }
}