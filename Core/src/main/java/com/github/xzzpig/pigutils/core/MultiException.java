package com.github.xzzpig.pigutils.core;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MultiException extends Exception {
    @NotNull
    public List<Exception> exceptions = new ArrayList<>();
    private String message = "null";

    public MultiException() {}

    public MultiException(@Nullable Exception... es) {
        add(es);
    }

    public MultiException(@Nullable List<Exception> es) {
        add(es);
    }

    @Override public String getMessage() {
        return message;
    }

    public MultiException setMessage(@Nullable String msg) {
        if (msg != null)
            this.message = msg;
        return this;
    }

    public MultiException add(@Nullable Exception... es) {
        if (es != null)
            for (Exception exception : es)
                exceptions.add(exception);
        return this;
    }

    public MultiException add(@Nullable List<Exception> es) {
        if (es != null)
            exceptions.addAll(es);
        return this;
    }
}
