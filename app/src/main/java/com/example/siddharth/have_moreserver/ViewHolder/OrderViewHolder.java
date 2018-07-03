package com.example.siddharth.have_moreserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.siddharth.have_moreserver.Comman.Comman;
import com.example.siddharth.have_moreserver.Interface.ItemClickListener;
import com.example.siddharth.have_moreserver.R;

/**
 * Created by Siddharth on 22-12-2017.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{

    public TextView txtOrderId ,txtOrderStatus,txtOrderPhone,txtOrderAddress;

    private ItemClickListener itemClickListener;


    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderAddress= (TextView) itemView.findViewById(R.id.order_address);
        txtOrderId= (TextView) itemView.findViewById(R.id.order_id);
        txtOrderPhone= (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderStatus= (TextView) itemView.findViewById(R.id.order_status);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener( this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the action");
        menu.add(0,0,getAdapterPosition(), "Update");
        menu.add(0,1,getAdapterPosition(),"Delete");

    }
}