package nohe.nohe_android.nohe_cz.controllers;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import nohe.nohe_android.nohe_cz.app.AppConfig;

public class ErrorController {
    private AppCompatActivity context;

    public ErrorController(AppCompatActivity context) {
        this.context = context;
    }

    public String getErrorKeyByCode(VolleyError response) {
        if (response instanceof NoConnectionError) {
            return getStringFromResourcesByName("server_error_32");
        }
        if (response.getMessage() == null) {
            return "Json error: in ErrorController no message:" + response.toString();
        }
        if (response.networkResponse != null) {
            if (response.networkResponse.statusCode == 401) {
                return getStringFromResourcesByName("server_error_22");
            }
        }
        try {
            JSONObject jObj = new JSONObject(response.getMessage());
            String code = jObj.getString("errorCode");
            return getStringFromResourcesByName("server_error_" + code);
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            if (!AppConfig.IS_PRODUCTION) {
                Toast.makeText(this.context, "Json error: in ErrorController " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        return getStringFromResourcesByName("server_error_fatal");
    }

    public int getErrorCodeFromResponse(VolleyError response) {
        if (response instanceof NoConnectionError) {
            return 32;
        }
        if (response.getMessage() == null) {
            return 0;
        }
        if (response.networkResponse != null) {
            if (response.networkResponse.statusCode == 401) {
                return 22;
            }
        }
        try {
            JSONObject jObj = new JSONObject(response.getMessage());
            String code = jObj.getString("errorCode");
            return Integer.parseInt(code);
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            if (!AppConfig.IS_PRODUCTION) {
                Toast.makeText(this.context, "Json error: in ErrorController " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        return 0;
    }

    public String getTextByErrorCode(Integer errorCode) {
        String errorString = "server_error_";

        if (isSupportedError(errorCode)) {
            errorString += errorCode.toString();
        } else {
            return "";
        }

        return getStringFromResourcesByName(errorString);
    }

    private boolean isSupportedError(Integer errorCode) {
        switch (errorCode) {
            case 11:
            case 12:
            case 22:
            case 30:
            case 32:
            case 35:
                return true;
            default:
                return false;
        }
    }


    private String alternativeMessage() {
        return "server_error_fatal";
    }

    public String getStringFromResourcesByName(String resourceName){
        String packageName = this.context.getPackageName();

        int resourceId = this.context.getResources().getIdentifier(resourceName,"string",packageName);

        return this.context.getString(resourceId);
    }
}
