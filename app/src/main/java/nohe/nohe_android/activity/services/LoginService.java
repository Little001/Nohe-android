package nohe.nohe_android.activity.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import nohe.nohe_android.activity.models.UserModel;

public class LoginService {
    private Editor editor;
    private Context _context;
    private SharedPreferences pref;

    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "login_pref";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String TOKEN = "Token";
    private static final String USERNAME = "UserName";
    private static final String USER_FIRST_NAME = "UserName";
    private static final String USER_LAST_NAME = "UserSurname";
    private static final String USER_ROLE = "UserRole";

    @SuppressLint("CommitPrefEdits")
    public LoginService(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void login(String token, UserModel user) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(TOKEN, token);
        editor.putString(USERNAME, user.username);
        editor.putString(USER_FIRST_NAME, user.name);
        editor.putString(USER_LAST_NAME, user.surname);
        editor.putInt(USER_ROLE, user.role);

        editor.commit();
    }

    public void logout() {
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(TOKEN, "");
        editor.putString(USERNAME, "");
        editor.putString(USER_FIRST_NAME, "");
        editor.putString(USER_LAST_NAME, "");
        editor.putInt(USER_ROLE, 0);

        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false) && !pref.getString(TOKEN, "").equals("");
    }

    public String getToken() {
        return pref.getString(TOKEN, "");
    }

    public int getUserRole() {
        return pref.getInt(USER_ROLE, -1);
    }

    public String getUsername() {
        return pref.getString(USERNAME, "");
    }

    public String getUserFirstName() {
        return pref.getString(USER_FIRST_NAME, "");
    }

    public String getUserSurname() {
        return pref.getString(USER_LAST_NAME, "");
    }
}
