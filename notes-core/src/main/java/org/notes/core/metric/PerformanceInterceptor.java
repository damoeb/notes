package org.notes.core.metric;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.endpoints.internal.NotesResponse;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@NotesInterceptors
public class PerformanceInterceptor {

    private static final Logger LOGGER = Logger.getLogger(PerformanceInterceptor.class);

    @AroundInvoke
    public Object injectCaching(InvocationContext invocationContext) throws Exception {
        if (invocationContext.getMethod().isAnnotationPresent(PerformanceLogger.class)) {

            long from = System.nanoTime();

            Object returnObject = invocationContext.proceed();

            if (returnObject instanceof NotesResponse) {
                double durationMillis = (System.nanoTime() - from) / 1000000d;
                LOGGER.info(String.format("call %s in %s", invocationContext.getMethod().getName(), durationMillis));
                ((NotesResponse) returnObject).setElapsedMillis(durationMillis);
            }

            return returnObject;

        } else {
            return invocationContext.proceed();
        }

    }
}
