package nohe.nohe_android.activity.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import nohe.nohe_android.activity.activity.ListShipmentActivity;
import nohe.nohe_android.activity.activity.LoginActivity;
import nohe.nohe_android.activity.activity.ShipmentInProgressActivity;
import nohe.nohe_android.activity.activity.StartShipmentActivity;
import nohe.nohe_android.activity.models.ShipmentModel;

public class ActivityController {
    private AppCompatActivity context;

    public ActivityController(AppCompatActivity context) {
        this.context = context;
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

    public void openListShipmentActivity(){
        Intent intent = new Intent(this.context, ListShipmentActivity.class);
        this.context.startActivity(intent);
        this.context.finish();
    }

    public void openInProgressShipmentActivity(ShipmentModel shipment) {
        Intent intent = new Intent(this.context, ShipmentInProgressActivity.class);
        Bundle b = new Bundle();

        b.putInt("id", shipment.ID);
        b.putString("address_from", shipment.address_from);
        b.putString("address_to", shipment.address_to);
        b.putString("unload_note", shipment.unload_note);
        b.putString("load_note", shipment.load_note);
        b.putString("price", shipment.price.toString());
        intent.putExtras(b);

        this.context.startActivity(intent);
        this.context.finish();
    }
}
