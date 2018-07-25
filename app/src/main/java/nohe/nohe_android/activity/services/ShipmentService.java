package nohe.nohe_android.activity.services;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import nohe.nohe_android.activity.app.AppConfig;
import nohe.nohe_android.activity.app.AppController;
import nohe.nohe_android.activity.interfaces.GetCurrentShipment;
import nohe.nohe_android.activity.interfaces.VolleyFormResponseListener;
import nohe.nohe_android.activity.interfaces.VolleyStringResponseListener;
import nohe.nohe_android.activity.models.ShipmentModel;

public class ShipmentService {
    public static void getCurrentService(final GetCurrentShipment listener) {
        StringRequest jsonObjectRequest = new StringRequest
                (Request.Method.GET, AppConfig.Urls.CURRENT_SHIPMENT, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppConfig.ShipmentData.shipment = null;
                        try {
                            if (response.equals("null")) {
                                AppConfig.ShipmentData.shipment = null;
                            } else {
                                JSONObject jObj = new JSONObject(response);
                                AppConfig.ShipmentData.shipment = new ShipmentModel(jObj);
                            }
                            listener.onResponse();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onResponse();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }) {
                        @Override
                        protected Map<String, String> getParams() {
                            return null;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            return listener.getHeaders();
                        }
                    };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
