package com.demo.playground.dispatcher.thread;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.demo.playground.dispatcher.action.Action;
import com.demo.playground.dispatcher.task.TimeCostTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by liyongan on 19/1/3.
 */

public class Dispatcher {
    static final int REQUEST_SUBMIT = 1;
    static final int TAG_PAUSE = 11;

    private final DispatcherThread dispatcherThread;
    private final ExecutorService service;
    private final Context context;
    private final Handler handler;
    private final Handler mainThreadHandler;
    Set<Object> pausedTags;
    Map<Object, Action> pausedActions;
    Map<String, TimeCostTask> taskMap;
    Map<Object, Action> failedActions;

    public Dispatcher(Context context, ExecutorService service, Handler mainThreadHandler) {
        this.dispatcherThread = new DispatcherThread();
        this.dispatcherThread.start();
        this.handler = new DispatcherHandler(dispatcherThread.getLooper(), this);

        this.context = context;
        this.service = service;
        this.mainThreadHandler = mainThreadHandler;
    }

    public void shutdown() {
        service.shutdown();
        dispatcherThread.quit();
    }

    public void dispatchSubmit(Action action) {
        handler.sendMessage(handler.obtainMessage(REQUEST_SUBMIT, action));
    }

    private void performSubmit(Action action) {
        if (pausedTags.contains(action.getTag())) {
            pausedActions.put(action.getTarget(), action);
            return;
        }

        TimeCostTask hunter = taskMap.get(action.request.key);
        if (hunter != null) {
            hunter.attach(action);
            return;
        }
        if (service.isShutdown()) {
            return;
        }
        hunter = TimeCostTask.forRequest(this, action);
        hunter.future = service.submit(hunter);
        taskMap.put(action.request.key, hunter);
    }

    public void dispatchPauseTag(Object tag) {
        handler.sendMessage(handler.obtainMessage(TAG_PAUSE, tag));
    }

    void performPauseTag(Object tag) {
        if (!pausedTags.add(tag)) {
            return;
        }
        for (Iterator<TimeCostTask> it = taskMap.values().iterator(); it.hasNext(); ) {
            TimeCostTask costTask = it.next();
            Action single = costTask.getAction();
            List<Action> joined = costTask.getActions();
            boolean hasMultiple = joined != null && !joined.isEmpty();
            if (single == null && !hasMultiple) {
                continue;
            }
            if (single != null && single.getTag().equals(tag)) {
                costTask.detach(single);
                pausedActions.put(single.getTarget(), single);
            }
            if (joined != null) {
                for (int i = joined.size() - 1; i >= 0; i--) {
                    Action action = joined.get(i);
                    if (!action.getTag().equals(tag)) {
                        continue;
                    }
                    costTask.detach(action);
                    pausedActions.put(action.getTarget(), action);
                }
            }
            // Check if the costTask can be cancelled in case all its requests
            // had the tag being paused here.
            if (costTask.cancel()) {
                it.remove();
            }
        }
    }

    void performResumeTag(Object tag) {
        // Trying to resume a tag that is not paused.
        if (!pausedTags.remove(tag)) {
            return;
        }

        List<Action> batch = null;
        for (Iterator<Action> i = pausedActions.values().iterator(); i.hasNext();) {
            Action action = i.next();
            if (action.getTag().equals(tag)) {
                if (batch == null) {
                    batch = new ArrayList<>();
                }
                batch.add(action);
                i.remove();
            }
        }
        if (batch != null) {
            for (int i = 0, n = batch.size(); i < n; i++) {
                Action action = batch.get(i);
                performSubmit(action);
            }
        }
    }

    private static class DispatcherThread extends HandlerThread {
        DispatcherThread() {
            super("lya-dispatcher", THREAD_PRIORITY_BACKGROUND);
        }
    }

    private static class DispatcherHandler extends Handler {
        private final Dispatcher dispatcher;

        DispatcherHandler(Looper looper, Dispatcher dispatcher) {
            super(looper);
            this.dispatcher = dispatcher;
        }

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case REQUEST_SUBMIT: {
                    Action action = (Action) msg.obj;
                    dispatcher.performSubmit(action);
                    break;
                }
            }
        }
    }
}
