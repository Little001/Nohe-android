package nohe.nohe_android.nohe_cz.activity;

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
import android.widget.Toast;
import com.android.volley.VolleyError;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nohe.nohe_android.R;
import nohe.nohe_android.nohe_cz.app.AppConfig;
import nohe.nohe_android.nohe_cz.controllers.ActivityController;
import nohe.nohe_android.nohe_cz.controllers.ErrorController;
import nohe.nohe_android.nohe_cz.controllers.MenuController;
import nohe.nohe_android.nohe_cz.controllers.PhotoConverter;
import nohe.nohe_android.nohe_cz.customItems.RVAdapterShipment;
import nohe.nohe_android.nohe_cz.database.DatabaseHelper;
import nohe.nohe_android.nohe_cz.interfaces.FinishShipment;
import nohe.nohe_android.nohe_cz.interfaces.GetCurrentShipment;
import nohe.nohe_android.nohe_cz.models.ShipmentModel;
import nohe.nohe_android.nohe_cz.services.DataFile;
import nohe.nohe_android.nohe_cz.services.LocationService;
import nohe.nohe_android.nohe_cz.services.LoginService;
import nohe.nohe_android.nohe_cz.services.ProgressDialogService;
import nohe.nohe_android.nohe_cz.services.ShipmentService;

