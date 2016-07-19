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
import org.jboss.rusheye.arquillian.event.StartCrawlingEvent;
import java.util.logging.Logger;
import org.jboss.rusheye.arquillian.event.FailedTestsCollection;
import org.jboss.rusheye.arquillian.event.InsertDescriptorAndPatternsHandlerEvent;
import org.jboss.rusheye.arquillian.event.VisuallyUnstableTestsCollection;

/**
 * Listener for the AfterSuite Event
 * @author spriadka
 */
public class AfterSuiteListener {

    @Inject
    private Instance<GrapheneVisualTestingConfiguration> visualTestingConfiguration;

    @Inject
    private Instance<ScreenshooterConfiguration> screenshooterConfiguration;

    @Inject
    private Event<StartParsingEvent> startParsingEvent;

    @Inject
    private Event<StartCrawlingEvent> crawlEvent;

    @Inject
    private Instance<ServiceLoader> serviceLoader;

    @Inject
    private Instance<FailedTestsCollection> failedTestsCollection;

    @Inject
    private Instance<VisuallyUnstableTestsCollection> visuallyUnstableTestsCollection;

    private static final Logger LOGGER = Logger.getLogger(AfterSuiteListener.class.getName());
    
    /**
     * Listens to the AfterSuite event and starts the process of visual comparison
     * @param event AfterSuite event observed
     */
    public void listenToAfterSuite(@Observes(precedence = Integer.MAX_VALUE) AfterSuite event) {
        String samplesPath = screenshooterConfiguration.get().getRootDir().getAbsolutePath();
        String descriptorAndPatternsDir
                = serviceLoader.get().onlyOne(DescriptorAndPatternsHandler.class).retrieveDescriptorAndPatterns();
        serviceLoader.get().onlyOne(DescriptorAndPatternsHandler.class).retreiveMasks();
        startParsingEvent.fire(new StartParsingEvent(descriptorAndPatternsDir, samplesPath, failedTestsCollection.get(), visuallyUnstableTestsCollection.get()));
    }
    
    /**
     * Retrieves the TestSuite descriptor and patterns created
     * @param event event observed
     */
    public void getDescriptionAndPatterns(@Observes InsertDescriptorAndPatternsHandlerEvent event) {
        serviceLoader.get().onlyOne(DescriptorAndPatternsHandler.class).retrieveDescriptorAndPatterns();

    }
}
