package com.client.module;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;

import com.service.module.IMyAidlInterface;
import com.service.module.IUnbindListenerInterface;

import java.util.List;

/**
 * Created by Nitinraj Arvind on 03/10/14.
 */
public class ServiceManagerImplementation extends ServiceManager{

    Context context;
    ConnectionCallback connectionCallback;
    IBinder iBinder = null; //TODO this variable will tell us the current status of the connection;
    IMyAidlInterface aidlInterface = null; //TODO this will be the remote service object
    IUnbindListenerInterface remoteUnbindListenerInterface = null; //TODO This event listener would be called back from remote service
    BindServiceConnection bindServiceConnection = null; //TODO use this to make connection to remote-service
    UnbindListenerInterface localUnbindListenerInterface = null; //TODO our own listener implementation, user could register to this or not

    /**
     * Basic hard-coding setup
     * This is being used in multiple places
     * */
    String applicationPackage = "com.service.module";
    String serviceClassName = "com.service.module.MyService";


    public ServiceManagerImplementation(Context context){
        /**
         * Application context is required since the connection can be established from
         * any part of any program.
         * */
        ServiceManagerImplementation.this.context = context.getApplicationContext();
    }

    @Override
    public void connect(ConnectionCallback connectionCallback) {
        doConnection(connectionCallback);
    }

    @Override
    public void disconnect() {
        doDisconnected();
    }

    @Override
    public boolean isServiceAvailable() {
        return isServiceRunning();
    }

    @Override
    public boolean isServiceConnected() {
        if(iBinder!=null && iBinder.isBinderAlive()){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public void setOnUnbindListener(UnbindListenerInterface unbindListenerInterface) {
        localUnbindListenerInterface  = unbindListenerInterface;
    }

    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            if(isBinderAlive() && aidlInterface!=null){
                aidlInterface.basicTypes(anInt, aLong, aBoolean, aFloat, aDouble, aString);
            }
    }


    /**
     * Actual Implementation
     * */
     private void doConnection(ConnectionCallback connectionCallback){
         ServiceManagerImplementation.this.connectionCallback = connectionCallback;
         if(isApplicationInstalled()){
             if(connectionCallback!=null){
                 if(bindServiceConnection == null){
                     bindServiceConnection = new BindServiceConnection();
                     Intent intent = getServiceStartingIntent();
                     boolean binderIsAlive = isBinderAlive();
                     boolean bindAttemptStatus = false;

                     if(!binderIsAlive)
                         bindAttemptStatus = ServiceManagerImplementation.this.context.bindService(intent, bindServiceConnection, Context.BIND_AUTO_CREATE);

                     if(!bindAttemptStatus){
                         //TODO to write the code to find out why the bind failed
                         connectionCallback.onConnectionFailed();
                         /**
                          * 1. Service does not exist,
                          * 2. Service is Running and bind failed due to other reasons
                          * */
                     }else{
                         connectionCallback.onConnected(ServiceManagerImplementation.this);
                     }
                 }
             }else{
                 //TODO write an exception for connection callback being null;
             }
         }else{
             if(connectionCallback!=null){
                 connectionCallback.onConnectionFailed();
             }else{
                 //TODO write an exception for connection callback being null;
             }
         }




     }

    private void doDisconnected(){
        if(bindServiceConnection != null){
            boolean binderIsAlive = isBinderAlive();
            if(binderIsAlive){
                //TODO have to confirm if this works
                ServiceManagerImplementation.this.context.unbindService(bindServiceConnection);
            }
        }
    }

    /**
     * Re-setting other variables related to the service connection
     * */
    private void onDisconnectCleanup(){
        iBinder = null;
        aidlInterface = null;
        remoteUnbindListenerInterface = null;
        bindServiceConnection = null;
        localUnbindListenerInterface = null;
        connectionCallback = null;
    }

    /**
     * Service Connection class
     * */
    private class BindServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceManagerImplementation.this.iBinder = iBinder;
            aidlInterface = IMyAidlInterface.Stub.asInterface(iBinder);
            remoteUnbindListenerInterface = new IUnbindListenerInterface.Stub(){

                @Override
                public void onUnbind(){
                    //TODO what happens on unbind?
                    if(connectionCallback!=null)
                        connectionCallback.onDisconnected();

                    //TODO the last operation is that of the final unbind
                    if(localUnbindListenerInterface!=null)
                        localUnbindListenerInterface.onUnbind();

                    //TODO make the do onDisconnectCleanup()
                    onDisconnectCleanup();
                }
            };

            /**
             * TODO Have to write the method to check the manager and service version, check and
             * request the user to upgrade the manager and service to match the same version
             * and then call doDisconnect()
             * */
            //TODO checkCurrentVersion();
            //TODO onUnbindCallbacks();
            try {
                aidlInterface.setUnbindListener(remoteUnbindListenerInterface);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            /**
             * onServiceDisconnected wouldn't get called as soon as the service is unbound,
             * This event would get called at a much later time.
             * */
        }
    }

    /**
     * Methods to check if the application containing the application
     * */
    private boolean isApplicationInstalled(){
        return isApplicationInstalled(applicationPackage);
    }


     private boolean isApplicationInstalled(String applicationPackageName){
        PackageManager packageManager = ServiceManagerImplementation.this.context.getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo applicationInfo : packages){
            if(applicationInfo.packageName.equalsIgnoreCase(applicationPackageName))
                return true;
        }
        return false;
    }

    /**
     * Methods to check if the service in question exists
     * */
     private boolean isServiceRunning(){
         return isServiceRunning(serviceClassName);
     }


     private boolean isServiceRunning(String serviceClassName){
        ActivityManager activityManager = (ActivityManager) ServiceManagerImplementation.this.context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> packages = activityManager.getRunningServices(Integer.MAX_VALUE);
        for(ActivityManager.RunningServiceInfo runningServiceInfo: packages){
            if(runningServiceInfo.service.getClassName().equalsIgnoreCase(serviceClassName))
                return true;
        }
        return false;
    }

    private boolean isBinderAlive(){
        boolean binderIsAlive = iBinder!=null && iBinder.isBinderAlive();
        return binderIsAlive;
    }

    /**
     * Generate the intent that starts and ends the service
     * */
      private Intent getServiceStartingIntent(){
          return getServiceStartingIntent(applicationPackage, serviceClassName);
      }

      private Intent getServiceStartingIntent(String applicationPackage, String serviceClassName){
         Intent intent = new Intent();
         intent.setClassName(applicationPackage,serviceClassName);
         return intent;
     }
 }
