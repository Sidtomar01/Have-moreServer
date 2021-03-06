package com.example.siddharth.have_moreserver;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.siddharth.have_moreserver.Comman.Comman;
import com.example.siddharth.have_moreserver.Interface.ItemClickListener;
import com.example.siddharth.have_moreserver.Model.MyResponse;
import com.example.siddharth.have_moreserver.Model.Notification;
import com.example.siddharth.have_moreserver.Model.Request;
import com.example.siddharth.have_moreserver.Model.Sender;
import com.example.siddharth.have_moreserver.Model.Token;
import com.example.siddharth.have_moreserver.Remote.APIService;
import com.example.siddharth.have_moreserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
        FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    FirebaseDatabase db;
    DatabaseReference requests;
    MaterialSpinner spinner;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        db=FirebaseDatabase.getInstance();
        requests=db.getReference("Requests");

        //service
        mService=Comman.getFCMClient();

        //init
        recyclerView= (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();


    }

    private void loadOrders() {

        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Comman.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.setItemClickListener(new ItemClickListener() {


                    @Override
                    public void onClick(View v, int position, boolean isLongClick) {

                    }
                });


            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Comman.UPDATE))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }
        else if(item.getTitle().equals(Comman.DELETE))
        {
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        }


        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
    }

    private void showUpdateDialog(String key, final Request item) {


        AlertDialog.Builder alertDialog=new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update order");
        alertDialog.setMessage("Please Choose the status");


        LayoutInflater inflater=this.getLayoutInflater();
        final View view=inflater.inflate(R.layout.update_order_layout,null);
        spinner= (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","OnTheWay","Shipped");




        alertDialog.setView(view);
        final String localkey=key;
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                requests.child(localkey).setValue(item);

                sendOrderStatusToUser(localkey,item);


            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();




    }

    private void sendOrderStatusToUser(final String key, final  Request item) {
        DatabaseReference tokens=db.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Token token=postSnapshot.getValue(Token.class);
                    //raw payload
                    Notification notification=new Notification("COM EXAMPLE Siddharth","Your Order"+key+"was updated");
                    Sender content=new Sender(token.getToken(),notification);
                    mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.body().success==1)
                            {
                                Toast.makeText(OrderStatus.this, "order was updated", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(OrderStatus.this, "order was updated but failed to send notification", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("ERROR",t.getMessage());
                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
