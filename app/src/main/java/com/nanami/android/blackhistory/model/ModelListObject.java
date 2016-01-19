package com.nanami.android.blackhistory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by atsumi on 2016/01/10.
 */
public class ModelListObject extends RealmObject {
    private Long userId;
    private Integer listType;

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setListType(Integer listType) {
        this.listType = listType;
    }

    public Integer getListType() {
        return listType;
    }
}
