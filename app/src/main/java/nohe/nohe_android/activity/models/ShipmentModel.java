package nohe.nohe_android.activity.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ShipmentModel implements Serializable {
    public Integer ID;
    public String address_from;
    public String address_to;
    public String load_note;
    public String unload_note;
    public Integer price;

    public ShipmentModel(JSONObject data) {
        this.parseJsonData(data);
    }

    private void parseJsonData(JSONObject data) {
        try {
            this.ID = data.getInt("ID");
            this.address_from = data.getString("address_from");
            this.address_to = data.getString("address_to");
            this.load_note = data.getString("load_note");
            this.unload_note = data.getString("unload_note");
            this.price = data.getInt("price");
        } catch (JSONException ex) {

        }
    }
}
