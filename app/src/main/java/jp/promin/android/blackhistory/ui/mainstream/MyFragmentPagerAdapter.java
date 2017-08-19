package jp.promin.android.blackhistory.ui.mainstream;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.Pair;

import java.util.ArrayList;

import jp.promin.android.blackhistory.ui.common.CommonStreamFragment;
import jp.promin.android.blackhistory.ui.mainstream.lists.FavoriteStreamFragment;
import jp.promin.android.blackhistory.ui.mainstream.lists.HomeStreamFragment;
import jp.promin.android.blackhistory.ui.mainstream.lists.MentionsStreamFragment;
import jp.promin.android.blackhistory.ui.mainstream.lists.TimelineListType;
import jp.promin.android.blackhistory.utils.BHLogger;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Pair<Long, CommonStreamFragment>> tab = new ArrayList<>();

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Pair<Long, CommonStreamFragment>> list) {
        super(fm);
        this.tab = list;
    }

    public ArrayList<Pair<Long, CommonStreamFragment>> getTab() {
        return tab;
    }

    /**
     * 新しくタブを追加する
     *
     * @param timelineListType 表示するリストのタイプ
     * @param userId           表示するリストの保持者 イベント受け取る際にも使う
     */
    public void addTab(@NonNull TimelineListType timelineListType, @NonNull Long userId) {
        /**
         * 既に同じ種類のリストが追加されてないかを確認する
         * 追加されていたら何もしない
         */
        if (userId == -1 || search(userId, timelineListType) != null) {
            return;
        }

        CommonStreamFragment additionalFragment = null;
        switch (timelineListType) {
            /**
             * 基本的なユーザーストリーム
             * * 自分のツイート/Fav/RT
             * * 他人のツイート/Fav
             * - 過去のツイートを表示
             */
            case Home:
                additionalFragment = HomeStreamFragment.newInstance(userId);
                break;

            /**
             * 自分へのイベントを表示
             * * 飛んできたリプ
             * * されたFav
             * * されたRT
             * * したRTをさらにRTされた時
             * * されたフォロー
             * - とりあえず最初にロードした分表示
             */
            case Notification:
                break;

            /**
             * 自分へのリプを表示
             * * 飛んできたリプ
             * - とりあえず最初にロードした分表示
             */
            case Mentions:
                additionalFragment = MentionsStreamFragment.newInstance(userId);
                break;

            /**
             * 自分がしたFavを表示
             * * 自分がしたFav
             * - とりあえず最初にロードした分表示
             */
            case Favorites:
                additionalFragment = FavoriteStreamFragment.newInstance(userId);
                break;

            /**
             * 保留
             * ※ ダイアログを出して、リストのストリームを受信する
             */
            case Lists:
                break;

            /**
             * 保留
             * ※ ダイアログを出して、ツイート/User検索をする
             */
            case Search:
                break;

            /**
             * 保留
             * 最近フォローしてきた人を表示
             * * されたフォロー
             */
            case Followers:
                break;

            /**
             * DMを表示
             * * DMを送ってきた人リスト
             */
            case Messages:
                break;

            /**
             * 指定ユーザーのみのツイートリストを表示
             */
            case User:
                break;
        }
        if (additionalFragment != null) {
            tab.add(new Pair<>(userId, additionalFragment));
            this.notifyDataSetChanged();
        }
    }

    /**
     * 指定した要素を含むタブが存在するかを確認
     *
     * @param userId
     * @param listType
     * @return
     */
    public CommonStreamFragment search(Long userId, TimelineListType listType) {
        for (Pair<Long, CommonStreamFragment> item : this.tab) {
            if (item.first.equals(userId) && item.second.getListType().equals(listType)) {
                return item.second;
            }
        }
        return null;
    }

    /**
     * 指定した要素を含むタブを削除する
     *
     * @param item
     */
    public void deleteTab(Pair<Long, CommonStreamFragment> item) {
        for (Pair<Long, CommonStreamFragment> _item : this.tab) {
            if (_item.equals(item)) {
                this.tab.remove(item);
                this.notifyDataSetChanged();
                BHLogger.toast("削除しました");
                return;
            }
        }
    }

    /**
     * indexからリストオブジェクトを取得する
     * タブ削除の時とかに使う
     *
     * @param position
     * @return
     */
    public Pair<Long, CommonStreamFragment> getItemAtIndex(int position) {
        return tab.get(position);
    }

    public void remove(int position) {
        this.tab.remove(position);
    }

    @Override
    public Fragment getItem(int position) {
        return tab.get(position).second;
    }

    @Override
    public int getCount() {
        return tab.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        try {
            try {
                return this.tab.get(position).second.getTitle();
            } catch (Exception e) {
                e.printStackTrace();
                return String.valueOf(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Non-Title";
        }
    }
}
