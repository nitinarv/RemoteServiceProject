package com.client.module;

import android.content.Context;
import android.os.RemoteException;

import java.sql.Connection;

/**
 * Created by apple on 03/10/14.
 */
public abstract class ServiceManager {

    private static ServiceManagerImplementation serviceManagerImplementation = null;

    private static void connect(Context context, ConnectionCallback connectionCallback){

    }

    private static void disconnect(Context context){

    }

    private static void isServiceAvailable(Context context){

    }

    private static void isServiceConnected(Context context){

    }

    /**
     * Abstract methods
     * */
    public abstract void connect(ConnectionCallback connectionCallback);

    public abstract void disconnect();

    public abstract void isServiceAvailable();

    public abstract boolean isServiceConnected();

    public abstract void setOnUnbindListener(UnbindListenerInterface unbindListenerInterface);


    /**
     * From the service, we are not putting the one-way interface code here
     * */
    public abstract void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                                          double aDouble, String aString) throws RemoteException;
}
