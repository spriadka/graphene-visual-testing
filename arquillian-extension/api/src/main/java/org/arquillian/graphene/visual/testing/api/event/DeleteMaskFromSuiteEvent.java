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
public class DeleteMaskFromSuiteEvent {
    
    private Long maskID;
    private File suiteDescriptor;
    
    public DeleteMaskFromSuiteEvent(Long maskID, File suiteDescriptor){
        this.maskID = maskID;
        this.suiteDescriptor = suiteDescriptor;
    }
    /**
     * @return the maskID
     */
    public Long getMaskID() {
        return maskID;
    }

    /**
     * @param maskID the maskID to set
     */
    public void setMaskID(Long maskID) {
        this.maskID = maskID;
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
