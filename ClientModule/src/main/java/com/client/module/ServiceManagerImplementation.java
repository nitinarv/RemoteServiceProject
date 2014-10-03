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

import java.util.List;

/**
 * Created by Nitinraj Arvind on 03/10/14.
 */
public class ServiceManagerImplementation extends ServiceManager{

    Context context;
    ConnectionCallback connectionCallback;
    IBinder iBinder = null; //TODO this variable will tell us the current status of the connection;
    IMyAidlInterface aidlInterface = null; //TODO this will be the remote service object
    BindServiceConnection bindServiceConnection = null; //TODO use this to make connection to remote-service

    /**Basic hard-coding setup
     * This is being used in multiple places
     * */
    String applicationPackage = "";
    String serviceClassName = "";


    public ServiceManagerImplementation(Context context){
        /**
         * Application context is required since the connection can be established from
         * any part of any program.
         * */
        ServiceManagerImplementation.this.context = context.getApplicationContext();
    }

    @Override
    public void connect(ConnectionCallback connectionCallback) {
        ServiceManagerImplementation.this.connectionCallback = connectionCallback;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void isServiceAvailable() {

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

    }

    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

    }


    /**
     * Actual Implementation
     * */
     private void doConnection(){
        if(bindServiceConnection == null){
            bindServiceConnection = new BindServiceConnection();
            Intent intent = getServiceStartingIntent();
            boolean binderIsAlive = isBinderAlive();
            boolean bindAttemptStatus = false;

            if(!binderIsAlive)
                bindAttemptStatus = ServiceManagerImplementation.this.context.bindService(intent, bindServiceConnection, Context.BIND_AUTO_CREATE);

              if(!bindAttemptStatus){
                  //TODO to write the code to find out why the bind failed

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
     * Service Connection class
     * */
    private class BindServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceManagerImplementation.this.iBinder = iBinder;
            aidlInterface = IMyAidlInterface.Stub.asInterface(iBinder);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

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
