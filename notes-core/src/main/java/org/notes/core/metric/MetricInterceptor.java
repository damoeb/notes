package org.notes.core.metric;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.core.services.NotesResponse;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * @author Daniel Scheidle, daniel.scheidle@ucs.at
 *         10:08, 09.03.12
 */
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
                double duration = (System.nanoTime() - from) / 1000000d;
                ((NotesResponse) returnObject).setElapsed(duration);
            }

            return returnObject;

        } else {
            return invocationContext.proceed();
        }

    }
}
