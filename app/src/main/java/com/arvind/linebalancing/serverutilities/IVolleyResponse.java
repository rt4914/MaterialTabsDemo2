package com.arvind.linebalancing.serverutilities;

import com.android.volley.VolleyError;

/**
 * Created by rajat4914 on 08/01/18.
 * Reference https://stackoverflow.com/questions/35628142/how-to-make-separate-class-for-volley-library-and-call-all-method-of-volley-from
 */

public interface IVolleyResponse {

    void notifySuccess(String requestType, String response);
    void notifyError(String requestType, VolleyError error);

}
