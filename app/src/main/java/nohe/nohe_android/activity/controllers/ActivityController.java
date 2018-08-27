package nohe.nohe_android.activity.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import nohe.nohe_android.activity.activity.AddShipmentActivity;
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

    public void openAddShipmentActivity(Integer ID){
        Intent intent = new Intent(this.context, AddShipmentActivity.class);

        Bundle b = new Bundle();
        b.putInt("id_shipment", ID != null ? ID : 0);
        intent.putExtras(b);

        this.context.startActivity(intent);
        this.context.finish();
    }

    public void openStartShipmentActivity(ShipmentModel shipment){
        Intent intent = new Intent(this.context, StartShipmentActivity.class);
        intent.putExtras(createShipmentBundle(shipment));

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
        intent.putExtras(createShipmentBundle(shipment));

        this.context.startActivity(intent);
        this.context.finish();
    }

    private Bundle createShipmentBundle(ShipmentModel shipment) {
        Bundle b = new Bundle();

        b.putInt("db_id", shipment.getDbId());
        b.putInt("id", shipment.ID);
        b.putString("address_from", shipment.address_from);
        b.putString("address_to", shipment.address_to);
        b.putString("unload_note", shipment.unload_note);
        b.putString("load_note", shipment.load_note);
        b.putInt("price", shipment.price);
        b.putInt("state", shipment.state.getValue());
        b.putString("photos_before", shipment.photos_before);
        b.putString("photos_after", shipment.photos_after);
        b.putInt("error_code", shipment.error_code);

        return b;
    }
}
