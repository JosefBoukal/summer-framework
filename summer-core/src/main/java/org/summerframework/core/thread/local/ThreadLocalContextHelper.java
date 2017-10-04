package org.summerframework.core.thread.local;

import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;

/**
 * The helper class for all the {@link ThreadLocal} context providers that also supports the {@link
 * InheritableThreadLocal} as well.
 *
 * @author Josef Boukal
 */
public class ThreadLocalContextHelper<T> {

    private final ThreadLocal<T> context;
    private final ThreadLocal<T> inheritableContext;

    public ThreadLocalContextHelper(String contextName) {
        this.context = new NamedThreadLocal<>(contextName);
        this.inheritableContext = new NamedInheritableThreadLocal<>(contextName);
    }

    /**
     * Resets the context for the current thread.
     */
    public void resetContext() {
        context.remove();
        inheritableContext.remove();
    }

    /**
     * Associate the given context with the current thread, <i>not</i> exposing it as inheritable for child threads.
     *
     * @param context the current context, or {@code null} to reset the thread-bound context
     * @see #resetContext()
     */
    public void setContext(T context) {
        setContext(context, false);
    }

    /**
     * Associate the given context with the current thread.
     *
     * @param context     the current context, or {@code null} to reset the thread-bound context
     * @param inheritable whether to expose the context as inheritable for child threads (using an {@link
     *                    InheritableThreadLocal})
     */
    public void setContext(T context, boolean inheritable) {
        if (context == null) {
            resetContext();
        } else {
            if (inheritable) {
                this.inheritableContext.set(context);
                this.context.remove();
            } else {
                this.context.set(context);
                this.inheritableContext.remove();
            }
        }
    }

    /**
     * Returns the context associated with the current thread, or {@code null} if none has been associated yet.
     */
    public T getContext() {
        T result = context.get();
        if (result == null) {
            result = inheritableContext.get();
        }
        return result;
    }

}
