package nohe.nohe_android.activity.controllers;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import nohe.nohe_android.R;
import nohe.nohe_android.activity.app.AppConfig;
import nohe.nohe_android.activity.interfaces.VolleyStringResponseListener;
import nohe.nohe_android.activity.services.LocationService;
import nohe.nohe_android.activity.services.LoginService;
import nohe.nohe_android.activity.services.ProgressDialogService;
import nohe.nohe_android.activity.services.RequestService;

public class MenuController {
    private AppCompatActivity context;
    private NavigationView navigationView;
    private LoginService loginService;
    private Integer mStackLevel;
    private DrawerLayout mDrawerLayout;
    private ImageView opener_menu_btn;
    private ProgressDialogService progressDialog;
    private ActivityController activityController;
    private ErrorController errorController;

    public MenuController(AppCompatActivity context, NavigationView navigationView, DrawerLayout mDrawerLayout,
                          ProgressDialogService progressDialog, LoginService loginService, ActivityController activityController,
                          ErrorController errorController, ImageView opener_menu_btn) {
        this.context = context;
        this.navigationView = navigationView;
        this.loginService = loginService;
        this.mDrawerLayout = mDrawerLayout;
        this.progressDialog = progressDialog;
        this.activityController = activityController;
        this.errorController = errorController;
        this.opener_menu_btn = opener_menu_btn;
        this.mStackLevel = 0;
        prepareMenu();
    }

    private void prepareMenu() {
        if (navigationView != null) {
            String name = loginService.getUserFirstName() + " " + loginService.getUserSurname();
            View header = navigationView.getHeaderView(0);
            TextView header2 = (TextView) header.findViewById(R.id.nav_user_name);
            header2.setText(name);

            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            resolveMenuClick(menuItem);
                            return true;
                        }
                    });
        }

        opener_menu_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    /**
     * Menu
     */
    private void resolveMenuClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_list_shipment:
                activityController.openListShipmentActivity();
                break;
            case R.id.nav_question:
                showDialog(R.layout.dialog_help);
                break;
            case R.id.nav_info:
                showDialog(R.layout.dialog_about);
                break;
        }
    }

    private void showDialog(int i) {
        mStackLevel++;

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(i);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * GPS Service
     */
    private void stopGpsService() {
        this.context.stopService(new Intent(this.context.getApplicationContext(), LocationService.class));
    }

    private void logout() {
        progressDialog.showDialog(this.context.getString(R.string.loading));
        stopGpsService();
        RequestService.makeJsonObjectRequest(Request.Method.POST, AppConfig.Urls.LOGOUT, new VolleyStringResponseListener() {
            @Override
            public void onError(VolleyError message) {
                Toast.makeText(context.getApplicationContext(),
                        errorController.getErrorKeyByCode(message), Toast.LENGTH_LONG).show();
                loginService.logout();
                progressDialog.hideDialog();
                activityController.openLoginActivity();
            }

            @Override
            public void onResponse(String response) {
                loginService.logout();
                progressDialog.hideDialog();
                activityController.openLoginActivity();
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
