package nohe.nohe_android.activity.customItems;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import nohe.nohe_android.R;
import nohe.nohe_android.activity.controllers.ActivityController;
import nohe.nohe_android.activity.models.ShipmentModel;

public class RVAdapterShipment extends RecyclerView.Adapter<RVAdapterShipment.ShipmentViewHolder> {
    Activity activity;
    ActivityController activityController;

    public static class ShipmentViewHolder extends RecyclerView.ViewHolder {
        TextView from_tv;
        TextView to_tv;
        View view;

        ShipmentViewHolder(View itemView) {
            super(itemView);
            from_tv = (TextView)itemView.findViewById(R.id.shipment_item_from);
            to_tv = (TextView)itemView.findViewById(R.id.shipment_item_to);
            view = itemView;
        }
    }

    List<ShipmentModel> shipments;

    public RVAdapterShipment(List<ShipmentModel> shipments, Activity activity, ActivityController activityController){
        this.shipments = shipments;
        this.activity = activity;
        this.activityController = activityController;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ShipmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shipment_item, parent, false);
        ShipmentViewHolder pvh = new ShipmentViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ShipmentViewHolder auctionViewHolder, final int i) {
        auctionViewHolder.from_tv.setText(shipments.get(i).address_from);
        auctionViewHolder.to_tv.setText(shipments.get(i).address_to);

        auctionViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shipments.get(i).state == ShipmentModel.State.NEW) {
                    activityController.openStartShipmentActivity(shipments.get(i));
                } else {
                    activityController.openInProgressShipmentActivity(shipments.get(i));
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return shipments.size();
    }
}

