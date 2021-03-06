package nohe.nohe_android.nohe_cz.app;

import nohe.nohe_android.nohe_cz.models.UserModel;

public class AppConfig {
    public static final boolean IS_PRODUCTION = true;
    private static String url = AppConfig.IS_PRODUCTION ? "https://www.nohe.cz/api/" : "http://10.0.2.2:51246/api/";
    public static class Urls {
        public static final String LOGIN = url + "token";
        public static final String CURRENT_POSITION = url + "driver/position";
        public static final String CURRENT_SHIPMENTS = url + "driver/currently";
        public static final String FINISH_SHIPMENTS = url + "driver/finish";
    }
    public static class UserData {
        public static UserModel user;
    }

    public static String PHOTOS_DIVIDER = "⚫";
    public static String PHOTOS_DIVIDER_FOR_SERVER = "|";
    public static String CURRENT_LANGUAGE = "cs";
}
