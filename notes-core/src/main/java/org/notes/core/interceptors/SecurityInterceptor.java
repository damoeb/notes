package org.notes.core.interceptors;

import org.apache.log4j.Logger;
import org.notes.common.configuration.Configuration;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.common.exceptions.NotesStatus;
import org.notes.core.domain.NotesSession;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;


@Interceptor
@NotesInterceptors
public class SecurityInterceptor {

    private static final Logger _log = Logger.getLogger(Configuration.class);

    @Inject
    private NotesSession notesSession;

    @AroundInvoke
    public Object handleSecurity(InvocationContext invocationContext) throws Exception {

        Method method = invocationContext.getMethod();

        try {
            Bouncer bouncer = method.getAnnotation(Bouncer.class);
            if (bouncer != null) {

                if (notesSession == null || notesSession.getUser() == null) {
                    throw new IllegalArgumentException("You're not logged in");
                }
                // todo validate Bouncer settings with session

            }

        } catch (Throwable t) {
            _log.error("Configuration for class " + invocationContext.getTarget().getClass().getName() + "failed: " + t.getMessage());
            throw new NotesException(NotesStatus.CONFIGURATION_ERROR, "Configuration for class " + invocationContext.getTarget().getClass().getName() + "failed: " + t.getMessage());
        }

        return invocationContext.proceed();
    }
}
