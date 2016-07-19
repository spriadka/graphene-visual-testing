package org.jboss.arquillian.rest;

import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    
    private static final Logger LOGGER = Logger.getLogger(TestSuiteRESTService.class.getName());
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TestSuite> getAllTestSuites() {
        return testSuiteManager.getAllTestSuites();
    }
    
    @DELETE
    @Path("/{testSuiteID:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTestSuite(@PathParam("testSuiteID") long id) {
        LOGGER.info("I AM GOING TO DELETE TEST SUITE");
        TestSuite testSuiteToRemove = testSuiteManager.findById(id);
        jcrBean.removeTestSuite(testSuiteToRemove.getName());
        testSuiteManager.deleteTestSuite(testSuiteManager.findById(id));
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
    public TestSuite getTestSuite(@PathParam("testSuiteID") long id) {
        return testSuiteManager.findById(id);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestSuite getByName(@QueryParam("name") String name){
        return testSuiteManager.getTestSuite(name);
    }
}
