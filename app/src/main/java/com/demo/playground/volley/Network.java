package com.demo.playground.volley;

/**
 * Created by liyongan on 18/3/30.
 */

/**
 * An interface for performing requests.
 */
public interface Network {

    NetworkResponse performRequest(Request<?> request) throws VolleyError;
}