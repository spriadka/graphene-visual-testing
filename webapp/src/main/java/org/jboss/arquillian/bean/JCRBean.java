package org.jboss.arquillian.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import org.jboss.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.VersionException;
import org.apache.commons.io.FileUtils;
import org.arquillian.graphene.visual.testing.api.event.CrawlMaskDoneEvent;
import org.arquillian.graphene.visual.testing.api.event.CrawlMaskToJCREvent;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.arquillian.model.testSuite.Pattern;
import org.jboss.arquillian.model.testSuite.Sample;
import org.arquillian.graphene.visual.testing.configuration.GrapheneVisualTestingConfiguration;
import org.jboss.arquillian.util.Base64;


@Named("jcrBean")
@ApplicationScoped
public class JCRBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(JCRBean.class);

    @Resource(mappedName = "java:/jcr/graphene-visual-testing")
    private javax.jcr.Repository repository;

    @Inject
    private BasicAuthSessionStore sessionStore;
    
    @Inject
    @New
    private Instance<GrapheneVisualTestingConfiguration> gVC;
    
    @org.jboss.arquillian.core.api.annotation.Inject
    private Event<CrawlMaskDoneEvent> event;

    public Session getSession() throws RepositoryException {
        return repository.login(new SimpleCredentials(sessionStore.getLogin(),
                sessionStore.getPassword().toCharArray()));
    }

    public void removeTestSuite(String testSuiteName) {
        Session session;
        try {
            session = getSession();
            session.getRootNode().getNode(testSuiteName).remove();
            session.save();
        } catch (RepositoryException ex) {
            LOGGER.error(ex);
        }
    }

    public void removeTestSuiteRun(String timestampOfRun, String testSuiteName) {
        Session session;
        try {
            session = getSession();
            Node testSuiteRunNode = session.getRootNode()
                    .getNode(testSuiteName)
                    .getNode("runs")
                    .getNode(timestampOfRun);
            testSuiteRunNode.remove();
            session.save();
        } catch (RepositoryException ex) {
            LOGGER.error(ex);
        }
    }

    public void changePatternForSample(String testSuiteName, String timestampOfRun, String sampleName) {
        Session session;
        try {
            session = getSession();
            Node sample = session.getRootNode()
                    .getNode(testSuiteName)
                    .getNode("runs")
                    .getNode(timestampOfRun)
                    .getNode("samples")
                    .getNode(sampleName);
            Node pattern = session.getRootNode()
                    .getNode(testSuiteName)
                    .getNode("patterns")
                    .getNode(sampleName);
            String patternPath = pattern.getPath();
            pattern.remove();
            session.save();
            session.move(sample.getPath(), patternPath);
            session.save();
        } catch (RepositoryException ex) {
            LOGGER.error(ex);
        }
    }

    public void changePattern(Pattern pattern, Sample sample) {

        Session session;
        try {
            session = getSession();
            String testSuiteName = pattern.getTestSuite().getName();
            Node patternContent = session.getRootNode().getNode(testSuiteName).getNode("patterns").getNode(pattern.getName()).getNode("jcr:content");
            Binary patternValue = patternContent.getProperty("jcr:data").getBinary();
            String timestamp = "" + sample.getTestSuiteRun().getTimestamp().getTime();
            Node sampleContent = session.getRootNode().getNode(testSuiteName).getNode("runs").getNode(timestamp).getNode("samples").getNode(sample.getName()).getNode("jcr:content");
            Binary sampleValue = sampleContent.getProperty("jcr:data").getBinary();
            patternValue = sampleValue;
            patternContent.setProperty("jcr:data", patternValue);
            session.save();

        } catch (RepositoryException ex) {
            LOGGER.error(ex);
        }

    }

    public void getMaskUrlFromData(Mask mask) {
        Session session;
        try {
            session = getSession();
            String testSuiteName = mask.getTestSuite().getName();
            String masks = "masks";
            String[] names = mask.getSample().getName().split("/");
            String testClass = names[0];
            String testName = names[1];
            String beforeOrAfter = names[2].substring(0, names[2].indexOf("."));
            Node suiteNode = session.getRootNode().getNode(testSuiteName);
            Node masksNode = addNodeAndProceed(suiteNode, masks);
            Node testClassNode = addNodeAndProceed(masksNode, testClass);
            Node testNameNode = addNodeAndProceed(testClassNode, testName);
            Node beforeOrAfterNode = addNodeAndProceed(testNameNode, beforeOrAfter);
            Node idNode = addNodeAndProceed(beforeOrAfterNode, mask.getMaskID().toString());
            Node contentNode = addNodeAndProceed(idNode, Property.JCR_CONTENT);
            String imageType = mask.getSourceData().split(";")[0].split(":")[1];
            contentNode.setProperty(Property.JCR_MIMETYPE, imageType);
            byte[] imageData = Base64.decodeFast(mask.getSourceData().split(";")[1].split(",")[1]);
            contentNode.setProperty(Property.JCR_DATA, session.getValueFactory().createBinary(new ByteArrayInputStream(imageData)));
            session.save();
            mask.setSourceUrl(gVC.get().getJcrContextRootURL() + "/binary" + contentNode.getProperty(Property.JCR_DATA).getPath());
        } catch (VersionException | LockException | ConstraintViolationException ex) {
            LOGGER.error(ex);
        } catch (RepositoryException ex) {
            LOGGER.error(ex);
        }
    }

    private Node addNodeAndProceed(Node node, String nodeName) {

        String ntFileOrFolder = (nodeName.matches("[0-9][0-9]*")) ? NodeType.NT_FILE : NodeType.NT_FOLDER;
        String resource = NodeType.NT_RESOURCE;
        try {

            if (!node.hasNode(nodeName)) {
                if (!nodeName.equals(Property.JCR_CONTENT)) {
                    return node.addNode(nodeName, ntFileOrFolder);
                } else {
                    return node.addNode(nodeName, resource);
                }

            }
            return node.getNode(nodeName);

        } catch (RepositoryException ex) {
            LOGGER.error(ex);
        }
        return null;
    }
    
    public File getDescriptorForMask(Mask mask){
        Session session;
        String suiteXml = "suite.xml";
        try{
            session = getSession();
            Node descriptorNode = session.getRootNode().getNode(mask.getTestSuite().getName()).getNode(suiteXml).getNode(Property.JCR_CONTENT);
            File toReturn = new File("suite.xml");
            InputStream dataToWrite = descriptorNode.getProperty(Property.JCR_DATA).getBinary().getStream();
            FileUtils.copyInputStreamToFile(dataToWrite, toReturn);
            return toReturn;
        }
        catch (RepositoryException | IOException e){
            LOGGER.error(e);
        }
        return null;      
    }
    
    public void writeSuiteXml(@Observes CrawlMaskToJCREvent crawlMasksTestsDoneEvent){
        Session session;
        try{
            session = getSession();
            Node descriptorNode = session.getRootNode().getNode(crawlMasksTestsDoneEvent.getSuiteName()).getNode("suite.xml").getNode(Property.JCR_CONTENT);
            Binary toWrite = session.getValueFactory().createBinary(new FileInputStream(crawlMasksTestsDoneEvent.getSuiteDescriptor()));
            descriptorNode.setProperty(Property.JCR_DATA, toWrite);
            event.fire(new CrawlMaskDoneEvent());
        }
        catch (RepositoryException | FileNotFoundException ex){
            LOGGER.error(ex);        
        }
        
    }

}
