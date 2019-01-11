package nohe.nohe_android.nohe_cz.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import java.util.HashMap;
import java.util.Map;
import nohe.nohe_android.nohe_cz.app.AppConfig;
import nohe.nohe_android.nohe_cz.interfaces.VolleyStringResponseListener;

public class LocationService extends Service {
    private static final String TAG = "GPS tracker";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 30000;
    private static final float LOCATION_DISTANCE = 0f;
    LoginService loginService;

    public class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            sendLocationData(location);
            if (!AppConfig.IS_PRODUCTION) {
                Toast.makeText(getApplicationContext(),
                    "onLocationChanged: " + location, Toast.LENGTH_SHORT).show();
            }

            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
            if (!AppConfig.IS_PRODUCTION) {
                Toast.makeText(getApplicationContext(),
                        "on gps provider disabled", Toast.LENGTH_SHORT).show();
            }
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
            if (!AppConfig.IS_PRODUCTION) {
                Toast.makeText(getApplicationContext(),
                        "on gps provider enabled", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
            if (!AppConfig.IS_PRODUCTION) {
                Toast.makeText(getApplicationContext(),
                        "on fps status changed", Toast.LENGTH_SHORT).show();
            }
        }

        private void sendLocationData(final Location location) {
            final String gps = location.getLatitude() + "," + location.getLongitude();
            RequestService.makeJsonObjectRequest(Request.Method.POST, AppConfig.Urls.CURRENT_POSITION, new VolleyStringResponseListener() {
                @Override
                public void onError(VolleyError response) {
                    if (response instanceof NoConnectionError) {
                        Toast.makeText(getApplicationContext(),
                                "Chyba připojení k internetu", Toast.LENGTH_SHORT).show();
                    }
                    if (!AppConfig.IS_PRODUCTION) {
                        Toast.makeText(getApplicationContext(),
                                response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (response.networkResponse != null) {
                        if (response.networkResponse.statusCode == 401) {
                            Toast.makeText(getApplicationContext(),
                                    "Vaše přihlášení vypršelo.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onResponse(String response) {
                    if (!AppConfig.IS_PRODUCTION) {
                        Toast.makeText(getApplicationContext(),
                                "location data sent to server", Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "location data sent to server");
                    // loginService.refreshToken();
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<String, String>();
                    header.put("Authorization", "Bearer " + loginService.getToken());
                    return header;
                }

                @Override
                public Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("position", gps);
                    return params;
                }
            });
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!AppConfig.IS_PRODUCTION) {
            Toast.makeText(getApplication(), "Location sending started", Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        loginService = new LoginService(this);

        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
            if (!AppConfig.IS_PRODUCTION) {
                Toast.makeText(getApplication(), "fail to request location update, ignore", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
            if (!AppConfig.IS_PRODUCTION) {
                Toast.makeText(getApplication(), "network provider does not exist", Toast.LENGTH_SHORT).show();
            }
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
            if (!AppConfig.IS_PRODUCTION) {
                Toast.makeText(getApplication(), "fail to request location update, ignore", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException ex) {
            if (!AppConfig.IS_PRODUCTION) {
                Toast.makeText(getApplication(), "gps provider does not exist", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        if (!AppConfig.IS_PRODUCTION) {
            Toast.makeText(getApplication(), "Location sending stopped", Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}

