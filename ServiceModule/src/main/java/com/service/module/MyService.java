package com.service.module;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

/**
 * Implementation for the remote service
 * */
public class MyService extends Service {
    /**Building the AIDL file in this project*/
    IMyAidlInterfaceImplementation aidlInterfaceImplementationBinder = new IMyAidlInterfaceImplementation();
    IUnbindListenerInterface unbindListenerInterface;

    /**Register callbacks*/
    final RemoteCallbackList<IUnbindListenerInterface> callbacks = new RemoteCallbackList<IUnbindListenerInterface>();

    public MyService() {
    }
    


    @Override
    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
        return aidlInterfaceImplementationBinder;
    }

    private class IMyAidlInterfaceImplementation extends IMyAidlInterface.Stub{

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void setUnbindListener(IUnbindListenerInterface listener) throws RemoteException {
            unbindListenerInterface = listener;
            /**
             * Register the callback interface
             * */
             if(callbacks!=null){
                 callbacks.register(listener);
             }
         }

    }

    /**
     * service events we won't override
     * */

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        /**
         * Making the Unbind Listner Callback
         * */
        IUnbindListenerCallback();
        /**
         * Unregister the callbacks mechanism.
         * */
        if(callbacks!=null && unbindListenerInterface !=null){
            callbacks.unregister(unbindListenerInterface);
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Registered callback on event callbacks,
     * Very important pattern, If we need to implement it to work
     * */
     protected void IUnbindListenerCallback(){
         final int N = callbacks.beginBroadcast();
         for(int i=0; i>N ; i++){
             try{
                 callbacks.getBroadcastItem(i).onUnbind();
             } catch (RemoteException e) {
                 //RemoteCallbackList would take care of removing dead objects
                 e.printStackTrace();
             }
         }
         callbacks.finishBroadcast();
     }


}
