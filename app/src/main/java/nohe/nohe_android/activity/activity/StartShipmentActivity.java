package nohe.nohe_android.activity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nohe.nohe_android.R;
import nohe.nohe_android.activity.app.AppConfig;
import nohe.nohe_android.activity.interfaces.VolleyStringResponseListener;
import nohe.nohe_android.activity.services.LoginService;
import nohe.nohe_android.activity.services.PagerService;
import nohe.nohe_android.activity.services.ProgressDialogService;
import nohe.nohe_android.activity.services.RequestService;


public class StartShipmentActivity extends AppCompatActivity {
    private Button logoutBtn;
    private Button startShipmentBtn;
    private Button takePhotoBtn;
    private Button btnRemovePhoto;
    private ProgressDialogService progressDialog;
    private LoginService loginService;
    private DrawerLayout mDrawerLayout;
    private EditText id_shipment_tb;
    private EditText code_tb;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ArrayList<Bitmap> photoCollection;
    PagerService pagerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_shipment);

        startShipmentBtn = (Button) findViewById(R.id.start_shipment_btn);
        takePhotoBtn = (Button) findViewById(R.id.take_photo_btn);
        btnRemovePhoto = (Button) findViewById(R.id.btnRemovePhoto);
        id_shipment_tb = (EditText) findViewById(R.id.id_shipment_tb);
        code_tb = (EditText) findViewById(R.id.code_tb);


        loginService = new LoginService(getApplicationContext());
        progressDialog = new ProgressDialogService(this);

        photoCollection = new ArrayList<Bitmap>();

        startShipmentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String id = id_shipment_tb.getText().toString().trim();
                String code = code_tb.getText().toString().trim();

                startShipment(id, code, new String[]{"", ""});
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
                removePhoto();
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
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
        pagerService = new PagerService(getApplicationContext(), photoCollection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            photoCollection.add((Bitmap) extras.get("data"));
            updateImageSwitcher();
        }
    }

    private void removePhoto() {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.photo_show_pager);

        if(photoCollection.size() > 0) {
            this.photoCollection.remove(mViewPager.getCurrentItem());
            updateImageSwitcher();
        }
    }

    private void updateImageSwitcher() {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.photo_show_pager);
        mViewPager.setAdapter(pagerService);
    }

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

    private void openLoginActivity(){
        Intent intent = new Intent(StartShipmentActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void startShipment(final String id_shipment, final String code, final String[] photos) {
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
               //  params.put("photos", "");

                return params;
            }
        });
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
                loginService.logout();
                progressDialog.hideDialog();

                openLoginActivity();
                finish();
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
