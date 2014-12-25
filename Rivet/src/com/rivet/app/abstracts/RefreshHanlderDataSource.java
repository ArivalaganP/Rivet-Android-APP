package com.rivet.app.abstracts;

import java.util.Date;

/**
 * Created by brian on 12/6/14.
 */
public abstract  class RefreshHanlderDataSource {

    Date lastRefreshedTime;
    int refreshInterval;
   abstract public void start();

}
