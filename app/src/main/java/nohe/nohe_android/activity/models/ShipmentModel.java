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
    public State state;
    public String photos_before;
    public String photos_after;
    public Integer error_code;
    public boolean local;

    public ShipmentModel(Integer id_shipment, String address_from, String address_to,
                         String load_note, String unload_note, Integer price, State state,
                         String photos_before, String photos_after, Integer errorCode, boolean local) {
        this.ID = id_shipment;
        this.address_from = address_from;
        this.address_to = address_to;
        this.load_note = load_note;
        this.unload_note = unload_note;
        this.price = price;
        this.state = state;
        this.photos_before = photos_before;
        this.photos_after = photos_after;
        this.error_code = errorCode;
        this.local = local;
    }

    public ShipmentModel(JSONObject data) {
        this.parseJsonData(data);
    }

    public enum State {
        NEW(1),
        DONE(2),
        IN_PROGRESS(3);

        public final Integer state;

        State(Integer state) {
            this.state = state;
        }

        public int getValue() {
            return state;
        }

        public static State fromValue(int value) {
            for (State my: State.values()) {
                if (my.state == value) {
                    return my;
                }
            }

            return null;
        }
    }


    private void parseJsonData(JSONObject data) {
        try {
            this.ID = data.getInt("ID");
            this.address_from = data.getString("address_from");
            this.address_to = data.getString("address_to");
            this.load_note = data.getString("load_note");
            this.unload_note = data.getString("unload_note");
            this.price = data.getInt("price");
            this.photos_before = "";
            this.photos_after = "";
            this.error_code = 0;
        } catch (JSONException ex) {

        }
    }
}
