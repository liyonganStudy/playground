package com.demo.playground.dispatcher.action;

import com.demo.playground.dispatcher.task.Request;
import com.demo.playground.dispatcher.task.RequestHandler;

/**
 * Created by liyongan on 19/1/3.
 */

public abstract class Action {
    public Request request;
    boolean cancelled;

    Action(Request request) {
        this.request = request;
    }

    abstract void complete(RequestHandler.Result result);

    abstract void error(Exception e);

    public abstract Object getTarget();

    public void cancel() {
        cancelled = true;
    }

    public Object getTag() {
        return request.tag != null ? request.tag : this;
    }
}
