package com.demo.playground.dispatcher.task;

import android.support.annotation.Nullable;

import com.demo.playground.dispatcher.action.Action;
import com.demo.playground.dispatcher.thread.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by liyongan on 19/1/3.
 */

public class TimeCostTask implements Runnable {

    @Nullable Action action;
    @Nullable List<Action> actions;
    public Future<?> future;

    public TimeCostTask(Action action) {
        this.action = action;
    }

    @Override
    public void run() {

    }

    public @Nullable Action getAction() {
        return action;
    }

    public @Nullable List<Action> getActions() {
        return actions;
    }

    public void attach(Action action) {
        Request request = action.request;

        if (this.action == null) {
            this.action = action;
            return;
        }

        if (actions == null) {
            actions = new ArrayList<>(3);
        }
        actions.add(action);
    }

    public void detach(Action action) {
        if (this.action == action) {
            this.action = null;
        } else if (actions != null) {
            actions.remove(action);
        }
    }

    public boolean cancel() {
        return action == null
                && (actions == null || actions.isEmpty())
                && future != null
                && future.cancel(false);
    }

    boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    public static TimeCostTask forRequest(Dispatcher dispatcher, Action action) {
        Request request = action.request;
        return new TimeCostTask(action);
//        List<RequestHandler> requestHandlers = picasso.getRequestHandlers();
//
//        for (int i = 0, count = requestHandlers.size(); i < count; i++) {
//            RequestHandler requestHandler = requestHandlers.get(i);
//            if (requestHandler.canHandleRequest(request)) {
//                return new BitmapHunter(picasso, dispatcher, cache, stats, action, requestHandler);
//            }
//        }
//        return new BitmapHunter(picasso, dispatcher, cache, stats, action, ERRORING_HANDLER);
    }
}
