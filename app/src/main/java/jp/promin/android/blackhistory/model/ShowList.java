package jp.promin.android.blackhistory.model;

import android.support.annotation.NonNull;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;

import java.util.Arrays;

import jp.promin.android.blackhistory.ui.mainstream.lists.TimelineListType;

@Table
public class ShowList {
    @PrimaryKey
    protected int hash;
    @Column
    protected int listType;
    @Column
    protected long userId;

    protected ShowList() {
    }

    public ShowList(@NonNull TimelineListType listType, long userId) {
        this.hash = Arrays.hashCode(new Object[]{listType, userId});
        this.listType = listType.getKind();
        this.userId = userId;
    }

    public int getHash() {
        return hash;
    }

    @NonNull
    public TimelineListType getListType() {
        return TimelineListType.kindOf(listType);
    }

    public long getUserId() {
        return userId;
    }
}
