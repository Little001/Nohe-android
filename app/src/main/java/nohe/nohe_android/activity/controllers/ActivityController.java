package nohe.nohe_android.activity.controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import nohe.nohe_android.activity.activity.LoginActivity;
import nohe.nohe_android.activity.activity.ShipmentInProgressActivity;
import nohe.nohe_android.activity.activity.StartShipmentActivity;
import nohe.nohe_android.activity.services.CurrentShipmentService;

public class ActivityController {
    private AppCompatActivity context;
    private CurrentShipmentService currentShipmentService;

    public ActivityController(AppCompatActivity context, CurrentShipmentService currentShipmentService) {
        this.context = context;
        this.currentShipmentService = currentShipmentService;
    }

    public void openLoginActivity(){
        Intent intent = new Intent(this.context, LoginActivity.class);
        this.context.startActivity(intent);
        this.context.finish();
    }

    public void openStartShipmentActivity(){
        Intent intent = new Intent(this.context, StartShipmentActivity.class);
        this.context.startActivity(intent);
        this.context.finish();
    }

    public void openInProgressShipmentActivity() {
        Intent intent = new Intent(this.context, ShipmentInProgressActivity.class);
        this.context.startActivity(intent);
        this.context.finish();
    }

    public void resolveAndOpenShipmentActivity() {
        Intent intent;

        if (!this.currentShipmentService.isSet()) {
            intent = new Intent(this.context, StartShipmentActivity.class);
        } else {
            intent = new Intent(this.context, ShipmentInProgressActivity.class);
        }

        this.context.startActivity(intent);
        this.context.finish();
    }
}
