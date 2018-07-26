package nohe.nohe_android.activity.controllers;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;

public class ErrorController {
    private AppCompatActivity context;

    public ErrorController(AppCompatActivity context) {
        this.context = context;
    }

    public String getErrorKeyByCode(VolleyError response) {
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
            Toast.makeText(this.context, "Json error: in ErrorController " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return getStringFromResourcesByName("server_error_fatal");
    }

    private String alternativeMessage() {
        return "server_error_fatal";
    }

    private String getStringFromResourcesByName(String resourceName){
        String packageName = this.context.getPackageName();

        int resourceId = this.context.getResources().getIdentifier(resourceName,"string",packageName);

        return this.context.getString(resourceId);
    }
}
