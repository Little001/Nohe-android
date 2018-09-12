package nohe.nohe_android.nohe_cz.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import nohe.nohe_android.R;
import nohe.nohe_android.nohe_cz.controllers.ActivityController;
import nohe.nohe_android.nohe_cz.controllers.ErrorController;
import nohe.nohe_android.nohe_cz.controllers.MenuController;
import nohe.nohe_android.nohe_cz.database.DatabaseHelper;
import nohe.nohe_android.nohe_cz.models.ShipmentModel;
import nohe.nohe_android.nohe_cz.services.LoginService;
import nohe.nohe_android.nohe_cz.services.ProgressDialogService;

public class AddShipmentActivity extends AppCompatActivity {
    private Button save_shipment_btn;
    private Button delete_shipment_btn;
    private NavigationView navigationView;
    private ImageView opener_menu_btn;
    private EditText start_address_tb;
    private EditText finish_address_tb;
    private EditText id_shipment_tb;
    private ProgressDialogService progressDialog;
    private LoginService loginService;
    private DrawerLayout mDrawerLayout;
    private ActivityController activityController;
    private ErrorController errorController;
    private MenuController menuController;
    private DatabaseHelper database;
    private boolean isEditMode;
    private int idShipment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shipment);
        setEditMode();
        database = new DatabaseHelper(this);

        start_address_tb = (EditText) findViewById(R.id.start_address_tb);
        finish_address_tb = (EditText) findViewById(R.id.finish_address_tb);
        id_shipment_tb = (EditText) findViewById(R.id.id_shipment_tb);
        save_shipment_btn = (Button) findViewById(R.id.save_shipment_btn);
        delete_shipment_btn = (Button) findViewById(R.id.delete_shipment_btn);
        opener_menu_btn = (ImageView) findViewById(R.id.opener_menu_btn);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        loginService = new LoginService(getApplicationContext());
        progressDialog = new ProgressDialogService(this);
        activityController = new ActivityController(this);
        errorController =  new ErrorController(this);
        menuController = new MenuController(this, navigationView, mDrawerLayout, progressDialog, loginService, activityController, errorController, opener_menu_btn);

        setGuiEvents();
        setTexts();
    }

    /**
     * Set GUI event
     */
    private void setGuiEvents() {
        save_shipment_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String id_shipment = id_shipment_tb.getText().toString().trim();
                String start_address = start_address_tb.getText().toString().trim();
                String finish_address = finish_address_tb.getText().toString().trim();

                if (!id_shipment.isEmpty() && !start_address.isEmpty() && !finish_address.isEmpty()) {
                    saveShipment(id_shipment, start_address, finish_address);
                } else {
                    Toast.makeText(getApplicationContext(),
                            errorController.getStringFromResourcesByName("add_shipment_error"), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        delete_shipment_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                deleteShipmentDialog();
            }
        });
    }

    private void setTexts() {
        if (isEditMode) {
            ShipmentModel shipment = database.getShipmentByIdShipment(idShipment);

            id_shipment_tb.setText(shipment.ID.toString());
            start_address_tb.setText(shipment.address_from);
            finish_address_tb.setText(shipment.address_to);
        }
    }

    private void setEditMode() {
        Bundle bundle = getIntent().getExtras();
        idShipment = bundle.getInt("id_shipment");

        isEditMode = idShipment != 0;
    }

    private void deleteShipmentDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(AddShipmentActivity.this);

        alert.setTitle(errorController.getStringFromResourcesByName("delete_dialog_title"));
        alert.setMessage(errorController.getStringFromResourcesByName("delete_dialog_text"));
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteShipment();
                activityController.openListShipmentActivity();
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void deleteShipment() {
        ShipmentModel shipment = new ShipmentModel(idShipment,
                "", "", "", "", 0, ShipmentModel.State.NEW,
                "", "", 0, true);

        database.deleteShipment(shipment);
    }

    private void saveShipment(String id_shipment, String start_address, String finish_address) {
        ShipmentModel shipment = new ShipmentModel(Integer.parseInt(id_shipment),
                start_address, finish_address, "", "", 0, ShipmentModel.State.NEW,
                "", "", 0, true);

        if (isEditMode) {
            updateShipment(shipment);
        } else {
            insertShipment(shipment);
        }
    }

    private void insertShipment(ShipmentModel shipment) {
        if (database.insertShipment(shipment)) {
            activityController.openListShipmentActivity();
        } else {
            Toast.makeText(getApplicationContext(),
                    errorController.getStringFromResourcesByName("insert_shipment_error"), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void updateShipment(ShipmentModel shipment) {
        ShipmentModel existingShipment = database.getShipmentByIdShipment(shipment.ID);
        if (existingShipment != null) {
            if (existingShipment.ID.equals(idShipment)) {
                database.updateLocalShipment(idShipment, shipment);
                activityController.openListShipmentActivity();
            } else {
                Toast.makeText(getApplicationContext(),
                        errorController.getStringFromResourcesByName("insert_shipment_error"), Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            database.updateLocalShipment(idShipment, shipment);
            activityController.openListShipmentActivity();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityController.openListShipmentActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
