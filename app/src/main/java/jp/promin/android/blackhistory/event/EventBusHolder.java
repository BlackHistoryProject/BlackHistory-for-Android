package jp.promin.android.blackhistory.event;

import com.squareup.otto.Bus;

public final class EventBusHolder {
    public static final Bus EVENT_BUS = new Bus() {
        @Override
        public void post(Object event) {
            super.post(event);
        }
    };
}