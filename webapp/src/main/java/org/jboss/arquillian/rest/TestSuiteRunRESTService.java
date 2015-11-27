package org.jboss.arquillian.rest;

import java.util.ArrayList;
import java.util.List;
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
import org.jboss.arquillian.managers.MaskManager;
import org.jboss.arquillian.managers.PatternManager;
import org.jboss.arquillian.managers.SampleManager;
import org.jboss.arquillian.managers.TestSuiteManager;
import org.jboss.arquillian.managers.TestSuiteRunManager;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Pattern;
import org.jboss.arquillian.model.testSuite.Sample;
import org.jboss.arquillian.model.testSuite.TestSuiteRun;
import org.jboss.arquillian.rest.builder.DiffComparisonResultBuilder;
import org.jboss.arquillian.rest.builder.SampleComparisonResultBuilder;
import org.jboss.logging.Logger;

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
    private MaskManager maskManager;

    @Inject
    private JCRBean jcrBean;

    private static final Logger LOGGER = Logger.getLogger(TestSuiteRunRESTService.class);

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
                result.add(new DiffComparisonResultBuilder().manager(maskManager).diff(diff).build());
                alreadyUploadedSamples.add(diff.getSample().getSampleID());
            }
        }
        List<Sample> samples = sampleManager.getSamples(id);
        for (Sample sample : samples) {
            Pattern pattern = patternManager.getPattern(sample.getName(), sample.getTestSuiteRun().getTestSuite().getTestSuiteID());
            if (!alreadyUploadedSamples.contains(sample.getSampleID())) {
                result.add(new SampleComparisonResultBuilder().sample(sample).pattern(pattern).build());
            }
        }
        return result;
    }

}
