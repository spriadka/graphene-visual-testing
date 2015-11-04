package org.arquillian.graphene.visual.testing.api;

import org.jboss.rusheye.arquillian.event.StartCrawlMissingTestsEvent;

/**
 *
 * @author jhuska
 */
public interface DescriptorAndPatternsHandler {

    /**
     * Saves created descriptor and respective patterns, 
     * so later it they can be retrieved and compared with new samples
     * 
     * @return 
     */
    boolean saveDescriptorAndPatterns();
    
    boolean saveDescriptorAndMissingPatterns(StartCrawlMissingTestsEvent event);
    
    /**
     * Retrieves descriptor and respective patterns, so the comparison process
     * can be done.
     * 
     * @return the path under which descriptor and patterns were saved
     */
    String retrieveDescriptorAndPatterns();
    
    void retreiveMasks();
}
