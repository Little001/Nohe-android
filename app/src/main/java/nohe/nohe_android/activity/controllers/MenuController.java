package nohe.nohe_android.activity.controllers;

import android.support.design.widget.NavigationView;
import android.view.Menu;
import nohe.nohe_android.R;
import nohe.nohe_android.activity.services.LoginService;

public class MenuController {
    private NavigationView navigationView;
    private LoginService loginService;

    public MenuController(NavigationView navigationView, LoginService loginService) {
        this.navigationView = navigationView;
        this.loginService = loginService;
    }

    public void setMenuTexts() {
        if (navigationView != null) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_user_name).setTitle(loginService.getUserFirstName() + " " + loginService.getUserSurname());
            menu.findItem(R.id.nav_name).setTitle(loginService.getUserFirstName());
            menu.findItem(R.id.nav_surname).setTitle(loginService.getUserSurname());
        }
    }
}
