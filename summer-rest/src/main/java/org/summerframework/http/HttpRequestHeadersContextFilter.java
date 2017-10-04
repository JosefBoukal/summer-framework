package org.summerframework.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.summerframework.core.http.HttpRequestHeadersContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This filter is responsible for binding HTTP Request Headers onto the {@link HttpRequestHeadersContext}.
 */
@Slf4j
public class HttpRequestHeadersContextFilter extends OncePerRequestFilter {

    private final Set<String> allowedHeaders;

    public HttpRequestHeadersContextFilter(String... allowedHeaders) {
        this(new HashSet<>(Arrays.asList(allowedHeaders)));
    }

    public HttpRequestHeadersContextFilter(Set<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders.stream()
                .map(item -> item.toLowerCase(Locale.ENGLISH))
                .collect(Collectors.toSet());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (allowedHeaders.contains(headerName.toLowerCase(Locale.ENGLISH))) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        HttpRequestHeadersContext.setContext(headers);
        try {
            filterChain.doFilter(request, response);
        } finally {
            HttpRequestHeadersContext.clearContext();
        }
    }

}
