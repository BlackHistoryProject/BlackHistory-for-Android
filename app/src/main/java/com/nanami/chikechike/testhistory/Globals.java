package com.nanami.chikechike.testhistory;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by Telneko on 2015/01/17.
 */
public class Globals extends Application {
    public ArrayList<Account> accountList = new ArrayList<Account>();
    public void initializeList(){
        accountList = new ArrayList<Account>();
    }
    public void addAccount(Account account){
        this.accountList.add(account);
    }
    public static class Account{
        long userID;
        String screenName;
        public Account(long userID, String  screenName){
            this.userID = userID;
            this.screenName = screenName;
        }
        public long getUserID(){
            return this.userID;
        }
        public String getScreenName(){
            return this.screenName;
        }
    }

    public ArrayList<String> getAccountNames(){
        ArrayList<String> ret = new ArrayList<String>();
        for (Account item : this.accountList){
            ret.add(item.getScreenName());
        }
        return ret;
    }
}
