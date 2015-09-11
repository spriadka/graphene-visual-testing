/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arquillian.graphene.visual.testing.api.event;

import java.io.File;

/**
 *
 * @author spriadka
 */
public class CrawlMaskToJCREvent {
    
    private File suiteDescriptor;
    private String suiteName;
    
    public CrawlMaskToJCREvent(File suiteDescriptor, String suiteName){
        this.suiteDescriptor = suiteDescriptor;
        this.suiteName = suiteName;
    }

    /**
     * @return the suiteDescriptor
     */
    public File getSuiteDescriptor() {
        return suiteDescriptor;
    }

    /**
     * @return the suiteName
     */
    public String getSuiteName() {
        return suiteName;
    }
    
}
