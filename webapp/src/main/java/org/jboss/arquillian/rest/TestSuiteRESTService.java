package org.jboss.arquillian.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import org.jboss.arquillian.bean.JCRBean;
import org.jboss.arquillian.managers.TestSuiteManager;
import org.jboss.arquillian.model.testSuite.TestSuite;

/**
 *
 * @author jhuska
 */
@Path("/suites")
@RequestScoped
public class TestSuiteRESTService {
    
    @Inject
    private TestSuiteManager testSuiteManager;
    
    @Inject
    private JCRBean jcrBean;
    
    @Context
    private Providers provider;
    
    private static final Logger LOGGER = Logger.getLogger(TestSuiteRESTService.class.getName());
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTestSuites(@QueryParam("fetch")List<String> fetch) throws JsonProcessingException {
        ContextResolver<ObjectMapper> resolver = provider
                .getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
        ObjectMapper mapper = resolver.getContext(TestSuite.class);
        return Response.ok(mapper.writeValueAsString(testSuiteManager.getAllTestSuites())
                , MediaType.APPLICATION_JSON).build();
    }
    
    @DELETE
    @Path("/{testSuiteID:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTestSuite(@PathParam("testSuiteID") long id) {
        LOGGER.info("I AM GOING TO DELETE TEST SUITE");
        TestSuite testSuiteToRemove = testSuiteManager.findById(id,"");
        jcrBean.removeTestSuite(testSuiteToRemove.getName());
        testSuiteManager.deleteTestSuite(testSuiteManager.findById(id,""));
        return Response.ok().build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TestSuite createTestSuite(TestSuite testSuite) {
        TestSuite result = null;
        if(testSuiteManager.getTestSuite(testSuite.getName()) == null) {
            result = testSuiteManager.createTestSuite(testSuite);
        } else {
            result = testSuite;
        }
        return result;
    }
    
    @GET
    @Path("/{testSuiteID:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTestSuite(@PathParam("testSuiteID") long id, @QueryParam("fetch") String list) throws JsonProcessingException {
        ContextResolver<ObjectMapper> resolver = provider
                .getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
        ObjectMapper mapper = resolver.getContext(TestSuite.class);
        TestSuite entity = testSuiteManager.findById(id, list);
        String json = mapper.writeValueAsString(entity);
        System.out.println(json);
        return Response.ok(json,
                MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestSuite getByName(@QueryParam("name") String name){
        return testSuiteManager.getTestSuite(name);
    }
}
