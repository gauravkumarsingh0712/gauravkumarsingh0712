package com.mirraw.android.network;

/**
 * Created by vihaan on 3/7/15.
 */
public class ApiUrls {

    //public static final String BASE_URL = "http://192.168.1.200/api/v1/";
    //public static final String BASE_URL = "http://183.87.7.139/api/v1/";
    //  public static final String BASE_URL = "https://182.237.138.204/api/v1/";

    public static final String BASE_URL_STAGING = "https://staging-mirraw-api.herokuapp.com/api/v1/";
    public static final String BASE_URL_PRODUCTION = "https://api.mirraw.com/api/v1/";
    public static final String BASE_URL = BASE_URL_PRODUCTION;
    //public static final String BASE_URL = "http://139.162.209.11:3000/api/v1/";
    // public static final String BASE_URL = "http://mtest1.mirraw.com/api/v1/";


    public static final String API_BLOCKS = BASE_URL + "/frontpage";
    //    public static final String API_BLOCKS = "https://api.myjson.com/bins/1gb7e";
    public static final String API_MENUS = BASE_URL + "/menu";
    //    public static final String API_MENUS = "https://api.myjson.com/bins/2x5li";
    public static final String API_PRODUCTS = BASE_URL + "/designs/";
//    public static final String API_PRODUCTS = "https://api.myjson.com/bins/177nq";

    public static final String API_COUNTRY_INFO = "";


    //public static final String API_SEARCH ="https://api.myjson.com/bins/1w5ym";

    //new search api
    public static final String API_SEARCH = BASE_URL + "search";


    //BASE_URL+"search?"+TOKEN+"&term=";

    public static final String API_CURRENCIES = "https://api.myjson.com/bins/21syy";

    public static final String API_COUNTRIES = BASE_URL + "countries";
    public static final String API_REGISTER = BASE_URL + "account";

    public static final String API_LOGIN = BASE_URL + "account/sign_in";

    public static final String API_LOGOUT = BASE_URL + "account/sign_out";

    public static final String API_ADD_CART = BASE_URL + "user/cart/line_item";

    public static final String API_GET_CART_ITEMS = BASE_URL + "user/cart";

    public static final String API_DEL_CART_ITEMS = BASE_URL + "user/cart/line_item/";
    public static final String API_ADD_ADDRESS = BASE_URL + "user/addresses";

    public static final String API_UPDATE_CART_ITEMS = BASE_URL + "user/cart/line_item/";

    public static final String API_RESET_PASS = BASE_URL + "account/password";

    public static final String API_GET_PAYMENT_OPTIONS = BASE_URL + "user/cart/payment_details";
    public static final String API_PRODUCT_TAB_DETAILS = BASE_URL + "/design_policies/";

    public static final String API_CREATE_ORDER = BASE_URL + "user/orders";

    public static final String API_COUPON_APPLY = BASE_URL + "user/cart/assign_coupon";
    public static final String API_FACEBOOK_AUTH = BASE_URL + "account/facebook/callback";
    public static final String API_GOOGLE_AUTH = BASE_URL + "account/google_oauth2/callback";

    public static final String API_PAYPAL_VERIFY = BASE_URL + "user/orders/";

    public static final String API_REFRESH_ACCESS_TOKEN = BASE_URL + "account/refresh";

    /**
     * TODO: PAYU CONSTANT URLS
     **/
    public static final String PAYU_SUCCESS_URL = BASE_URL + "orders/payu_response";
    public static final String PAYU_FAILURE_URL = BASE_URL + "orders/payu_response";
}
