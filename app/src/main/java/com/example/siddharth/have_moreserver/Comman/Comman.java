package com.example.siddharth.have_moreserver.Comman;

import com.example.siddharth.have_moreserver.Model.User;
import com.example.siddharth.have_moreserver.Remote.APIService;
import com.example.siddharth.have_moreserver.Remote.RetrofitClient;

import retrofit2.Retrofit;

/**
 * Created by Siddharth on 21-12-2017.
 */

public class Comman {
    public static final String BASE_URL="http://fcm.googleapis.com/";
    public static APIService getFCMClient()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static User currentUser;
    public static final String UPDATE="Update";
    public static final String DELETE="Delete";
    public static String convertCodeToStatus(String status) {

        if(status.equals("0"))
        {
            return "Placed";
        }
        else if (status.equals("1"))
        {
            return "On The Way";
        }
        else
            return "Shipped";
    }


}
