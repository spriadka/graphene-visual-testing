/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arquillian.graphene.visual.testing.api.event;

import java.io.File;
import org.arquillian.graphene.visual.testing.api.MaskFromREST;

/**
 *
 * @author spriadka
 */
public class DeleteMaskFromSuiteEvent {
    
    private MaskFromREST mask;
    private File suiteDescriptor;
    
    public DeleteMaskFromSuiteEvent(MaskFromREST mask, File suiteDescriptor){
        this.mask = mask;
        this.suiteDescriptor = suiteDescriptor;
    }
    /**
     * @return the maskID
     */
    public MaskFromREST getMask() {
        return mask;
    }

    /**
     * @param maskID the maskID to set
     */
    public void setMask(MaskFromREST mask) {
        this.mask = mask;
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
