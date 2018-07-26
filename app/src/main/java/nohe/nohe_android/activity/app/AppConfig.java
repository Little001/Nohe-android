package nohe.nohe_android.activity.app;

import nohe.nohe_android.activity.models.ShipmentModel;
import nohe.nohe_android.activity.models.UserModel;

public class AppConfig {
    private static String url = "http://10.0.2.2:51246/api/";
    // private static final String url = "http://www.nohe.cz/api/";
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

    public static class ShipmentData {
        public static ShipmentModel shipment;
    }

    public static String CURRENT_LANGUAGE = "cz";
    public static Boolean GPS_SERVICE_RUNNING = false;
}
