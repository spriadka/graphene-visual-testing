/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.handler.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jboss.arquillian.event.CrawlMaskToJCREvent;
import org.jboss.arquillian.event.CrawlMaskToSuiteEvent;
import org.jboss.arquillian.event.DeleteMaskFromSuiteEvent;
import org.jboss.arquillian.handler.MaskHandler;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.rusheye.RushEye;

/**
 *
 * @author spriadka
 */
@RequestScoped
public class MaskHandlerImpl implements MaskHandler {

    @Inject
    private Event<CrawlMaskToJCREvent> crawlingMasksDoneEvent;

    @Override
    public void deleteMasks(@Observes DeleteMaskFromSuiteEvent event) {
        File descriptor = event.getDescriptor();
        String maskID = event.getMask().getMaskID();
        try {
            Document suiteXml = new SAXReader().read(descriptor);
            deleteMaskFromConfiguration(maskID, suiteXml);
            deleteMaskFromTest(maskID, suiteXml);
            String suiteName = event.getMask().getTestSuiteName().split(":")[0];
            writeDocumentToFile(suiteName, suiteXml);
        } catch (Exception e) {

        }
    }

    @Override
    public void uploadMasks(@Observes CrawlMaskToSuiteEvent event) {
        File descriptor = event.getDescriptor();
        try {
            Document suiteXml = new SAXReader().read(descriptor);
            addMasksToConfiguration(event.getMask(), suiteXml);
            addMasksToTest(event.getMask(), suiteXml);
            String suiteName = event.getMask().getTestSuiteName().split(":")[0];
            writeDocumentToFile(suiteName, suiteXml);

        } catch (DocumentException ex) {
            Logger.getLogger(MaskHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addMasksToConfiguration(Mask mask, Document doc) {
        Namespace ns = Namespace.get(RushEye.NAMESPACE_VISUAL_SUITE);
        String xPath = "/*[namespace-uri()=\""
                + ns.getURI()
                + "\" and name()=\"visual-suite\"]/*[namespace-uri()=\""
                + ns.getURI() + "\" and name()=\"global-configuration\"]";
        Element configurationNode = (Element) doc.selectSingleNode(xPath);
        Element maskElement = configurationNode.addElement(QName.get("mask", ns));
        maskElement.addAttribute("id", mask.getMaskID());
        maskElement.addAttribute("source", mask.getSourceUrl());
        maskElement.addAttribute("type", "selective-alpha");

    }

    @Override
    public void deleteMaskFromConfiguration(String maskID, Document doc) {
        Namespace ns = Namespace.get(RushEye.NAMESPACE_VISUAL_SUITE);
        String xPath = "/*[namespace-uri()=\"" + ns.getURI() + "\" and name()=\"visual-suite\"]/*[namespace-uri()=\"" + ns.getURI() + "\" and name()=\"global-configuration\"]/*[namespace-uri()=\"" + ns.getURI() + "\" and name()=\"mask\" and @id=\"" + maskID + "\"]";
        Node maskElement = doc.selectSingleNode(xPath);
        maskElement.detach();
    }

    @Override
    public void deleteMaskFromTest(String maskID, Document doc) {
        Namespace ns = Namespace.get(RushEye.NAMESPACE_VISUAL_SUITE);
        String xPath = "/*[namespace-uri()=\""
                + ns.getURI()
                + "\" and name()=\"visual-suite\"]/*[namespace-uri()=\""
                + ns.getURI() + "\" and name()=\"test\"]/*[namespace-uri()=\""
                + ns.getURI() + "\" and name()=\"mask\" and @id=\"" + maskID + "\"]";
        Node maskElement = doc.selectSingleNode(xPath);
        maskElement.detach();
    }

    @Override
    public void addMasksToTest(Mask mask, Document doc) {
        Namespace ns = Namespace.get(RushEye.NAMESPACE_VISUAL_SUITE);
        String testName = mask.getTestSuiteName().split(":")[1].replaceAll("/", ".");
        testName = testName.substring(0, testName.lastIndexOf(".png"));
        String xPath = "/*[namespace-uri()=\"" + ns.getURI() + "\" and name()=\"visual-suite\"]/*[namespace-uri()=\"" + ns.getURI() + "\" and name()=\"test\" and @name=\"" + testName + "\"]";
        Element testElement = (Element) doc.selectSingleNode(xPath);
        Element patternElement = testElement.element(QName.get("pattern", ns));
        patternElement.detach();
        Element maskElement = testElement.addElement(QName.get("mask", ns));
        maskElement.addAttribute("id", mask.getMaskID());
        maskElement.addAttribute("source", mask.getSourceUrl());
        maskElement.addAttribute("type", "selective-alpha");
        testElement.add(patternElement);
    }

    private void writeDocumentToFile(String suiteName, Document doc) {
        File toWrite = new File("suite.xml");
        try {
            XMLWriter writer = new XMLWriter(new FileOutputStream(toWrite), OutputFormat.createPrettyPrint());
            writer.write(doc);
            crawlingMasksDoneEvent.fire(new CrawlMaskToJCREvent(toWrite, suiteName));
        } catch (IOException ex) {
            Logger.getLogger(MaskHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
