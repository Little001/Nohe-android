package nohe.nohe_android.activity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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
import nohe.nohe_android.activity.controllers.ErrorController;
import nohe.nohe_android.activity.controllers.MenuController;
import nohe.nohe_android.activity.controllers.PhotosController;
import nohe.nohe_android.activity.interfaces.VolleyStringResponseListener;
import nohe.nohe_android.activity.services.LoginService;
import nohe.nohe_android.activity.services.PagerService;
import nohe.nohe_android.activity.services.ProgressDialogService;
import nohe.nohe_android.activity.services.RequestService;

public class StartShipmentActivity extends AppCompatActivity {
    private Button startShipmentBtn;
    private Button takePhotoBtn;
    private Button btnRemovePhoto;
    private Button opener_menu_btn;
    private NavigationView navigationView;
    private ProgressDialogService progressDialog;
    private LoginService loginService;
    private DrawerLayout mDrawerLayout;
    private EditText id_shipment_tb;
    private EditText code_tb;
    private PhotosController photosController;
    private ActivityController activityController;
    private ErrorController errorController;
    private MenuController menuController;
    static final int REQUEST_TAKE_PHOTO = 2;
    ArrayList<Bitmap> photoCollection;
    PagerService pagerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_shipment);

        startShipmentBtn = (Button) findViewById(R.id.start_shipment_btn);
        takePhotoBtn = (Button) findViewById(R.id.take_photo_btn);
        btnRemovePhoto = (Button) findViewById(R.id.btnRemovePhoto);
        opener_menu_btn = (Button) findViewById(R.id.opener_menu_btn);
        id_shipment_tb = (EditText) findViewById(R.id.id_shipment_tb);
        code_tb = (EditText) findViewById(R.id.code_tb);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        loginService = new LoginService(getApplicationContext());
        progressDialog = new ProgressDialogService(this);
        photoCollection = new ArrayList<Bitmap>();
        pagerService = new PagerService(getApplicationContext(), photoCollection);

        photosController = new PhotosController(this, pagerService, photoCollection, takePhotoBtn, startShipmentBtn, btnRemovePhoto);
        activityController = new ActivityController(this);
        errorController =  new ErrorController(this);
        menuController = new MenuController(this, navigationView, mDrawerLayout, progressDialog, loginService, activityController, errorController, opener_menu_btn);

        setGuiEvents();
    }

    /**
     * Set GUI event
     */
    private void setGuiEvents() {
        startShipmentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String id = id_shipment_tb.getText().toString().trim();
                String code = code_tb.getText().toString().trim();
                if (!id.isEmpty() && !code.isEmpty()) {
                    startShipment(id, code, photosController.getPhotosInBase64(photoCollection));
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter code and id!", Toast.LENGTH_LONG)
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
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityController.openListShipmentActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Shipment methods
     */
    private void startShipment(final String id_shipment, final String code, final String[] photos) {
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
                params.put("id_shipment", id_shipment);
                params.put("code", code);
                params.put("photos",  Arrays.deepToString(photos));

                return params;
            }
        });
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
}
