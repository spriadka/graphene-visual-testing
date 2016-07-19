package org.arquillian.graphene.visual.testing.impl;

import org.arquillian.graphene.visual.testing.api.DescriptorAndPatternsHandler;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.rusheye.arquillian.event.CrawlingDoneEvent;
import org.jboss.rusheye.arquillian.event.StartCrawlMissingTestsEvent;

/**
 * Observer for the crawling process of visual comparison
 * @author jhuska
 */
public class CrawlingDoneObserver {

    @Inject
    private Instance<ServiceLoader> serviceLoader;
    
    /**
     * Fired when crawling process of patterns finishes
     * @param event CrawlingDoneEvent observed
     */
    public void saveDescriptorAndPatterns(@Observes CrawlingDoneEvent event) {
        DescriptorAndPatternsHandler handler = serviceLoader.get().onlyOne(DescriptorAndPatternsHandler.class);
        boolean success = handler.saveDescriptorAndPatterns();
        if(success) {
            System.out.println("Descriptor and Patterns saved!");
        }
    }
    
    /**
     * Fired when crawling process of missing patterns finished
     * @param event StartCrawlingMissingTestsEvent observed
     */
    public void saveDescriptorAndMissingPatterns(@Observes StartCrawlMissingTestsEvent event){
        DescriptorAndPatternsHandler handler = serviceLoader.get().onlyOne(DescriptorAndPatternsHandler.class);
        boolean success = handler.saveDescriptorAndMissingPatterns(event);
        if(success) {
            System.out.println("Descriptor and Patterns saved!");
        }
    }
    
    
}
