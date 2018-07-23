package nohe.nohe_android.activity.services;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import org.apache.http.entity.mime.MultipartEntity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import nohe.nohe_android.activity.app.AppController;
import nohe.nohe_android.activity.interfaces.VolleyFormResponseListener;
import nohe.nohe_android.activity.interfaces.VolleyStringResponseListener;

public class RequestService {
    public static void makeJsonObjectRequest(int method, String url, final VolleyStringResponseListener listener) {
        StringRequest jsonObjectRequest = new StringRequest
                (method, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(response);
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

                return new VolleyError("Unexpected error");
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

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public static void makeFormDataRequest(int method, String url, final VolleyFormResponseListener listener) {
        final MultipartEntity entity = listener.getData();

        StringRequest jsonObjectRequest = new StringRequest
                (method, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return entity.getContentType().getValue();
            }

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

                return new VolleyError("Unexpected error");
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    entity.writeTo(bos);
                } catch (IOException e) {
                    VolleyLog.e("IOException writing to ByteArrayOutputStream");
                }
                return bos.toByteArray();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return listener.getHeaders();
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
