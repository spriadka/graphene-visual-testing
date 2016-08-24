/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author spriadka
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonHibernate4ModuleConfig implements ContextResolver<ObjectMapper> {

    private ObjectMapper objectMapper = new ObjectMapper(){
    
    private static final long serialVersionUID = 1456975632L;
    {
        Hibernate4Module hibernate4Module = new Hibernate4Module();
        hibernate4Module.disable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
        registerModule(hibernate4Module);
        //setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        enable(SerializationFeature.INDENT_OUTPUT);
    }   
        
    };

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }

}
