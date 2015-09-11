/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arquillian.graphene.visual.testing.api.event;

import java.io.File;
import java.util.List;
import org.arquillian.graphene.visual.testing.api.MaskFromREST;

/**
 *
 * @author spriadka
 */
public class CrawlMaskToSuiteEvent {
    
    private List<MaskFromREST> masks;
    
    private File suiteDescriptor;
    
    public CrawlMaskToSuiteEvent(List<MaskFromREST> masks, File suiteDescriptor){
        this.masks = masks;
        this.suiteDescriptor = suiteDescriptor;
    }

    /**
     * @return the masks
     */
    public List<MaskFromREST> getMasks() {
        return masks;
    }

    /**
     * @param masks the masks to set
     */
    public void setMasks(List<MaskFromREST> masks) {
        this.masks = masks;
    }

    /**
     * @return the suiteDescriptor
     */
    public File getSuiteDescriptor() {
        return suiteDescriptor;
    }

    /**
     * @param suiteDescriptor the suiteDescriptor to set
     */
    public void setSuiteDescriptor(File suiteDescriptor) {
        this.suiteDescriptor = suiteDescriptor;
    }
    
}
