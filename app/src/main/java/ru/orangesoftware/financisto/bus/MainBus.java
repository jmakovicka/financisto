package ru.orangesoftware.financisto.bus;

import androidx.lifecycle.MutableLiveData;

public class MainBus {
    public static final MutableLiveData<Object> eventBus = new MutableLiveData<>();

    public static void post(Object event) {
        eventBus.postValue(event);
    }
}
