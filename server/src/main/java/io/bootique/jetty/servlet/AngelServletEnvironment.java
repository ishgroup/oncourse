package io.bootique.jetty.servlet;

import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class AngelServletEnvironment extends DefaultServletEnvironment {

    // super class requiest variable works incorrect in Bootique version 2.0
    private static final ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();

    public AngelServletEnvironment() {}

    @Override
    public Optional<HttpServletRequest> request() {
        return Optional.ofNullable(request.get());
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        request.set((HttpServletRequest)sre.getServletRequest());
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        request.remove();
    }
}
