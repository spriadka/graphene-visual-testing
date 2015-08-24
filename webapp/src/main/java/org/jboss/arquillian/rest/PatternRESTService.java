package org.jboss.arquillian.rest;

import org.jboss.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.bean.JCRBean;
import org.jboss.arquillian.managers.DiffManager;
import org.jboss.arquillian.managers.PatternManager;
import org.jboss.arquillian.managers.TestSuiteManager;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Pattern;
import org.jboss.arquillian.model.testSuite.Sample;
import org.jboss.arquillian.model.testSuite.TestSuite;

/**
 *
 * @author jhuska
 */
@Path("/patterns")
@RequestScoped
public class PatternRESTService {

    @Inject
    private PatternManager patternManager;

    @Inject
    private TestSuiteManager testSuiteManager;
    
    @Inject
    private SamplesRESTService samplesRESTService;
    
    @Inject
    private DiffManager diffManager;
    
    @Inject
    private JCRBean jcrBean;
    
    private final Logger LOGGER = Logger.getLogger(PatternRESTService.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Pattern createPattern(Pattern pattern) {
        TestSuite testSuite = testSuiteManager.getTestSuite(pattern.getTestSuite().getName());
        Pattern toCreate = new Pattern();
        toCreate.setName(pattern.getName());
        toCreate.setTestSuite(testSuite);
        toCreate.setUrlOfScreenshot(pattern.getUrlOfScreenshot());
        toCreate.setLastModificationDate(pattern.getLastModificationDate());
        toCreate = patternManager.createPattern(toCreate);
        return toCreate;
    }
    
    @PUT
    @Path("/reject/{diffID:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rejectPattrn(@PathParam("diffID") long diffID) {
        Diff diff = diffManager.getDiff(diffID);
        jcrBean.changePatternForSample(diff.getTestSuiteRun().getTestSuite().getName(),
                "" + diff.getTestSuiteRun().getTimestamp().getTime(), diff.getSample().getName());
        samplesRESTService.rejectSample(diffID);
        return Response.ok().build();
    }
    
    @PUT
    @Path("/update/{diffID:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptSampleAsNewPattern(@PathParam("diffID") long diffID){
        LOGGER.info("clicked");
        Diff diff = diffManager.getDiff(diffID);
        acceptSampleAsNewPattern(diff);
        return Response.ok().build();
        
    }
    
    private void acceptSampleAsNewPattern(Diff diff){
        Pattern pattern = diff.getPattern();
        LOGGER.info("pattern retrieved");
        LOGGER.info(pattern.getName());
        Sample sample = diff.getSample();
        jcrBean.changePattern(pattern, sample);
        LOGGER.info("accepted new sample as pattern");
        
    }
    
}
