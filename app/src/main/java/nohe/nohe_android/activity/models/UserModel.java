package nohe.nohe_android.activity.models;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {
    public Integer ID;
    public String username;
    public String name;
    public String surname;
    public Integer role;

    public UserModel(JSONObject data) {
        this.parseJsonData(data);
    }

    private void parseJsonData(JSONObject data) {
        try {
            this.ID = data.getInt("ID");
            this.username = data.getString("username");
            this.name = data.getString("name");
            this.surname = data.getString("surname");
            this.role = data.getInt("role");
        } catch (JSONException ex) {

        }
    }
}
