package org.jboss.arquillian.rest;

import java.util.List;
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
import org.jboss.arquillian.managers.SampleManager;
import org.jboss.arquillian.managers.TestSuiteRunManager;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Sample;
import org.jboss.arquillian.model.testSuite.TestSuiteRun;
import org.jboss.logging.Logger;

/**
 *
 * @author jhuska
 */
@Path("/diffs")
@RequestScoped
public class DiffRESTService {
    
    @Inject
    private TestSuiteRunManager testSuiteRunManager;
    
    @Inject
    private SampleManager sampleManager;
    
    @Inject
    private PatternManager patternManager;
    
    @Inject
    private DiffManager diffManager;
    
    @Inject
    private JCRBean jcrBean;
    
    private final Logger LOGGER = Logger.getLogger(DiffRESTService.class);
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Long createDiff(Diff diff) {
        TestSuiteRun run = testSuiteRunManager.findById(diff.getTestSuiteRun().getTestSuiteRunID());
        Diff toCreate = new Diff();
        toCreate.setName(diff.getName());
        Sample assocSample = sampleManager.findById(diff.getSample().getSampleID());
        toCreate.setSample(assocSample);
        toCreate.setTestSuiteRun(run);
        toCreate.setUrlOfScreenshot(diff.getUrlOfScreenshot());
        toCreate.setPattern(patternManager.getPattern(assocSample.getName(), 
                assocSample.getTestSuiteRun().getTestSuite().getTestSuiteID()));
        return diffManager.createDiff(toCreate).getDiffID();
    }
    
    
}