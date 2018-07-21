package nohe.nohe_android.activity.app;

import nohe.nohe_android.activity.models.UserModel;

public class AppConfig {
    // private String URL = "http://10.0.2.2:51246/api/";
    private String url = "http://www.nohe.cz/api/";
    public static class Urls {
        public static final String LOGIN = "login";
        public static final String LOGOUT = "logout";
        public static final String CHECK_TOKEN = "checkToken";
        public static final String CURRENT_SHIPMENT = "shipment/currently";
        public static final String SHIPMENT_CODE = "shipment/code";
        public static final String SHIPMENT_ROUTE = "shipment/route";
    }
    public static class UserData {
        public static UserModel user;
    }
}
