package com.griddynamics.jagger.util;

import com.google.common.annotations.Beta;

public class ExceptionUtils {

    /**
     * Rethrows the cause exception of a given throwable, discarding the original
     * throwable. Optionally, the stack frames of the cause and the outer
     * exception are combined and the stack trace of the cause is set to this
     * combined trace. If there is no cause the original exception is rethrown
     * unchanged in all cases.
     *
     * @param exception the exception from which to extract the cause
     * @param combineStackTraces if true the stack trace of the cause will be
     *     replaced by the concatenation of the trace from the exception and the
     *     trace from the cause.
     */
    @Beta
    public static Exception throwCause(Exception exception,
                                       boolean combineStackTraces) throws Exception {
        Throwable cause = exception.getCause();
        if (cause == null) {
            throw exception;
        }
        if (combineStackTraces) {
            StackTraceElement[] causeTrace = cause.getStackTrace();
            StackTraceElement[] outerTrace = exception.getStackTrace();
            StackTraceElement[] combined =
                    new StackTraceElement[causeTrace.length + outerTrace.length];
            System.arraycopy(causeTrace, 0, combined, 0, causeTrace.length);
            System.arraycopy(outerTrace, 0, combined,
                    causeTrace.length, outerTrace.length);
            cause.setStackTrace(combined);
        }
        if (cause instanceof Exception) {
            throw (Exception) cause;
        }
        if (cause instanceof Error) {
            throw (Error) cause;
        }
        // The cause is a weird kind of Throwable, so throw the outer exception
        throw exception;
    }

}
