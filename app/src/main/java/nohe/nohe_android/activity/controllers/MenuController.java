package nohe.nohe_android.activity.controllers;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import nohe.nohe_android.R;
import nohe.nohe_android.activity.services.LoginService;

public class MenuController {
    private AppCompatActivity context;
    private NavigationView navigationView;
    private LoginService loginService;
    private Integer mStackLevel;

    public MenuController(AppCompatActivity context, NavigationView navigationView, LoginService loginService) {
        View header = navigationView.getHeaderView(0);

        this.context = context;
        this.navigationView = navigationView;
        this.loginService = loginService;
        this.mStackLevel = 0;

        showDialog();

        header.findViewById(R.id.nav_question).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDialog();
            }
        });

    }

    public void setMenuTexts() {
        if (navigationView != null) {
            String name = loginService.getUserFirstName() + " " + loginService.getUserSurname();
            View header = navigationView.getHeaderView(0);
            TextView header2 = (TextView) header.findViewById(R.id.nav_user_name);
            header2.setText(name);
        }
    }

    public void showDialog() {
        mStackLevel++;

        FragmentTransaction ft = this.context.getFragmentManager().beginTransaction();
        DialogFragment newFragment = FireMissilesDialogFragment.newInstance(mStackLevel);
        newFragment.show(ft, "dialog");
    }
}
