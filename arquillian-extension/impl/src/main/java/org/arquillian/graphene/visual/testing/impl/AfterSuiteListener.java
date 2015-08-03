package org.arquillian.graphene.visual.testing.impl;

import org.arquillian.extension.recorder.screenshooter.ScreenshooterConfiguration;
import org.arquillian.graphene.visual.testing.api.DescriptorAndPatternsHandler;
import org.arquillian.graphene.visual.testing.configuration.GrapheneVisualTestingConfiguration;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.rusheye.arquillian.event.StartParsingEvent;
import org.jboss.rusheye.arquillian.event.StartCrawlinglEvent;
import java.util.logging.Logger;
import org.jboss.rusheye.arquillian.event.FailedTestsCollection;
import org.jboss.rusheye.arquillian.event.VisuallyUnstableTestsCollection;

public class AfterSuiteListener {

    @Inject
    private Instance<GrapheneVisualTestingConfiguration> visualTestingConfiguration;

    @Inject
    private Instance<ScreenshooterConfiguration> screenshooterConfiguration;

    @Inject
    private Event<StartParsingEvent> startParsingEvent;

    @Inject
    private Event<StartCrawlinglEvent> crawlEvent;

    @Inject
    private Instance<ServiceLoader> serviceLoader;

    @Inject
    private Instance<FailedTestsCollection> failedTestsCollection;

    @Inject
    private Instance<VisuallyUnstableTestsCollection> visuallyUnstableTestsCollection;

    private static final Logger LOGGER = Logger.getLogger(AfterSuiteListener.class.getName());

    public void listenToAfterSuite(@Observes AfterSuite event) {
        String samplesPath = screenshooterConfiguration.get().getRootDir().getAbsolutePath();
        if (visualTestingConfiguration.get().isFirstRun()) {

            //HANDLES FAILED TESTS IN FIRST RUN 
            if (!failedTestsCollection.get().getTests().isEmpty() || !visuallyUnstableTestsCollection.get().getTests().isEmpty()) {
                crawlEvent.fire(new StartCrawlinglEvent(samplesPath, failedTestsCollection.get(),
                        visuallyUnstableTestsCollection.get()));
                String descriptorAndPatternsDir
                        = serviceLoader.get().onlyOne(DescriptorAndPatternsHandler.class).retrieveDescriptorAndPatterns();
                startParsingEvent.fire(new StartParsingEvent(descriptorAndPatternsDir, samplesPath,
                        failedTestsCollection.get(),
                        visuallyUnstableTestsCollection.get()));

            } else {
                crawlEvent.fire(new StartCrawlinglEvent(samplesPath, failedTestsCollection.get(),
                        visuallyUnstableTestsCollection.get()));
            }

        } else {
            String descriptorAndPatternsDir
                    = serviceLoader.get().onlyOne(DescriptorAndPatternsHandler.class).retrieveDescriptorAndPatterns();
            startParsingEvent.fire(new StartParsingEvent(descriptorAndPatternsDir, samplesPath,
                    failedTestsCollection.get(),
                    visuallyUnstableTestsCollection.get()));
        }
    }
}
