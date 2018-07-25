package nohe.nohe_android.activity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import nohe.nohe_android.activity.controllers.PhotosController;
import nohe.nohe_android.activity.interfaces.VolleyStringResponseListener;
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
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private EditText code_tb;
    private PhotosController photosController;
    private ActivityController activityController;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ArrayList<Bitmap> photoCollection;
    PagerService pagerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_in_progress);

        finishShipmentBtn = (Button) findViewById(R.id.finish_shipment_btn);
        takePhotoBtn = (Button) findViewById(R.id.take_photo_btn);
        btnRemovePhoto = (Button) findViewById(R.id.btnRemovePhoto);
        code_tb = (EditText) findViewById(R.id.code_tb);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        loginService = new LoginService(getApplicationContext());
        progressDialog = new ProgressDialogService(this);
        photoCollection = new ArrayList<Bitmap>();
        pagerService = new PagerService(getApplicationContext(), photoCollection);
        photosController = new PhotosController(this, pagerService, photoCollection, takePhotoBtn);
        activityController = new ActivityController(this);
        setGuiEvents();
        startGpsService();
    }

    /**
     * Set GUI event
     */
    private void setGuiEvents() {
        finishShipmentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String code = code_tb.getText().toString().trim();

                finishShipment(code, photosController.getPhotosInBase64(photoCollection));
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
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
    }

    /**
     * GPS Service
     */
    private void startGpsService() {
        startService(new Intent(getApplicationContext(), LocationService.class).putExtra("id_shipment", AppConfig.ShipmentData.shipment.ID.toString()));
    }
    private void stopGpsService() {
        stopService(new Intent(getApplicationContext(), LocationService.class));
    }

    /**
     * Camera methods
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            this.photosController.addPhoto((Bitmap) extras.get("data"));
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
                        message.toString(), Toast.LENGTH_LONG).show();
                progressDialog.hideDialog();
            }

            @Override
            public void onResponse(String response) {
                progressDialog.hideDialog();
                Toast.makeText(getApplicationContext(),
                        "stav shipmenut je zmenen", Toast.LENGTH_LONG).show();
                stopGpsService();
                activityController.openStartShipmentActivity();
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
                params.put("id_shipment", AppConfig.ShipmentData.shipment.ID.toString());
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
            case R.id.nav_camera:
                Toast.makeText(getApplicationContext(),
                        "nave_camera", Toast.LENGTH_LONG).show();
            case R.id.nav_gallery:
                Toast.makeText(getApplicationContext(),
                        "nave_galery", Toast.LENGTH_LONG).show();
            case R.id.nav_logout:
                logout();
        }
    }

    private void logout() {
        progressDialog.showDialog(getString(R.string.loading));

        RequestService.makeJsonObjectRequest(Request.Method.POST, AppConfig.Urls.LOGOUT, new VolleyStringResponseListener() {
            @Override
            public void onError(VolleyError message) {
                Toast.makeText(getApplicationContext(),
                        message.toString(), Toast.LENGTH_LONG).show();
                progressDialog.hideDialog();
            }

            @Override
            public void onResponse(String response) {
                stopGpsService();
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

