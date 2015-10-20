package com.msarangal.travelkhaanademo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Mandeep on 16/6/2015.
 */
public class VolleySingleton {

    private static VolleySingleton vInstance = null;
    private RequestQueue requestQueue;

    private VolleySingleton() {
        requestQueue = Volley.newRequestQueue(MyApplication.getAppContext());
    }

    public static VolleySingleton getInstance() {
        if (vInstance == null) {
            vInstance = new VolleySingleton();
        }
        return vInstance;
    }

    public RequestQueue getRequestQueue()
    {
        return  requestQueue;
    }
}
