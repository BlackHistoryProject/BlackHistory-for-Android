package com.nanami.android.blackhistory.serialize;

import java.io.Serializable;

import twitter4j.Status;

/**
 * Created by nanami on 2014/09/14.
 */
public class TweetSerialize implements Serializable {
    Status status;
    public TweetSerialize(Status status) { this.status = status; }
    public  Status getStatus() { return  this.status; }
}
