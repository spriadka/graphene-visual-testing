package org.arquillian.graphene.visual.testing.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import org.apache.commons.io.FileUtils;
import org.arquillian.graphene.visual.testing.api.DescriptorAndPatternsHandler;
import org.arquillian.graphene.visual.testing.configuration.GrapheneVisualTestingConfiguration;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.rusheye.arquillian.configuration.RusheyeConfiguration;
import org.jboss.rusheye.arquillian.event.StartCrawlMissingTestsEvent;

/**
 *
 * @author jhuska
 */
public class DummyDescriptorAndPatternsHandler implements DescriptorAndPatternsHandler {

    @Inject
    private Instance<GrapheneVisualTestingConfiguration> visualTestingConfiguration;

    @Inject
    private Instance<ReporterConfiguration> reporterConfiguration;

    private static final File SCREENSHOTS_REPO = new File("/tmp/visual-testing");

    @Override
    public boolean saveDescriptorAndPatterns() {
        copyNewScreenshotsToRepository();
        return true;
    }

    @Override
    public String retrieveDescriptorAndPatterns() {
        return copyNewScreenshotsFromRepository();
    }

    private String copyNewScreenshotsFromRepository() {
        File sourceOfPatterns = new File(
                SCREENSHOTS_REPO.getAbsolutePath()
                + File.separator
                + visualTestingConfiguration.get().getLastValidScreenshots());
        File destinationForPatterns = new File("target/" + visualTestingConfiguration.get().getLastValidScreenshots());
        try {
            FileUtils.copyDirectory(sourceOfPatterns, destinationForPatterns);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destinationForPatterns.getAbsolutePath();
    }

    private void copyNewScreenshotsToRepository() {
        Timestamp thisRunScreenshotsTimestamp = new Timestamp(System.currentTimeMillis());

        File destForNewScreenshots = new File(
                SCREENSHOTS_REPO.getAbsolutePath()
                + File.separator
                + thisRunScreenshotsTimestamp.toString().replaceAll("\\s", "_").replaceAll(":", "-").replaceAll("\\.", "-"));
        destForNewScreenshots.mkdir();
        try {
            FileUtils.copyDirectory(reporterConfiguration.get().getRootDir(), destForNewScreenshots);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean saveDescriptorAndMissingPatterns(StartCrawlMissingTestsEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void retreiveMasks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
