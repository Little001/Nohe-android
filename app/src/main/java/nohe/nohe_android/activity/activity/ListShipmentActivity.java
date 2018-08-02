package nohe.nohe_android.activity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nohe.nohe_android.R;
import nohe.nohe_android.activity.app.AppConfig;
import nohe.nohe_android.activity.controllers.ActivityController;
import nohe.nohe_android.activity.controllers.ErrorController;
import nohe.nohe_android.activity.customItems.RVAdapterShipment;
import nohe.nohe_android.activity.interfaces.GetCurrentShipment;
import nohe.nohe_android.activity.interfaces.VolleyStringResponseListener;
import nohe.nohe_android.activity.models.ShipmentModel;
import nohe.nohe_android.activity.services.CurrentShipmentService;
import nohe.nohe_android.activity.services.LocationService;
import nohe.nohe_android.activity.services.LoginService;
import nohe.nohe_android.activity.services.ProgressDialogService;
import nohe.nohe_android.activity.services.RequestService;
import nohe.nohe_android.activity.services.ShipmentService;

public class ListShipmentActivity extends AppCompatActivity {
    private ProgressDialogService progressDialog;
    private CurrentShipmentService currentShipmentService;
    private ActivityController activityController;
    private ErrorController errorController;
    private LoginService loginService;
    private RecyclerView rv_shipment;
    private Button start_shipment_btn;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_list);

        progressDialog = new ProgressDialogService(this);
        loginService = new LoginService(getApplicationContext());
        currentShipmentService = new CurrentShipmentService(getApplicationContext());
        activityController = new ActivityController(this);
        errorController =  new ErrorController(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        rv_shipment = (RecyclerView) findViewById(R.id.rv_shipment);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        start_shipment_btn = (Button) findViewById(R.id.start_shipment_btn);
        this.getCurrentShipments();
        setGuiEvents();
        startGpsService();
    }

    /**
     * Set GUI event
     */
    private void setGuiEvents() {
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

        start_shipment_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                activityController.openStartShipmentActivity();
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

    /**
     * GPS Service
     */
    private void startGpsService() {
        startService(new Intent(getApplicationContext(), LocationService.class));
    }

    private void stopGpsService() {
        stopService(new Intent(getApplicationContext(), LocationService.class));
    }

    private void getCurrentShipments() {
        progressDialog.showDialog(getString(R.string.loading));

        ShipmentService.getCurrentService(new GetCurrentShipment() {
            @Override
            public void onResponse(List<ShipmentModel> shipments) throws JSONException {
                currentShipmentService.unSetShipments();
                if (shipments != null) {
                    currentShipmentService.setShipments(shipments);
                    updateContent();
                }
                progressDialog.hideDialog();
            }

            @Override
            public void onError(VolleyError message) {
                Toast.makeText(getApplicationContext(),
                        errorController.getErrorKeyByCode(message), Toast.LENGTH_LONG).show();
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

    private void updateContent() throws JSONException {
        rv_shipment.setClickable(true);

        RecyclerView.Adapter adapter;
        adapter = new RVAdapterShipment(currentShipmentService.getShipments(), this, activityController);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        rv_shipment.setLayoutManager(llm);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rv_shipment.getContext(),
                llm.getOrientation());
        rv_shipment.addItemDecoration(mDividerItemDecoration);

        rv_shipment.setAdapter(adapter);
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
