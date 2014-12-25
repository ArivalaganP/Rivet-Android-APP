package com.rivet.app.abstracts;

import java.util.Date;

/**
 * Created by brian on 12/6/14.
 */
public abstract class BuilderBase  {

    protected Date lastRefreshTime;
    private int retryCount;

    private RDBAdapterBase datasource;

    abstract  public void start();

    public RDBAdapterBase getDatasource() {
        return datasource;
    }
    public Date getLastRefreshedDate(){
    	return lastRefreshTime;
    }
    public void setLastRefreshedDate(Date lastrefdate){
    	this.lastRefreshTime = lastrefdate;
    }
    public void setDatasource(RDBAdapterBase datasource) {
        this.datasource = datasource;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
