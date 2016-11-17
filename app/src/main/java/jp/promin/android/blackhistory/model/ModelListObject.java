package jp.promin.android.blackhistory.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by atsumi on 2016/01/10.
 */
public class ModelListObject extends RealmObject {

    /**
     * ListData Format
     *    $UserId-$ListName
     */
    @PrimaryKey
    private String listData;

    /**
     * リストデータを取得します
     * @return $UserId-$ListName
     */
    public String getListData() {
        return listData;
    }

    /**
     * リストデータをセットします
     * @param listData $UserId-$ListName
     */
    public void setListData(String listData) {
        this.listData = listData;
    }
}
