package org.jboss.arquillian.rest;

import java.util.ArrayList;
import java.util.Date;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.bean.JCRBean;
import org.jboss.arquillian.managers.DiffManager;
import org.jboss.arquillian.managers.PatternManager;
import org.jboss.arquillian.managers.SampleManager;
import org.jboss.arquillian.managers.TestSuiteManager;
import org.jboss.arquillian.managers.TestSuiteRunManager;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Pattern;
import org.jboss.arquillian.model.testSuite.Sample;
import org.jboss.arquillian.model.testSuite.TestSuiteRun;

/**
 *
 * @author jhuska
 */
@Path("/runs")
@RequestScoped
public class TestSuiteRunRESTService {

    @Inject
    private TestSuiteRunManager testSuiteRunManager;

    @Inject
    private TestSuiteManager testSuiteManager;

    @Inject
    private DiffManager diffManager;

    @Inject
    private SampleManager sampleManager;

    @Inject
    private PatternManager patternManager;

    @Inject
    private JCRBean jcrBean;

    private static final Logger LOGGER = Logger.getLogger(TestSuiteRunRESTService.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Long createTestSuiteRun(TestSuiteRun testSuiteRun) {
        testSuiteRun.setTestSuite(testSuiteManager.getTestSuite(testSuiteRun.getTestSuite().getName()));
        return testSuiteRunManager.createTestSuiteRun(testSuiteRun).getTestSuiteRunID();
    }

    //TODO Duplicated entities returned problem
    @GET
    @Path("/{testSuiteRunID:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public TestSuiteRun getTestSuiteRun(@PathParam("testSuiteRunID") long id) {
        TestSuiteRun result = testSuiteRunManager.findById(id);
        return result;
    }

    @DELETE
    @Path("/{testSuiteRunID:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTestSuiteRun(@PathParam("testSuiteRunID") long testSuiteRunID) {
        TestSuiteRun testSuiteRunToRemove = testSuiteRunManager.findById(testSuiteRunID);
        jcrBean.removeTestSuiteRun("" + testSuiteRunToRemove.getTimestamp().getTime(),
                testSuiteRunToRemove.getTestSuite().getName());
        deleteDiffsFromTestSuiteRun(testSuiteRunToRemove);
        deleteSamplesFromTestSuiteRun(testSuiteRunToRemove);
        testSuiteRunManager.deleteTestSuiteRun(testSuiteRunToRemove);
        return Response.ok().build();
    }

    private void deleteSamplesFromTestSuiteRun(TestSuiteRun run) {
        for (Sample sample : run.getSamples()) {
            sampleManager.deleteSample(sample);
        }
    }

    private void deleteDiffsFromTestSuiteRun(TestSuiteRun run) {
        for (Diff diff : run.getDiffs()) {
            diffManager.deleteDiff(diff);
        }
    }

    @GET
    @Path("/comparison-result/{testSuiteRunID:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ComparisonResult> getTestSuiteRunComparisonResults(@PathParam("testSuiteRunID") long id) {
        List<ComparisonResult> result = new ArrayList<>();
        List<Diff> diffs = diffManager.getDiffsForRun(id);
        List<Long> alreadyUploadedSamples = new ArrayList<>();
        if (diffManager.areThereDiffs(id)) {
            for (Diff diff : diffs) {
                alreadyUploadedSamples.add(diff.getSample().getSampleID());
                result.add(new ComparisonResult(diff.getPattern().getUrlOfScreenshot(), diff.getPattern().getPatternID(), diff.getPattern().getLastModificationDate(), diff.getSample().getUrlOfScreenshot(),diff.getSample().getSampleID(), diff.getSample().getLastModificationDate(), diff.getUrlOfScreenshot(), diff.getDiffID(), getTestClassName(diff), getTestName(diff)));
            }
        }

        List<Sample> samples = sampleManager.getSamples(id);
        for (Sample sample : samples) {
            if (!alreadyUploadedSamples.contains(sample.getSampleID())) {
                Pattern pattern = patternManager.getPattern(sample.getName(), sample.getTestSuiteRun().getTestSuite().getTestSuiteID());
                result.add(new ComparisonResult(pattern.getUrlOfScreenshot(), pattern.getPatternID(),pattern.getLastModificationDate(), sample.getUrlOfScreenshot(), sample.getSampleID(),sample.getLastModificationDate(), null, id,getTestClassName(sample), getTestName(sample)));
            }
        }
        return result;
    }

    private String getTestClassName(Diff diff) {
        return diff.getSample().getName().split("/")[0];
    }

    private String getTestClassName(Sample sample) {
        return sample.getName().split("/")[0];
    }

    private String getTestName(Diff diff) {
        return diff.getSample().getName().split("/")[1];
    }

    private String getTestName(Sample sample) {
        return sample.getName().split("/")[1];
    }
}
