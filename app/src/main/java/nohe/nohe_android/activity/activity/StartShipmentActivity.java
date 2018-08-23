package nohe.nohe_android.activity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import nohe.nohe_android.R;
import nohe.nohe_android.activity.app.AppConfig;
import nohe.nohe_android.activity.controllers.ActivityController;
import nohe.nohe_android.activity.controllers.ErrorController;
import nohe.nohe_android.activity.controllers.MenuController;
import nohe.nohe_android.activity.controllers.PhotoConverter;
import nohe.nohe_android.activity.controllers.PhotosController;
import nohe.nohe_android.activity.database.DatabaseHelper;
import nohe.nohe_android.activity.models.ShipmentModel;
import nohe.nohe_android.activity.services.LoginService;
import nohe.nohe_android.activity.services.PagerService;
import nohe.nohe_android.activity.services.ProgressDialogService;

public class StartShipmentActivity extends AppCompatActivity {
    private Button startShipmentBtn;
    private Button takePhotoBtn;
    private Button btnRemovePhoto;
    private ImageView opener_menu_btn;
    private NavigationView navigationView;
    private ProgressDialogService progressDialog;
    private LoginService loginService;
    private DrawerLayout mDrawerLayout;
    private TextView shipment_from_vw;
    private TextView shipment_to_vw;
    private TextView shipment_unload_note_vw;
    private TextView shipment_load_note_vw;
    private TextView shipment_price_vw;
    private PhotosController photosController;
    private ActivityController activityController;
    private ErrorController errorController;
    private MenuController menuController;
    static final int REQUEST_TAKE_PHOTO = 2;
    ArrayList<Bitmap> photoCollection;
    PagerService pagerService;
    private PhotoConverter photoConverter;

    private DatabaseHelper database;
    private ShipmentModel shipment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_shipment);
        database = new DatabaseHelper(this);
        shipment = getShipment();
        photoConverter = new PhotoConverter(this);

        /*Text view about shipment*/
        shipment_from_vw = (TextView) findViewById(R.id.shipment_from_vw);
        shipment_to_vw = (TextView) findViewById(R.id.shipment_to_vw);
        shipment_unload_note_vw = (TextView) findViewById(R.id.shipment_unload_note_vw);
        shipment_load_note_vw = (TextView) findViewById(R.id.shipment_load_note_vw);
        shipment_price_vw = (TextView) findViewById(R.id.shipment_price_vw);

        startShipmentBtn = (Button) findViewById(R.id.start_shipment_btn);
        takePhotoBtn = (Button) findViewById(R.id.take_photo_btn);
        btnRemovePhoto = (Button) findViewById(R.id.btnRemovePhoto);
        opener_menu_btn = (ImageView) findViewById(R.id.opener_menu_btn);
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
        setTextsAndPhotos();
    }

    /**
     * Set GUI event
     */
    private void setGuiEvents() {
        startShipmentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startShipment(photoCollection);
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

    private void setTextsAndPhotos() {
        shipment_from_vw.setText(shipment.address_from);
        shipment_to_vw.setText(shipment.address_to);
        shipment_unload_note_vw.setText(shipment.unload_note);
        shipment_load_note_vw.setText(shipment.load_note);
        shipment_price_vw.setText(shipment.price);
    }

    private ShipmentModel getShipment() {
        Bundle bundle = getIntent().getExtras();

        return new ShipmentModel(bundle.getInt("id"),
                bundle.getString("address_from"),
                bundle.getString("address_to"),
                bundle.getString("load_note"),
                bundle.getString("unload_note"),
                bundle.getInt("price"),
                ShipmentModel.State.values()[bundle.getInt("state")],
                bundle.getString("photos_before"),
                bundle.getString("photos_after"),
                bundle.getInt("error_code"));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityController.openListShipmentActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
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
    private void startShipment(ArrayList<Bitmap> bitmaps) {
        ArrayList<String> paths = new ArrayList<>();

        progressDialog.showDialog(getString(R.string.loading));

        shipment.state = ShipmentModel.State.IN_PROGRESS;
        for(Integer i = 0; i < bitmaps.size(); i++) {
            paths.add(photoConverter.saveImageToInternalStorage(bitmaps.get(i),
                    ShipmentModel.State.IN_PROGRESS.toString() + i.toString()));
        }

        shipment.photos_before = TextUtils.join(AppConfig.PHOTOS_DIVIDER, paths);
        database.updateShipment(shipment);

        progressDialog.hideDialog();
        activityController.openListShipmentActivity();
    }
}
