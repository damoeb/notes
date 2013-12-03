package org.notes.core.metric;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.services.NotesResponse;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@NotesInterceptors
public class MetricInterceptor {

    private static final Logger _log = Logger.getLogger(MetricInterceptor.class);

    @AroundInvoke
    public Object injectCaching(InvocationContext invocationContext) throws Exception {
        if (invocationContext.getMethod().isAnnotationPresent(ServiceMetric.class)) {

            long from = System.nanoTime();

            Object returnObject = invocationContext.proceed();

            if (returnObject instanceof NotesResponse) {
                double durationMsec = (System.nanoTime() - from) / 1000000d;
                ((NotesResponse) returnObject).setElapsedMillis(durationMsec);
            }

            return returnObject;

        } else {
            return invocationContext.proceed();
        }

    }
}
