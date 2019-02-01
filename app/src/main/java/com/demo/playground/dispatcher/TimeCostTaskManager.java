package com.demo.playground.dispatcher;

import android.os.Looper;
import android.support.annotation.NonNull;

import com.demo.playground.dispatcher.action.Action;
import com.demo.playground.dispatcher.thread.Dispatcher;

import java.util.Map;

/**
 * Created by liyongan on 19/1/3.
 */

public class TimeCostTaskManager {
    Map<Object, Action> targetToAction;
    Dispatcher dispatcher;

    void enqueueAndSubmit(Action action) {
        Object target = action.getTarget();
        if (targetToAction.get(target) != action) {
            // This will also check we are on the main thread.
            cancelExistingRequest(target);
            targetToAction.put(target, action);
        }
        submit(action);
    }

    void submit(Action action) {
        dispatcher.dispatchSubmit(action);
    }

    public void pauseTag(@NonNull Object tag) {
        dispatcher.dispatchPauseTag(tag);
    }

    void cancelExistingRequest(Object target) {
        checkMain();
        Action action = targetToAction.remove(target);
        if (action != null) {
            action.cancel();
//            dispatcher.dispatchCancel(action);
        }
    }

    static void checkMain() {
        if (!isMain()) {
            throw new IllegalStateException("Method call should happen from the main thread.");
        }
    }

    static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
