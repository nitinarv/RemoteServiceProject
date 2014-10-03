package com.client.module;

import android.content.Context;
import android.os.RemoteException;

import java.sql.Connection;

/**
 * Created by Nitinraj Arvind on 03/10/14.
 */
public abstract class ServiceManager {

    private static ServiceManagerImplementation serviceManagerImplementation = null;

    private static void connect(Context context, ConnectionCallback connectionCallback){
        if(serviceManagerImplementation == null){
            serviceManagerImplementation = new ServiceManagerImplementation(context);
        }
        serviceManagerImplementation.connect(connectionCallback);
    }

    private static void disconnect(Context context){
       if(serviceManagerImplementation != null){
           serviceManagerImplementation.disconnect();
       }
    }

    private static boolean isServiceAvailable(Context context){
        if(serviceManagerImplementation != null){
            return serviceManagerImplementation.isServiceAvailable();
        }else{
            return false;
        }
    }

    private static boolean isServiceConnected(Context context){
        if(serviceManagerImplementation != null){
            return serviceManagerImplementation.isServiceConnected();
        }else{
            return false;
        }
    }

    /**
     * Abstract methods
     * */
    public abstract void connect(ConnectionCallback connectionCallback);

    public abstract void disconnect();

    public abstract boolean isServiceAvailable();

    public abstract boolean isServiceConnected();

    public abstract void setOnUnbindListener(UnbindListenerInterface unbindListenerInterface);


    /**
     * From the service, we are not putting the one-way interface code here
     * */
    public abstract void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                                          double aDouble, String aString) throws RemoteException;
}
