package com.novas.nettop;

/**
 * Created by novas on 15/12/2.
 */
public interface NetTopListener {
    public void onSuccess(HttpResponse response);
    public void onFail();
    public void onError();
}
