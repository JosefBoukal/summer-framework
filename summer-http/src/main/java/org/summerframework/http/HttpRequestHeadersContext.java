package org.summerframework.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedThreadLocal;

import java.util.Map;
import java.util.TreeMap;

/**
 * The HTTP Request Headers context that holds selected HTTP request headers in a thread local variable thus they are
 * available in the current HTTP request thread.
 */
@Slf4j
public class HttpRequestHeadersContext {

    private static final ThreadLocal<Map<String, String>> context = new NamedThreadLocal<>("HTTP Request Headers");

    /**
     * Returns case insensitive key thread local context to hold HTTP request headers. It throws an {@link
     * IllegalStateException} when no such context is available.
     */
    public static Map<String, String> getContext() {
        Map<String, String> result = context.get();
        if (result == null) {
            throw new IllegalStateException("No HTTP Request Headers context exists, please, set context first!");
        }
        return result;
    }

    /**
     * Sets the current thread local context to the given HTTP request headers.
     */
    public static void setContext(Map<String, String> headers) {
        Map<String, String> data = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        data.putAll(headers);
        log.debug("Setting the current HTTP Request Headers context to {}", headers);
        context.set(data);
    }

    /**
     * Clears the current HTTP request headers context.
     */
    public static void clearContext() {
        log.debug("Clearing the current HTTP Request Headers context");
        context.remove();
    }

}
