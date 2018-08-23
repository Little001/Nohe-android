package nohe.nohe_android.activity.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.VolleyError;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nohe.nohe_android.R;
import nohe.nohe_android.activity.app.AppConfig;
import nohe.nohe_android.activity.controllers.ActivityController;
import nohe.nohe_android.activity.controllers.ErrorController;
import nohe.nohe_android.activity.controllers.MenuController;
import nohe.nohe_android.activity.controllers.PhotoConverter;
import nohe.nohe_android.activity.customItems.RVAdapterShipment;
import nohe.nohe_android.activity.database.DatabaseHelper;
import nohe.nohe_android.activity.interfaces.FinishShipment;
import nohe.nohe_android.activity.interfaces.GetCurrentShipment;
import nohe.nohe_android.activity.models.ShipmentModel;
import nohe.nohe_android.activity.services.LocationService;
import nohe.nohe_android.activity.services.LoginService;
import nohe.nohe_android.activity.services.ProgressDialogService;
import nohe.nohe_android.activity.services.ShipmentService;

public class ListShipmentActivity extends AppCompatActivity {
    private ProgressDialogService progressDialog;
    private ActivityController activityController;
    private ErrorController errorController;
    private LoginService loginService;
    private RecyclerView rv_shipment;
    private Button get_shipments;
    private Button finish_shipments;
    private Button add_shipment;
    private ImageView opener_menu_btn;
    private TextView no_shipments;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private MenuController menuController;
    private PhotoConverter photoConverter;
    private DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_list);
        database = new DatabaseHelper(this);
        progressDialog = new ProgressDialogService(this);
        photoConverter = new PhotoConverter(this);
        loginService = new LoginService(getApplicationContext());
        activityController = new ActivityController(this);
        errorController =  new ErrorController(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        rv_shipment = (RecyclerView) findViewById(R.id.rv_shipment);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        get_shipments = (Button) findViewById(R.id.get_shipments);
        add_shipment = (Button) findViewById(R.id.add_shipment);
        finish_shipments = (Button) findViewById(R.id.finish_shipments);
        opener_menu_btn = (ImageView) findViewById(R.id.opener_menu_btn);
        no_shipments = (TextView) findViewById(R.id.no_shipments);
        setGuiEvents();
        startGpsService();
        menuController = new MenuController(this, navigationView, mDrawerLayout, progressDialog, loginService, activityController, errorController, opener_menu_btn);
        showShipments();
    }

    /**
     * Set GUI event
     */
    private void setGuiEvents() {
        finish_shipments.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finishShipments();
            }
        });

        get_shipments.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                getCurrentShipments();
            }
        });

        add_shipment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                addShipment();
            }
        });
    }

    /**
     * GPS Service
     */
    private void startGpsService() {
        startService(new Intent(getApplicationContext(), LocationService.class));
    }

    private void addShipment() {
        Integer ID = 5;
        String to = "Olomouc";

        ShipmentModel shipment = new ShipmentModel(5, "Olomouc",
                "Prerov", "naklad", "vyklad",
                800, ShipmentModel.State.NEW, "", "", 0);

        database.insertShipment(shipment);
    }

    private void getCurrentShipments() {
        progressDialog.showDialog(getString(R.string.loading));

        ShipmentService.getCurrentService(new GetCurrentShipment() {
            @Override
            public void onResponse(List<ShipmentModel> shipments) {
                database.deleteAllShipments();
                if (shipments != null) {
                    database.insertShipments(shipments);
                    showShipments();
                } else {
                    no_shipments.setVisibility(View.VISIBLE);
                }
                progressDialog.hideDialog();
            }

            @Override
            public void onError(VolleyError message) {
                progressDialog.hideDialog();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Token", loginService.getToken());
                return header;
            }
        });
    }

    private void finishShipments() {
        progressDialog.showDialog(getString(R.string.loading));

        List<ShipmentModel> finishedShipments = database.getFinishedShipments();

        for (ShipmentModel shipment : finishedShipments) {
            finishShipment(shipment);
        }

        progressDialog.hideDialog();
    }

    private void finishShipment(final ShipmentModel shipment) {
        final String[] photosBeforePaths = TextUtils.split(shipment.photos_before, AppConfig.PHOTOS_DIVIDER);
        final String[] photosAfterPaths = TextUtils.split(shipment.photos_after, AppConfig.PHOTOS_DIVIDER);
        final ArrayList<Bitmap> beforeBitmaps = new ArrayList<Bitmap>();
        final ArrayList<Bitmap> afterBitmaps = new ArrayList<Bitmap>();

        for(Integer i = 0; i < photosBeforePaths.length; i++) {
            beforeBitmaps.add(photoConverter.loadImageFromStorage(photosBeforePaths[i], i.toString()));
        }

        for(Integer i = 0; i < photosAfterPaths.length; i++) {
            afterBitmaps.add(photoConverter.loadImageFromStorage(photosAfterPaths[i], i.toString()));
        }

        ShipmentService.finishShipmentService(new FinishShipment() {
            @Override
            public void onResponse() {
                deleteShipmentWithPhotos(shipment);
            }

            @Override
            public void onError(VolleyError message) {
                shipment.error_code = 5;
                database.updateShipment(shipment);
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("id_shipment", shipment.ID.toString());
                params.put("photos_before",  Arrays.deepToString(photoConverter.getPhotosInBase64(beforeBitmaps)));
                params.put("photos_after",  Arrays.deepToString(photoConverter.getPhotosInBase64(afterBitmaps)));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Token", loginService.getToken());
                return header;
            }
        });
    }

    private void showShipments() {
        rv_shipment.setClickable(true);

        RecyclerView.Adapter adapter;
        adapter = new RVAdapterShipment(database.getAllShipments(), this, activityController);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        rv_shipment.setLayoutManager(llm);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rv_shipment.getContext(),
                llm.getOrientation());
        rv_shipment.addItemDecoration(mDividerItemDecoration);

        rv_shipment.setAdapter(adapter);
    }

    private void deleteShipmentWithPhotos(ShipmentModel shipment) {
        String[] photosBeforePaths = TextUtils.split(shipment.photos_before, AppConfig.PHOTOS_DIVIDER);
        String[] photosAfterPaths = TextUtils.split(shipment.photos_after, AppConfig.PHOTOS_DIVIDER);

        for(Integer i = 0; i < photosBeforePaths.length; i++) {
            File f = new File(photosBeforePaths[i], i.toString() + ".jpg");
            boolean deleted = f.delete();
        }
        for(Integer i = 0; i < photosAfterPaths.length; i++) {
            File f = new File(photosAfterPaths[i], i.toString() + ".jpg");
            boolean deleted = f.delete();
        }

        database.deleteShipment(shipment);
    }
}
