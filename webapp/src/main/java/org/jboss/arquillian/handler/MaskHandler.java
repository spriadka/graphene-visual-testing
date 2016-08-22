/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.handler;

import org.dom4j.Document;
import org.jboss.arquillian.event.CrawlMaskToSuiteEvent;
import org.jboss.arquillian.event.DeleteMaskFromSuiteEvent;
import org.jboss.arquillian.model.testSuite.Mask;

/**
 *
 * @author spriadka
 */
public interface MaskHandler {
    public void deleteMasks(DeleteMaskFromSuiteEvent event);
    public void uploadMasks(CrawlMaskToSuiteEvent event);
    public void addMasksToConfiguration(Mask mask,Document doc);
    public void deleteMaskFromConfiguration(String maskID, Document doc);
    public void deleteMaskFromTest(String maskId, Document doc);
    public void addMasksToTest(Mask mask,Document doc);
}
