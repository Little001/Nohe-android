package nohe.nohe_android.activity.controllers;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

public class ErrorController {
    private AppCompatActivity context;

    public ErrorController(AppCompatActivity context) {
        this.context = context;
    }
    public String getErrorKeyByCode(String response) {
        try {
            JSONObject jObj = new JSONObject(response);
            String code = jObj.getString("errorCode");
            return getStringFromResourcesByName("server_error_" + code);
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            Toast.makeText(this.context, "Json error: in ErrorController " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return getStringFromResourcesByName("server_error_fatal");
    }

    private String getStringFromResourcesByName(String resourceName){
        String packageName = this.context.getPackageName();

        int resourceId = this.context.getResources().getIdentifier(resourceName,"string",packageName);

        return this.context.getString(resourceId);
    }
}
