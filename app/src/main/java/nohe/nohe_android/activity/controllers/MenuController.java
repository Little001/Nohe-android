package nohe.nohe_android.activity.controllers;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
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
    private ProgressDialogService progressDialog;
    private ActivityController activityController;
    private ErrorController errorController;

    public MenuController(AppCompatActivity context, NavigationView navigationView, DrawerLayout mDrawerLayout,
                          ProgressDialogService progressDialog, LoginService loginService, ActivityController activityController,
                          ErrorController errorController) {
        this.context = context;
        this.navigationView = navigationView;
        this.loginService = loginService;
        this.mDrawerLayout = mDrawerLayout;
        this.progressDialog = progressDialog;
        this.activityController = activityController;
        this.errorController = errorController;
        this.mStackLevel = 0;
    }

    public void setMenuTexts() {
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
    }

    /**
     * Menu
     */
    private void resolveMenuClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_question:
                showDialog();
                break;
        }
    }

    private void showDialog() {
        mStackLevel++;

        FragmentTransaction ft = this.context.getFragmentManager().beginTransaction();
        DialogFragment newFragment = FireMissilesDialogFragment.newInstance(mStackLevel);
        newFragment.show(ft, "dialog");
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
                progressDialog.hideDialog();
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
