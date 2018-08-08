package nohe.nohe_android.activity.app;

import nohe.nohe_android.activity.models.UserModel;

public class AppConfig {
    public static final boolean IS_PRODUCTION = true;
    private static String url = AppConfig.IS_PRODUCTION ? "https://www.nohe.cz/api/" : "http://10.0.2.2:51246/api/";
    public static class Urls {
        public static final String LOGIN = url + "login";
        public static final String LOGOUT = url + "logout";
        public static final String CURRENT_SHIPMENT = url + "shipment/currently";
        public static final String SHIPMENT_CODE = url + "shipment/code";
        public static final String SHIPMENT_ROUTE = url + "shipment/route";
    }
    public static class UserData {
        public static UserModel user;
    }

    public static String CURRENT_LANGUAGE = "cs";
}
