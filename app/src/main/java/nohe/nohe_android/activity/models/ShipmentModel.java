package nohe.nohe_android.activity.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ShipmentModel implements Serializable {
    public Integer ID;
    public AddressModel from;
    public AddressModel to;
    public String load_note;
    public String unload_note;
    public Integer price;

    public ShipmentModel(JSONObject data) {
        this.parseJsonData(data);
    }

    private void parseJsonData(JSONObject data) {
        try {
            this.ID = data.getInt("ID");
            this.from = new AddressModel(
                    data.getJSONObject("from").getString("city"),
                    data.getJSONObject("from").getString("street"));
            this.to = new AddressModel(
                    data.getJSONObject("to").getString("city"),
                    data.getJSONObject("to").getString("street"));
            this.load_note = data.getString("load_note");
            this.unload_note = data.getString("unload_note");
            this.price = data.getInt("price");
        } catch (JSONException ex) {

        }
    }

    public class AddressModel implements Serializable {
        private String city;
        private String street;

        public AddressModel(String city, String street) {
            this.city = city;
            this.street = street;
        }
    }
}
