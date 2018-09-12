package nohe.nohe_android.nohe_cz.services;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import nohe.nohe_android.nohe_cz.app.AppConfig;
import nohe.nohe_android.nohe_cz.app.AppController;
import nohe.nohe_android.nohe_cz.interfaces.FinishShipment;
import nohe.nohe_android.nohe_cz.interfaces.GetCurrentShipment;
import nohe.nohe_android.nohe_cz.models.ShipmentModel;

public class ShipmentService {
    public static void finishShipmentService(final FinishShipment listener) {
        StringRequest jsonObjectRequest = new StringRequest
                (Request.Method.POST, AppConfig.Urls.FINISH_SHIPMENTS, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResponse();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    return Response.success(jsonString,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    return error;
                }

                return volleyError;
            }
            @Override
            protected Map<String, String> getParams() {
                return listener.getParams();
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


    public static void getCurrentService(final GetCurrentShipment listener) {
        StringRequest jsonObjectRequest = new StringRequest
                (Request.Method.GET, AppConfig.Urls.CURRENT_SHIPMENTS, new Response.Listener<String>() {
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
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            try {
                                String jsonString = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                return Response.success(jsonString,
                                        HttpHeaderParser.parseCacheHeaders(response));
                            } catch (UnsupportedEncodingException e) {
                                return Response.error(new ParseError(e));
                            }
                        }
                        @Override
                        protected VolleyError parseNetworkError(VolleyError volleyError) {
                            if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                                VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                                return error;
                            }

                            return volleyError;
                        }
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
