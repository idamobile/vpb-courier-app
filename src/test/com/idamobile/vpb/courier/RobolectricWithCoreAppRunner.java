package com.idamobile.vpb.courier;

import android.app.Application;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

public class RobolectricWithCoreAppRunner extends RobolectricTestRunner {
    /**
     * Creates a runner to run {@code testClass}. Looks in your working directory for your AndroidManifest.xml file
     * and res directory.
     *
     * @param testClass the test class to be run
     * @throws org.junit.runners.model.InitializationError
     *          if junit says so
     */
    public RobolectricWithCoreAppRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected Application createApplication() {
        Application application = super.createApplication();
        application.onCreate();
        return application;
    }
}
