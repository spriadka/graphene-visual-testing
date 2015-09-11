/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arquillian.graphene.visual.testing.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arquillian.graphene.visual.testing.api.MaskFromREST;
import org.arquillian.graphene.visual.testing.api.MaskHandler;
import org.arquillian.graphene.visual.testing.api.event.CrawlMaskToSuiteEvent;
import org.arquillian.graphene.visual.testing.api.event.CrawlMaskToJCREvent;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.rusheye.RushEye;

/**
 *
 * @author spriadka
 */
public class JCRMaskHandler implements MaskHandler{
   
    @Inject
    private Event<CrawlMaskToJCREvent> crawlingMasksDoneEvent;
    
    
    @Override
    public void deleteMasks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void uploadMasks(@Observes CrawlMaskToSuiteEvent crawlMaskEvent) {
        File descriptor = crawlMaskEvent.getSuiteDescriptor();
        try {
            Document suiteXml = new SAXReader().read(descriptor);
            addMasksToConfiguration(crawlMaskEvent.getMasks(), suiteXml);
            addMasksToTest(crawlMaskEvent.getMasks(), suiteXml);
            String suiteName = crawlMaskEvent.getMasks().get(0).getName().split(":")[0];
            writeDocumentToFile(suiteName,suiteXml);
            
        } catch (DocumentException ex) {
            Logger.getLogger(JCRMaskHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addMasksToConfiguration(List<MaskFromREST> masks, Document doc){
        Element configurationNode = (Element) doc.selectSingleNode("/visual-suite/global-configuration");
        Namespace ns = Namespace.get(RushEye.NAMESPACE_VISUAL_SUITE);
        for (MaskFromREST mask : masks){
            Element maskElement = configurationNode.addElement(QName.get("mask",ns));
            maskElement.addAttribute("id", mask.getId().toString());
            maskElement.addAttribute("source", mask.getSourceUrl());
            maskElement.addAttribute("type", mask.getMaskType().value());
        }
    }
    
    private void addMasksToTest(List<MaskFromREST> masks, Document doc){
        Namespace ns = Namespace.get(RushEye.NAMESPACE_VISUAL_SUITE);
        for (MaskFromREST mask : masks){
            String testName = mask.getName().split(":")[1].replaceAll("/", ".");
            Element testElement  = (Element) doc.selectSingleNode("/visual-suite-test[@name=\"" + testName + "\"]");
            Element maskElement = testElement.addElement(QName.get("mask",ns ));
            maskElement.addAttribute("id", mask.getId().toString());
            maskElement.addAttribute("source", mask.getSourceUrl());
            maskElement.addAttribute("type", mask.getMaskType().value());
        }
    }
    
    private void writeDocumentToFile(String suiteName, Document doc){
        File toWrite = new File("suite.xml");
        try {
            XMLWriter writer = new XMLWriter(new FileWriter(toWrite),OutputFormat.createPrettyPrint());
            writer.write(doc);
            crawlingMasksDoneEvent.fire(new CrawlMaskToJCREvent(toWrite,suiteName));
        } catch (IOException ex) {
            Logger.getLogger(JCRMaskHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    
}
