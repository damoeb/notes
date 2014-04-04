package org.notes.core.endpoints.request;

import org.apache.log4j.Logger;
import org.notes.common.configuration.NotesInterceptors;
import org.notes.common.exceptions.NotesException;
import org.notes.core.endpoints.NotesResponse;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.lang.reflect.Method;

@Interceptor
@NotesInterceptors
public class ErrorHandlingInterceptor {

    private static final Logger _log = Logger.getLogger(ErrorHandlingInterceptor.class);

    @AroundInvoke
    public Object handleErrors(InvocationContext invocationContext) throws Exception {

        Method method = invocationContext.getMethod();

        if (method.isAnnotationPresent(GET.class) ||
                method.isAnnotationPresent(POST.class) ||
                method.isAnnotationPresent(PUT.class) ||
                method.isAnnotationPresent(DELETE.class)
                ) {
            try {
                // todo
                return invocationContext.proceed();
            } catch (NotesRequestException t) {
                return NotesResponse.ok(t);
            } catch (NotesException t) {
                return NotesResponse.ok(new NotesRequestException(javax.ws.rs.core.Response.Status.CONFLICT, t));
            } catch (IllegalArgumentException t) {
                return NotesResponse.ok(new NotesRequestException(javax.ws.rs.core.Response.Status.PRECONDITION_FAILED, t));
            } catch (Throwable t) {
                if (t.getCause() instanceof NotesRequestException) {
                    return NotesResponse.ok(new NotesRequestException(javax.ws.rs.core.Response.Status.CONFLICT, t.getCause()));
                } else {
                    _log.error(t);
                    return NotesResponse.ok(new NotesRequestException("Error in method " + invocationContext.getMethod().getName(), t));
                }
            }
        } else {
            return invocationContext.proceed();
        }
    }

}
