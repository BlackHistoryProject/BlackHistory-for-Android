package com.nanami.android.blackhistory.event;

import com.squareup.otto.Bus;

/**
 * Created by atsumi on 2016/01/09.
 */
public final class EventBusHolder {
    // EventBus のインスタンス自体はどの Activity からでも同じものを使うようにする
    // DI フレームワークで Producer を使うと便利
    public static final Bus EVENT_BUS = new Bus();
}