package com.example.siddharth.have_moreserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.siddharth.have_moreserver.Comman.Comman;
import com.example.siddharth.have_moreserver.Interface.ItemClickListener;
import com.example.siddharth.have_moreserver.Model.Category;

import com.example.siddharth.have_moreserver.Model.Token;
import com.example.siddharth.have_moreserver.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView txtfullname;
    //firebase
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageRefrence;
    DatabaseReference catagories;
    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
//view
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    MaterialEditText edtName,edtPrice,edtMenuId;
    FButton btnSelect,btnUpload;
    Category newCategory;
    Uri saveuri;
    private final int PICK_IMAGE_REQUEST=71;
    DrawerLayout drawer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);

        database=FirebaseDatabase.getInstance();
        catagories=database.getReference("Category");
            storage=FirebaseStorage.getInstance();
        storageRefrence=storage.getReference();







        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //set name
        View HeaderView=navigationView.getHeaderView(0);
        txtfullname= (TextView) HeaderView.findViewById(R.id.textFullName);
        txtfullname.setText(Comman.currentUser.getName());



        ///initView
        recycler_menu= (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);




        loadMenu();

        updateToken(FirebaseInstanceId.getInstance().getToken());




    }

    private void updateToken(String token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,true);
        tokens.child(Comman.currentUser.getPhone()).setValue(data);
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add New Category");
        alertDialog.setMessage("Please Fill Full information");
        LayoutInflater inflater=this.getLayoutInflater();
        View add_menu_layout=inflater.inflate(R.layout.add_new_layout_resource_file,null);
        edtName= (MaterialEditText) add_menu_layout.findViewById(R.id.edtName);
        edtPrice= (MaterialEditText) add_menu_layout.findViewById(R.id.edtPrice);
        edtMenuId= (MaterialEditText) add_menu_layout.findViewById(R.id.edtMenuId);
        btnSelect= (FButton) add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload= (FButton) add_menu_layout.findViewById(R.id.btnUpload);
        //btn ke liye listener


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();//letUs select image from the gallery;
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });




        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        ///setButton

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();;
              if(newCategory!=null)
              {
                  catagories.push().setValue(newCategory);
                  Snackbar.make(drawer,"New Category  "+newCategory.getName()+"is added",Snackbar.LENGTH_SHORT).show();

              }


            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();;

            }
        });alertDialog.show();


    }

    private void uploadImage() {

        if(saveuri!=null)
        {
            final ProgressDialog pd=new ProgressDialog(this);
            pd.setMessage("UPLOADING!!!!!!!");
            pd.show();;


            String imageNmae= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageRefrence.child("image/"+imageNmae);
            imageFolder.putFile(saveuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();;
                    Toast.makeText(Home.this, "Image uploaded!! ;D", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newCategory=new Category();
                            newCategory.setName(edtName.getText().toString());
                            newCategory.setMenuId(edtMenuId.getText().toString());
                            newCategory.setPrice(edtPrice.getText().toString());
                            newCategory.setImage(uri.toString());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    pd.setMessage("uploded"+progress+"%");

                }
            });

        }


    }

    //

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null &&data.getData()!=null)
        {
            saveuri=data.getData();
            btnSelect.setText("Image Selected!");
        }
    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select Pitcure"),PICK_IMAGE_REQUEST);



    }

    private void loadMenu() {

        adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,catagories) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View v, int position, boolean isLongClick) {

                    }
                });

            }
        };
        adapter.notifyDataSetChanged();//refresh data if have data changed
        recycler_menu.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
           // Intent intent=new Intent(Home.this,Cart.class);
            //startActivity(intent);

        } else if (id == R.id.nav_orders) {
           Intent intent=new Intent(Home.this, OrderStatus.class);
           startActivity(intent);

        } else if (id == R.id.log_out) {

            Intent intent=new Intent(Home.this,SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Comman.UPDATE))
        {
            showuUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }
        else  if(item.getTitle().equals(Comman.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());

        }



        return super.onContextItemSelected(item);

    }

    private void deleteCategory(String key) {
        catagories.child(key).removeValue();
        Toast.makeText(this, "Item deleted !!!!", Toast.LENGTH_SHORT).show();

    }

    private void showuUpdateDialog(final String key, final Category item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update Category");
        alertDialog.setMessage("Please Fill Full information");
        LayoutInflater inflater=this.getLayoutInflater();
        View add_menu_layout=inflater.inflate(R.layout.add_new_layout_resource_file,null);
        edtName= (MaterialEditText) add_menu_layout.findViewById(R.id.edtName);
        edtPrice= (MaterialEditText) add_menu_layout.findViewById(R.id.edtPrice);
        edtMenuId= (MaterialEditText) add_menu_layout.findViewById(R.id.edtMenuId);
        btnSelect= (FButton) add_menu_layout.findViewById(R.id.btnSelect);
        btnUpload= (FButton) add_menu_layout.findViewById(R.id.btnUpload);
        //btn ke liye listener


        //
        edtName.setText(item.getName());
        edtPrice.setText(item.getPrice());
        edtMenuId.setText(item.getMenuId());


        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();//letUs select image from the gallery;
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               changeImage(item);
            }
        });




        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        ///setButton

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();;

                  item.setName(edtName.getText().toString());
                item.setMenuId(edtMenuId.getText().toString());
                item.setPrice(edtPrice.getText().toString());
                    catagories.child(key).setValue(item);
                    Snackbar.make(drawer,"New Category  "+item.getName()+"is edited",Snackbar.LENGTH_SHORT).show();






            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();;

            }
        });alertDialog.show();

    }

    private void changeImage(final Category item) {
        if(saveuri!=null)
        {
            final ProgressDialog pd=new ProgressDialog(this);
            pd.setMessage("UPLOADING!!!!!!!");
            pd.show();;


            String imageNmae= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageRefrence.child("image/"+imageNmae);
            imageFolder.putFile(saveuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();;
                    Toast.makeText(Home.this, "Image uploaded!! ;D", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                          /*  newCategory=new Category();
                            newCategory.setName(edtName.getText().toString());
                            newCategory.setMenuId(edtMenuId.getText().toString());
                            newCategory.setMenuId(edtPrice.getText().toString());
                            newCategory.setImage(uri.toString());*/
                          item.setImage(uri.toString());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    pd.setMessage("uploded"+progress+"%");

                }
            });

        }

    }


}
