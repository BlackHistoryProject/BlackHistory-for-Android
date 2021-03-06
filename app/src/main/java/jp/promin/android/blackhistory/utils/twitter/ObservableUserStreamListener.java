package jp.promin.android.blackhistory.utils.twitter;

import android.content.Context;
import android.support.annotation.NonNull;

import jp.promin.android.blackhistory.BlackHistoryController;
import jp.promin.android.blackhistory.event.TwitterFavoriteEvent;
import jp.promin.android.blackhistory.event.TwitterFriendListEvent;
import jp.promin.android.blackhistory.event.TwitterStreamEvent;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

final public class ObservableUserStreamListener implements UserStreamListener {

    private final BlackHistoryController mApp;
    private final long mUserId;

    public ObservableUserStreamListener(@NonNull Context context, long userId) {
        mApp = BlackHistoryController.get(context);
        mUserId = userId;
    }

    public long getUserId() {
        return mUserId;
    }

    @Override
    public void onDeletionNotice(long directMessageId, long userId) {

    }

    @Override
    public void onFriendList(final long[] friendIds) {
        mApp.postEvent(new TwitterFriendListEvent(mUserId, friendIds));
    }

    @Override
    public void onFavorite(final User source, final User target, final Status favoritedStatus) {
        mApp.postEvent(new TwitterFavoriteEvent(mUserId, source, target, favoritedStatus));
    }

    @Override
    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

    }

    @Override
    public void onFollow(User source, User followedUser) {

    }

    @Override
    public void onUnfollow(User source, User unfollowedUser) {

    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {

    }

    @Override
    public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

    }

    @Override
    public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

    }

    @Override
    public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

    }

    @Override
    public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

    }

    @Override
    public void onUserListCreation(User listOwner, UserList list) {

    }

    @Override
    public void onUserListUpdate(User listOwner, UserList list) {

    }

    @Override
    public void onUserListDeletion(User listOwner, UserList list) {

    }

    @Override
    public void onUserProfileUpdate(User updatedUser) {

    }

    @Override
    public void onUserSuspension(long suspendedUser) {

    }

    @Override
    public void onUserDeletion(long deletedUser) {

    }

    @Override
    public void onBlock(User source, User blockedUser) {

    }

    @Override
    public void onUnblock(User source, User unblockedUser) {

    }

    @Override
    public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {

    }

    @Override
    public void onFavoritedRetweet(User source, User target, Status favoritedRetweeet) {

    }

    @Override
    public void onQuotedTweet(User source, User target, Status quotingTweet) {

    }

    @Override
    public void onStatus(final Status status) {
        mApp.postEvent(new TwitterStreamEvent(mUserId, status));
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {

    }

    @Override
    public void onStallWarning(StallWarning warning) {

    }

    @Override
    public void onException(Exception ex) {
        ex.printStackTrace();
    }
}
