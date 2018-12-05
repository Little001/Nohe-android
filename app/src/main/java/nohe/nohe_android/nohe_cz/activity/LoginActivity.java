package nohe.nohe_android.nohe_cz.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import nohe.nohe_android.R;
import nohe.nohe_android.nohe_cz.app.AppConfig;
import nohe.nohe_android.nohe_cz.controllers.ActivityController;
import nohe.nohe_android.nohe_cz.controllers.ErrorController;
import nohe.nohe_android.nohe_cz.controllers.LocaleController;
import nohe.nohe_android.nohe_cz.interfaces.VolleyStringResponseListener;
import nohe.nohe_android.nohe_cz.models.UserModel;
import nohe.nohe_android.nohe_cz.services.LoginService;
import nohe.nohe_android.nohe_cz.services.ProgressDialogService;
import nohe.nohe_android.nohe_cz.services.RequestService;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private Button czBtn;
    private Button enBtn;
    private EditText usernameTb;
    private EditText passwordTb;
    private LoginService loginService;
    private ProgressDialogService progressDialog;
    private ActivityController activityController;
    private ErrorController errorController;
    private LocaleController localeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameTb = (EditText) findViewById(R.id.userNameTb);
        passwordTb = (EditText) findViewById(R.id.passwordTb);
        loginBtn = (Button) findViewById(R.id.loginBtn);
       /* czBtn = (Button) findViewById(R.id.czLngBtn);
        enBtn = (Button) findViewById(R.id.enLngBtn);*/

        loginService = new LoginService(getApplicationContext());
        progressDialog = new ProgressDialogService(this);
        activityController = new ActivityController(this);
        errorController =  new ErrorController(this, activityController, loginService);
        localeController =  new LocaleController(this);

        runtimePermissions();
        checkIsUserLogged();
        setGuiEvents();
    }

    /**
     * Set GUI event
     */
    private void setGuiEvents() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = usernameTb.getText().toString().trim();
                String password = passwordTb.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {
                    login(username, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            errorController.getStringFromResourcesByName("login_error"), Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        /*czBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                localeController.setCzechLocale();
            }
        });

        enBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                localeController.setEnglishLocale();
            }
        });*/
    }

    private void checkIsUserLogged() {
        try {
            if (loginService.isLoggedIn()) {
                this.activityController.openListShipmentActivity();
            }
        } catch (Exception ex) {
            loginService.logout();
        }
    }

    private void login(final String username, final String password) {
        progressDialog.showDialog(getString(R.string.loading));

        RequestService.makeJsonObjectRequest(Request.Method.POST, AppConfig.Urls.LOGIN, new VolleyStringResponseListener() {
            @Override
            public void onError(VolleyError message) {
                try {
                    Toast.makeText(getApplicationContext(),
                            errorController.getErrorKeyByCodeForLogin(
                                    message,
                                    new String(message.networkResponse.data,"UTF-8")), Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                progressDialog.hideDialog();
            }

            @Override
            public void onResponse(String response) {
                progressDialog.hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String token = jObj.getString("access_token");

                    // Check for error node in json
                    if (!token.equals("")) {
                        UserModel user = new UserModel(jObj);
                        if (user.role == 3) {
                            AppConfig.UserData.user = new UserModel(jObj);

                            // Create login session
                            loginService.login(token, AppConfig.UserData.user);
                            activityController.openListShipmentActivity();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    errorController.getStringFromResourcesByName("no_driver_error"), Toast.LENGTH_LONG).show();
                            loginService.logout();
                            activityController.openLoginActivity();
                        }

                    } else {
                        // Error in login. Get the error message
                        Toast.makeText(getApplicationContext(),
                                errorController.getStringFromResourcesByName("auth_error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    if (!AppConfig.IS_PRODUCTION) {
                        Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<String, String>();
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("Username", username);
                params.put("Password", password);
                params.put("grant_type", "password");

                return params;
            }
        });
    }

    private boolean runtimePermissions() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, errorController.getStringFromResourcesByName("location_permissions_fail"),
                        Toast.LENGTH_LONG).show();
                runtimePermissions();
            }
        }
    }
}
