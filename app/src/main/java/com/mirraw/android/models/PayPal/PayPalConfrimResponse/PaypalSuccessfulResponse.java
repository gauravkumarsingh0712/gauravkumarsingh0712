package com.mirraw.android.models.PayPal.PayPalConfrimResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pavitra on 24/8/15.
 */
public class PaypalSuccessfulResponse {

    /*{
        "client": {
        "environment": "sandbox",
                "paypal_sdk_version": "2.9.10",
                "platform": "Android",
                "product_name": "PayPal-Android-SDK"
    },
        "response": {
        "create_time": "2015-08-24T12:05:32Z",
                "id": "PAY-8RL189911V803482YKXNQRDA",
                "intent": "sale",
                "state": "approved"
    },
        "response_type": "payment"
    }*/

    @Expose
    private Client client;
    @Expose
    private Response response;
    @SerializedName("response_type")
    @Expose
    private String responseType;

    /**
     * @return The client
     */
    public Client getClient() {
        return client;
    }

    /**
     * @param client The client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * @return The response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * @param response The response
     */
    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * @return The responseType
     */
    public String getResponseType() {
        return responseType;
    }

    /**
     * @param responseType The response_type
     */
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

}
