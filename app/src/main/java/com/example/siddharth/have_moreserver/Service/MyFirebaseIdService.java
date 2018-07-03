package com.example.siddharth.have_moreserver.Service;

import com.example.siddharth.have_moreserver.Comman.Comman;
import com.example.siddharth.have_moreserver.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Siddharth on 24-12-2017.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {

        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        updateToServer(refreshedToken);
        super.onTokenRefresh();
    }

    private void updateToServer(String refreshedToken) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token token=new Token(refreshedToken,true);
        tokens.child(Comman.currentUser.getPhone()).setValue(token);
    }
}
