package nohe.nohe_android.activity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import nohe.nohe_android.R;
import nohe.nohe_android.activity.app.AppConfig;
import nohe.nohe_android.activity.controllers.ActivityController;
import nohe.nohe_android.activity.controllers.ErrorController;
import nohe.nohe_android.activity.controllers.MenuController;
import nohe.nohe_android.activity.controllers.PhotosController;
import nohe.nohe_android.activity.interfaces.VolleyStringResponseListener;
import nohe.nohe_android.activity.services.CurrentShipmentService;
import nohe.nohe_android.activity.services.LocationService;
import nohe.nohe_android.activity.services.LoginService;
import nohe.nohe_android.activity.services.PagerService;
import nohe.nohe_android.activity.services.ProgressDialogService;
import nohe.nohe_android.activity.services.RequestService;

public class ShipmentInProgressActivity extends AppCompatActivity {
    private Button finishShipmentBtn;
    private Button takePhotoBtn;
    private Button btnRemovePhoto;
    private ProgressDialogService progressDialog;
    private LoginService loginService;
    private CurrentShipmentService currentShipmentService;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private EditText code_tb;
    private TextView shipment_from_vw;
    private TextView shipment_to_vw;
    private TextView shipment_unload_note_vw;
    private TextView shipment_load_note_vw;
    private TextView shipment_price_vw;
    private PhotosController photosController;
    private ActivityController activityController;
    private ErrorController errorController;
    private MenuController menuController;
    private Integer id_shipment;
    static final int REQUEST_TAKE_PHOTO = 2;
    ArrayList<Bitmap> photoCollection;
    PagerService pagerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_in_progress);

        /*Text view about shipment*/
        shipment_from_vw = (TextView) findViewById(R.id.shipment_from_vw);
        shipment_to_vw = (TextView) findViewById(R.id.shipment_to_vw);
        shipment_unload_note_vw = (TextView) findViewById(R.id.shipment_unload_note_vw);
        shipment_load_note_vw = (TextView) findViewById(R.id.shipment_load_note_vw);
        shipment_price_vw = (TextView) findViewById(R.id.shipment_price_vw);

        finishShipmentBtn = (Button) findViewById(R.id.finish_shipment_btn);
        takePhotoBtn = (Button) findViewById(R.id.take_photo_btn);
        btnRemovePhoto = (Button) findViewById(R.id.btnRemovePhoto);
        code_tb = (EditText) findViewById(R.id.code_tb);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        loginService = new LoginService(getApplicationContext());
        currentShipmentService = new CurrentShipmentService(getApplicationContext());
        progressDialog = new ProgressDialogService(this);
        photoCollection = new ArrayList<Bitmap>();
        pagerService = new PagerService(getApplicationContext(), photoCollection);
        photosController = new PhotosController(this, pagerService, photoCollection, takePhotoBtn, finishShipmentBtn, btnRemovePhoto);
        activityController = new ActivityController(this);
        errorController =  new ErrorController(this);
        menuController = new MenuController(this, navigationView, loginService);
        id_shipment = getIntent().getExtras().getInt("id");
        menuController.setMenuTexts();
        setGuiEvents();
    }

    /**
     * Set GUI event
     */
    private void setGuiEvents() {
        finishShipmentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String code = code_tb.getText().toString().trim();

                if (!code.isEmpty()) {
                    finishShipment(code, photosController.getPhotosInBase64(photoCollection));
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter code!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                photosController.dispatchTakePictureIntent(REQUEST_TAKE_PHOTO);
            }
        });

        btnRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photosController.removePhoto();
            }
        });

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

        shipment_from_vw.setText(getIntent().getExtras().getString("address_from"));
        shipment_to_vw.setText(getIntent().getExtras().getString("address_to"));
        shipment_unload_note_vw.setText(getIntent().getExtras().getString("unload_note"));
        shipment_load_note_vw.setText(getIntent().getExtras().getString("load_note"));
        shipment_price_vw.setText(getIntent().getExtras().getString("price"));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityController.openListShipmentActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * GPS Service
     */
    private void stopGpsService() {
        stopService(new Intent(getApplicationContext(), LocationService.class));
    }

    /**
     * Camera methods
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(this.photosController.mCurrentPhotoPath, bmOptions);
            this.photosController.addPhoto(bitmap);
        }
    }

    /**
     * Shipment methods
     */
    private void finishShipment(final String code, final String[] photos) {
        progressDialog.showDialog(getString(R.string.loading));

        RequestService.makeJsonObjectRequest(Request.Method.POST, AppConfig.Urls.SHIPMENT_CODE, new VolleyStringResponseListener() {
            @Override
            public void onError(VolleyError message) {
                Toast.makeText(getApplicationContext(),
                        errorController.getErrorKeyByCode(message), Toast.LENGTH_LONG).show();
                progressDialog.hideDialog();
            }

            @Override
            public void onResponse(String response) {
                progressDialog.hideDialog();
                currentShipmentService.unSetShipments();
                Toast.makeText(getApplicationContext(),
                        "stav shipmenut je zmenen", Toast.LENGTH_LONG).show();
                activityController.openListShipmentActivity();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Token", loginService.getToken());
                return header;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("id_shipment", id_shipment.toString());
                params.put("code", code);
                params.put("photos",  Arrays.deepToString(photos));

                return params;
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
        }
    }

    private void logout() {
        progressDialog.showDialog(getString(R.string.loading));
        stopGpsService();
        RequestService.makeJsonObjectRequest(Request.Method.POST, AppConfig.Urls.LOGOUT, new VolleyStringResponseListener() {
            @Override
            public void onError(VolleyError message) {
                Toast.makeText(getApplicationContext(),
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

