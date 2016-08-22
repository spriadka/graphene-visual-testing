/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.event;

import java.io.File;
import org.jboss.arquillian.model.testSuite.Mask;

/**
 *
 * @author spriadka
 */
public class DeleteMaskFromSuiteEvent {
    private  Mask mask;
    private  File descriptor;

    /**
     * @return the masks
     */
    public Mask getMask() {
        return mask;
    }

    /**
     * @param masks the masks to set
     */
    public void setMasks(Mask mask) {
        this.mask = mask;
    }

    /**
     * @return the descriptor
     */
    public File getDescriptor() {
        return descriptor;
    }

    /**
     * @param descriptor the descriptor to set
     */
    public void setDescriptor(File descriptor) {
        this.descriptor = descriptor;
    }
    
    public DeleteMaskFromSuiteEvent(Mask mask,File descritpor){
        this.mask = mask;
        this.descriptor = descritpor;
    }
}
