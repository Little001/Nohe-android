package nohe.nohe_android.activity.controllers;

import android.app.Dialog;
import android.app.DialogFragment;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.TextView;

import nohe.nohe_android.R;
import nohe.nohe_android.activity.services.LoginService;

public class MenuController {
    private NavigationView navigationView;
    private LoginService loginService;

    public MenuController(NavigationView navigationView, LoginService loginService) {
        this.navigationView = navigationView;
        this.loginService = loginService;
        DialogFragment help = new FireMissilesDialogFragment();
//        Dialog dialog = help.onCreateDialog(null);
//        dialog.show();
    }

    public void setMenuTexts() {
        if (navigationView != null) {
            String name = loginService.getUserFirstName() + " " + loginService.getUserSurname();
            View header = navigationView.getHeaderView(0);
            TextView header2 = header.findViewById(R.id.nav_user_name);
            header2.setText(name);
        }
    }
}
