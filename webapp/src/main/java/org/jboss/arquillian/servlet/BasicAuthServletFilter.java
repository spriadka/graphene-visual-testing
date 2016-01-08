/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.servlet;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.jboss.arquillian.bean.BasicAuthSessionStore;
import org.jboss.arquillian.util.Base64;

/**
 *
 * @author jhuska
 */
public class BasicAuthServletFilter implements Filter {

    private Logger LOGGER = Logger.getLogger(BasicAuthServletFilter.class.getName());
    
    @Inject
    private BasicAuthSessionStore sessionStore;
    
    @Override
    public void init(FilterConfig fc) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        HttpServletRequestWrapper httpRequest = new HttpServletRequestWrapper((HttpServletRequest) sr);
        final String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.decode(base64Credentials),
                    Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            sessionStore.setLogin(values[0]);
            sessionStore.setPassword(values[1]);
        }
        fc.doFilter(sr, sr1);
    }

    @Override
    public void destroy() {
    }

}
