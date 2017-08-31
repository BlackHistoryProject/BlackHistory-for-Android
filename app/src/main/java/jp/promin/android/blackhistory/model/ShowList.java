package jp.promin.android.blackhistory.model;

import android.support.annotation.NonNull;

import java.util.Arrays;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import jp.promin.android.blackhistory.ui.mainstream.lists.TimelineListType;

public class ShowList extends RealmObject {
    @PrimaryKey
    private int hash;
    private int listType;
    private long userId;

    public ShowList() {
    }

    public ShowList(@NonNull TimelineListType listType, long userId) {
        this.hash = Arrays.hashCode(new Object[]{listType, userId});
        this.listType = listType.getKind();
        this.userId = userId;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public int getListType() {
        return listType;
    }

    public void setListType(int listType) {
        this.listType = listType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
