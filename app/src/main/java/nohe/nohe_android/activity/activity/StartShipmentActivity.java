package nohe.nohe_android.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import nohe.nohe_android.R;
import nohe.nohe_android.activity.app.AppConfig;
import nohe.nohe_android.activity.interfaces.VolleyStringResponseListener;
import nohe.nohe_android.activity.services.LoginService;
import nohe.nohe_android.activity.services.ProgressDialogService;
import nohe.nohe_android.activity.services.RequestService;


public class StartShipmentActivity extends AppCompatActivity {
    private Button logoutBtn;
    private ProgressDialogService progressDialog;
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_shipment);

        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        loginService = new LoginService(getApplicationContext());
        progressDialog = new ProgressDialogService(this);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                logout();
            }

        });
    }

    private void openActivity(){
        Intent intent = new Intent(StartShipmentActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void logout() {
        progressDialog.showDialog(getString(R.string.loading));

        RequestService.makeJsonObjectRequest(Request.Method.POST, AppConfig.Urls.LOGOUT, new VolleyStringResponseListener() {
            @Override
            public void onError(VolleyError message) {
                Toast.makeText(getApplicationContext(),
                        message.toString(), Toast.LENGTH_LONG).show();
                progressDialog.hideDialog();
            }

            @Override
            public void onResponse(String response) {
                loginService.logout();
                progressDialog.hideDialog();

                openActivity();
                finish();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Token", loginService.getToken());
                return header;
            }

            @Override
            public Map<String, String> getParams() {
                return null;
            }
        });
    }
}
