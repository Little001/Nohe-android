package nohe.nohe_android.nohe_cz.customItems;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import nohe.nohe_android.R;
import nohe.nohe_android.nohe_cz.controllers.ActivityController;
import nohe.nohe_android.nohe_cz.controllers.ErrorController;
import nohe.nohe_android.nohe_cz.models.ShipmentModel;

public class RVAdapterShipment extends RecyclerView.Adapter<RVAdapterShipment.ShipmentViewHolder> {
    Activity activity;
    ActivityController activityController;
    ErrorController errorController;

    public static class ShipmentViewHolder extends RecyclerView.ViewHolder {
        TextView from_tv;
        TextView to_tv;
        TextView shipment_error;
        View view;
        Button edit_shipment_btn;

        ShipmentViewHolder(View itemView) {
            super(itemView);
            from_tv = (TextView)itemView.findViewById(R.id.shipment_item_from);
            to_tv = (TextView)itemView.findViewById(R.id.shipment_item_to);
            shipment_error = (TextView)itemView.findViewById(R.id.shipment_error);
            edit_shipment_btn = (Button)itemView.findViewById(R.id.edit_shipment_btn);
            view = itemView;
        }
    }

    List<ShipmentModel> shipments;

    public RVAdapterShipment(List<ShipmentModel> shipments, Activity activity, ActivityController activityController, ErrorController errorController){
        this.shipments = shipments;
        this.activity = activity;
        this.activityController = activityController;
        this.errorController = errorController;
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
        final ShipmentModel shipment = shipments.get(i);

        auctionViewHolder.from_tv.setText(shipment.address_from);
        auctionViewHolder.to_tv.setText(shipment.address_to);
        auctionViewHolder.shipment_error.setText(errorController.getTextByErrorCode(shipment.error_code));

        if (!shipment.local) {
            auctionViewHolder.edit_shipment_btn.setVisibility(View.INVISIBLE);
        }

        auctionViewHolder.edit_shipment_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                activityController.openAddShipmentActivity(shipment.ID);
            }
        });

        auctionViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shipment.state == ShipmentModel.State.NEW) {
                    activityController.openStartShipmentActivity(shipment);
                } else if(shipment.state == ShipmentModel.State.IN_PROGRESS) {
                    activityController.openInProgressShipmentActivity(shipment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return shipments.size();
    }
}

