package com.aslansari.hypocoin.app;

import android.app.Application;

import androidx.annotation.NonNull;

import com.aslansari.hypocoin.BuildConfig;

import timber.log.Timber;

public class HypoCoinApp extends Application {

    private static HypoCoinApp thisInstance = null;
    public AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        thisInstance = this;

        appContainer = new AppContainer(this);
        initializeTimber();

    }

    public static HypoCoinApp getInstance() {
        return thisInstance;
    }

    private void initializeTimber(){
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                // Add the line number to the tag
                @Override
                protected String createStackElementTag(@NonNull StackTraceElement element) {
                    return String.format("[Line - %s] [Method - %s] [Class - %s]",
                            element.getLineNumber(),
                            element.getMethodName(),
                            super.createStackElementTag(element));
                }
            });
        }
    }
}
