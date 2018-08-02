package nohe.nohe_android.activity.services;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import nohe.nohe_android.activity.app.AppConfig;
import nohe.nohe_android.activity.app.AppController;
import nohe.nohe_android.activity.interfaces.GetCurrentShipment;
import nohe.nohe_android.activity.models.ShipmentModel;

public class ShipmentService {
    public static void getCurrentService(final GetCurrentShipment listener) {
        StringRequest jsonObjectRequest = new StringRequest
                (Request.Method.GET, AppConfig.Urls.CURRENT_SHIPMENT, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response.equals("null")) {
                                listener.onResponse(null);
                            } else {
                                JSONArray jsonarray = new JSONArray(response);

                                Gson gson = new Gson();
                                Type type = new TypeToken<List<ShipmentModel>>(){}.getType();

                                gson.fromJson(jsonarray.toString(), type);
                                List<ShipmentModel> shipments = gson.fromJson(jsonarray.toString(), type);

                                listener.onResponse(shipments);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            try {
                                listener.onResponse(null);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