public class ListShipmentActivity extends AppCompatActivity {
    private ProgressDialogService progressDialog;
    private ActivityController activityController;
    private ErrorController errorController;
    private LoginService loginService;
    private RecyclerView rv_shipment_new;
    private RecyclerView rv_shipment_in_progress;
    private RecyclerView rv_shipment_finish;
    private TextView new_shipments_count_tw;
    private TextView in_progress_shipments_count_tw;
    private TextView finish_shipments_count_tw;
    private Button finish_shipments;
    private Button add_shipment;
    private ImageView opener_menu_btn;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private MenuController menuController;
    private PhotoConverter photoConverter;
    private DatabaseHelper database;
    private Integer counter;
    private Integer countOfFinishingShipments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_list);
        database = new DatabaseHelper(this);
        progressDialog = new ProgressDialogService(this);
        photoConverter = new PhotoConverter(this);
        loginService = new LoginService(getApplicationContext());
        activityController = new ActivityController(this);
        errorController =  new ErrorController(this, activityController, loginService);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        rv_shipment_new = (RecyclerView) findViewById(R.id.rv_shipment_new);
        rv_shipment_in_progress = (RecyclerView) findViewById(R.id.rv_shipment_in_progress);
        rv_shipment_finish = (RecyclerView) findViewById(R.id.rv_shipment_finish);
        new_shipments_count_tw = (TextView) findViewById(R.id.new_shipments_count_tw);
        in_progress_shipments_count_tw = (TextView) findViewById(R.id.in_progress_shipments_count_tw);
        finish_shipments_count_tw = (TextView) findViewById(R.id.finish_shipments_count_tw);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        add_shipment = (Button) findViewById(R.id.add_shipment);
        finish_shipments = (Button) findViewById(R.id.finish_shipments);
        opener_menu_btn = (ImageView) findViewById(R.id.opener_menu_btn);
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
                synchronization();
            }
        });

        add_shipment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                activityController.openAddShipmentActivity(null);
            }
        });
    }

    /**
     * GPS Service
     */
    private void startGpsService() {
        startService(new Intent(getApplicationContext(), LocationService.class));
    }

    private void synchronization() {
        loginService.refreshToken();
        progressDialog.showDialog(getString(R.string.loading));
        finishShipments();
    }

    private void afterSynchronization() {
        showShipments();
        progressDialog.hideDialog();
    }

    private boolean finishingShipmentsIsDone() {
        return countOfFinishingShipments.equals(counter);
    }

    private void getCurrentShipments() {
        if (!finishingShipmentsIsDone()) {
            return;
        }

        ShipmentService.getCurrentService(new GetCurrentShipment() {
            @Override
            public void onResponse(List<ShipmentModel> shipments) {
                database.deleteUselessShipments(shipments);
                if (shipments != null) {
                    database.insertOrUpdateShipments(shipments);
                }
                afterSynchronization();
            }

            @Override
            public void onError(VolleyError message) {
                Toast.makeText(getApplicationContext(),
                        errorController.getTextByErrorCode(errorController.getErrorCodeFromResponse(message)),
                        Toast.LENGTH_LONG)
                        .show();
                afterSynchronization();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + loginService.getToken());
                return header;
            }
        });
    }

    private void finishShipments() {
        List<ShipmentModel> finishedShipments = database.getFinishedShipments();
        counter = 0;
        countOfFinishingShipments = finishedShipments.size();

        for (ShipmentModel shipment : finishedShipments) {
            finishShipment(shipment);
        }
        if (countOfFinishingShipments == 0) {
            getCurrentShipments();
        }
    }

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void finishShipment(final ShipmentModel shipment) {
        String[] photosBeforePaths = shipment.photos_before.split(AppConfig.PHOTOS_DIVIDER);
        String[] photosAfterPaths = shipment.photos_after.split(AppConfig.PHOTOS_DIVIDER);
        final ArrayList<Bitmap> beforeBitmaps = new ArrayList<Bitmap>();
        final ArrayList<Bitmap> afterBitmaps = new ArrayList<Bitmap>();

        for(Integer i = 0; i < photosBeforePaths.length; i++) {
            beforeBitmaps.add(photoConverter.loadImageFromStorage(photosBeforePaths[i], shipment.getDbId().toString() +
                    ShipmentModel.State.IN_PROGRESS.toString() +
                    i.toString()));
        }

        for(Integer i = 0; i < photosAfterPaths.length; i++) {
            afterBitmaps.add(photoConverter.loadImageFromStorage(photosAfterPaths[i], shipment.getDbId().toString() +
                    ShipmentModel.State.DONE.toString() +
                    i.toString()));
        }

        ShipmentService.finishShipmentService(new FinishShipment() {
            @Override
            public void onResponse() {
                counter++;
                deleteShipmentWithPhotos(shipment);
                getCurrentShipments();
            }

            @Override
            public void onError(VolleyError message) {
                counter++;
                shipment.error_code = errorController.getErrorCodeFromResponse(message);
                database.updateShipment(shipment);
                getCurrentShipments();
            }

            @Override
            public Map<String, DataFile> getByteData() {
                Map<String, DataFile> params = new HashMap<>();
                long imageName = System.currentTimeMillis();
                for (int i = 0; i < beforeBitmaps.size(); i++) {
                    params.put("before" + AppConfig.PHOTOS_DIVIDER_FOR_SERVER + i,
                            new DataFile(imageName + i + ".jpg", getFileDataFromDrawable(beforeBitmaps.get(i))));
                }
                for (int i = 0; i < afterBitmaps.size(); i++) {
                    params.put("after" + AppConfig.PHOTOS_DIVIDER_FOR_SERVER + i,
                            new DataFile(imageName + i + ".jpg", getFileDataFromDrawable(afterBitmaps.get(i))));
                }
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("id_shipment", shipment.ID.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> header = new HashMap<String, String>();
                header.put("Authorization", "Bearer " + loginService.getToken());
                return header;
            }
        });
    }

    private void showShipments() {
        List<ShipmentModel> allShipments = database.getAllShipments();
        List<ShipmentModel> newShipments = new ArrayList<>();
        List<ShipmentModel> shipmentsInProgress = new ArrayList<>();
        List<ShipmentModel> finishShipments = new ArrayList<>();

        for (ShipmentModel shipment : allShipments) {
            switch (shipment.state) {
                case NEW:
                    newShipments.add(shipment);
                    break;
                case IN_PROGRESS:
                    shipmentsInProgress.add(shipment);
                    break;
                case DONE:
                    finishShipments.add(shipment);
                    break;
            }
        }

        new_shipments_count_tw.setText(String.valueOf(newShipments.size()));
        in_progress_shipments_count_tw.setText(String.valueOf(shipmentsInProgress.size()));
        finish_shipments_count_tw.setText(String.valueOf(finishShipments.size()));

        setRecyclerView(rv_shipment_new, newShipments);
        setRecyclerView(rv_shipment_in_progress, shipmentsInProgress);
        setRecyclerView(rv_shipment_finish, finishShipments);
    }

    private void setRecyclerView(RecyclerView rv_shipment, List<ShipmentModel> shipments) {
        rv_shipment.setClickable(true);

        RecyclerView.Adapter adapter;
        adapter = new RVAdapterShipment(shipments, this, activityController, errorController);
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
            File f = new File(photosBeforePaths[i],
                    shipment.getDbId().toString() +
                    ShipmentModel.State.IN_PROGRESS.toString() +
                    i.toString() + ".jpg");
            boolean deleted = f.delete();
        }
        for(Integer i = 0; i < photosAfterPaths.length; i++) {
            File f = new File(photosAfterPaths[i],
                    shipment.getDbId().toString() +
                            ShipmentModel.State.DONE.toString() +
                            i.toString() + ".jpg");
            boolean deleted = f.delete();
        }

        database.deleteShipment(shipment);
    }
}
