// IMyAidlInterface.aidl
package com.service.module;

// Declare any non-default types here with import statements
import com.service.module.IUnbindListenerInterface;

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    /**
    * Demonstrats some of the basic interface type callback regsitration mechanism
    *
    */
    void setUnbindListener(in IUnbindListenerInterface listener);
}
