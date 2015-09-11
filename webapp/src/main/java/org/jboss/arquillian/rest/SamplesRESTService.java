package org.jboss.arquillian.rest;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.bean.JCRBean;
import org.jboss.arquillian.managers.DiffManager;
import org.jboss.arquillian.managers.MaskManager;
import org.jboss.arquillian.managers.SampleManager;
import org.jboss.arquillian.managers.TestSuiteRunManager;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.arquillian.model.testSuite.Sample;
import org.jboss.arquillian.model.testSuite.TestSuiteRun;
import org.jboss.logging.Logger;

/**
 *
 * @author jhuska
 */
@Path("/samples")
@RequestScoped
public class SamplesRESTService {
    
    @Inject
    private TestSuiteRunManager testSuiteRunManager;
    
    @Inject
    private SampleManager sampleManager;
    
    @Inject
    private DiffManager diffManager;
    
    @Inject
    private MaskManager maskManager;
    
    @Inject
    private JCRBean jcrBean;
    
    private Logger LOGGER = Logger.getLogger(SamplesRESTService.class);
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Long createSample(Sample sample) {
        TestSuiteRun testSuiteRun = testSuiteRunManager.findById(sample.getTestSuiteRun().getTestSuiteRunID());
        Sample toCreate = new Sample();
        toCreate.setName(sample.getName());
        toCreate.setTestSuiteRun(testSuiteRun);
        toCreate.setUrlOfScreenshot(sample.getUrlOfScreenshot());
        toCreate.setLastModificationDate(sample.getLastModificationDate());
        return sampleManager.createTestSuiteRun(toCreate).getSampleID();
    }
    
    @PUT
    @Path("/reject/{diffID:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rejectSample(@PathParam("diffID") long diffID) {
        Diff diff = diffManager.getDiff(diffID);
        deleteDiffFromDatabase(diff);
        LOGGER.info("Diff deleted from database");
        deleteSampleFromDatabase(diff);
        LOGGER.info("Sample deleted from database");
        //deleteTestSuiteRun(diff);
        return Response.ok().build();
    }
    
    @GET
    @Path("/{sampleID:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Sample getSample(@PathParam("sampleID") long sampleID){
        return sampleManager.findById(sampleID);
    }
    
    private void deleteTestSuiteRun(Diff diff) {
        if (!diffManager.areThereDiffs(diff.getTestSuiteRun().getTestSuiteRunID())) {
            jcrBean.removeTestSuiteRun(
                    "" + diff.getTestSuiteRun().getTimestamp().getTime(),
                    diff.getTestSuiteRun().getTestSuite().getName());
            LOGGER.info("test suite run deleted from JCR");
            TestSuiteRun run = diff.getTestSuiteRun();
            LOGGER.info("test suite run obtained from diff which was deleted!");
            testSuiteRunManager.deleteTestSuiteRun(run);
        }
    }
    
    private void deleteDiffFromDatabase(Diff diff) {
        long testSuiteRunID = diff.getTestSuiteRun().getTestSuiteRunID();
        diffManager.deleteDiff(diff);
    }
    
    private void deleteSampleFromDatabase(Diff diff) {
        sampleManager.deleteSample(diff.getSample());
    }
    
    private void deleteMasksForSample(Diff diff){
        List<Mask> masks = maskManager.getMasksForSample(diff.getSample().getSampleID());
        for (Mask mask : masks){
            maskManager.deleteMask(mask);
        }
    }
}
