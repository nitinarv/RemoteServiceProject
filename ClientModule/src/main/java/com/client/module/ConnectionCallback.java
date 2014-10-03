package com.client.module;

/**
 * Created by apple on 03/10/14.
 */
public interface ConnectionCallback {

    void onConnected(ServiceManager serviceManager);

    void onConnectionFailed();

    void onDisconnected();

}
