package nohe.nohe_android.nohe_cz.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import nohe.nohe_android.nohe_cz.app.AppConfig;
import nohe.nohe_android.nohe_cz.interfaces.VolleyStringResponseListener;
import nohe.nohe_android.nohe_cz.models.UserModel;

public class LoginService {
    private Editor editor;
    private Context _context;
    private SharedPreferences pref;

    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "login_pref";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String TOKEN = "Token";
    private static final String REFRESH_TOKEN = "Refresh_token";
    private static final String USER_ID = "user_id";
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

    public void login(String token, String refresh_token, UserModel user) {
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(TOKEN, token);
        editor.putString(REFRESH_TOKEN, refresh_token);
        editor.putString(USERNAME, user.username);
        editor.putInt(USER_ID, user.ID);
        editor.putString(USER_FIRST_NAME, user.name);
        editor.putString(USER_LAST_NAME, user.surname);
        editor.putInt(USER_ROLE, user.role);

        editor.commit();
    }

    public void logout() {
        editor.putBoolean(IS_LOGGED_IN, false);
        editor.putString(TOKEN, "");
        editor.putString(REFRESH_TOKEN, "");
        editor.putInt(USER_ID, 0);
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

    public String getRefreshToken() {
        return pref.getString(REFRESH_TOKEN, "");
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

    public int getUserId() {
        return pref.getInt(USER_ID, 0);
    }

    public void refreshToken() {
        RequestService.makeJsonObjectRequest(Request.Method.POST, AppConfig.Urls.LOGIN, new VolleyStringResponseListener() {
            @Override
            public void onError(VolleyError message) {
                logout();
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String token = jObj.getString("access_token");
                    String refresh_token = jObj.getString("refresh_token");

                    // Check for error node in json
                    if (!token.equals("")) {
                        UserModel user = new UserModel(jObj);
                        if (user.role == 3) {
                            AppConfig.UserData.user = new UserModel(jObj);

                            // Create login session
                            login(token, refresh_token, AppConfig.UserData.user);
                        } else {
                            logout();
                        }

                    } else {
                        logout();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + getToken());
                return header;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("refresh_token", getRefreshToken());
                params.put("grant_type", "refresh_token");

                return params;
            }
        });
    }
}
