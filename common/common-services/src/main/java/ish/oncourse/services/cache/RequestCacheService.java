package ish.oncourse.services.cache;

//import org.apache.tapestry5.ioc.Invocation;
//import org.apache.tapestry5.ioc.MethodAdvice;

import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.services.Request;

import java.lang.reflect.Method;

public class RequestCacheService implements IRequestCacheService {

    @Inject
    private Request request;

    public <V> Value<V> getFromRequest(Class<V> vClass, String key) {
        return  (Value<V>) request.getAttribute(key);
    }

    public <V> void putToRequest(String key, V value) {
        request.setAttribute(key, Value.valueOf(value));
    }


    public void applyRequestCachedAdvice(final MethodAdviceReceiver receiver) {
        MethodAdvice advice = new MethodAdvice()
        {
            @Override
            public void advise(MethodInvocation invocation) {
                String key = receiver.getInterface().getName() + '.' + invocation.getMethod().getName();

                try {
                    Value result = RequestCacheService.this.getFromRequest(invocation.getMethod().getReturnType(), key);
                    if (result == null) {
                        invocation.proceed();
                        RequestCacheService.this.putToRequest(key, invocation.getReturnValue());
                    }
                    else {
                        invocation.setReturnValue(result.getValue());
                    }
                } catch (Exception e) {
                    invocation.proceed();
                    RequestCacheService.this.putToRequest(key, invocation.getReturnValue());
                }

            }
        };

        for (Method m : receiver.getInterface().getMethods())
        {
            if (m.getAnnotation(RequestCached.class) != null)
                receiver.adviseMethod(m, advice);
        }
    }

}
