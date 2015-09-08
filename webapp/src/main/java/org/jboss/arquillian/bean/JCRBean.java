package org.jboss.arquillian.bean;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
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
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.arquillian.model.testSuite.Pattern;
import org.jboss.arquillian.model.testSuite.Sample;
import org.jboss.arquillian.util.ImageCreator;
import org.arquillian.graphene.visual.testing.configuration.GrapheneVisualTestingConfiguration;


@Named("jcrBean")
@ApplicationScoped
public class JCRBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(JCRBean.class.getSimpleName());

    @Resource(mappedName = "java:/jcr/graphene-visual-testing")
    private javax.jcr.Repository repository;

    @Inject
    private BasicAuthSessionStore sessionStore;
    
    @Inject
    @New
    private Instance<GrapheneVisualTestingConfiguration> gVC;

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
            Logger.getLogger(JCRBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JCRBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JCRBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JCRBean.class.getName()).log(Level.SEVERE, null, ex);
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
            //suiteNode.addNode(suiteNode.getPath() + "/" + masks + "/" + testClass + "/" + testName + "/" + beforeOrAfter + "/" + mask.getMaskID() + "/" + Property.JCR_CONTENT);
            Node masksNode = addNodeAndProceed(suiteNode, masks);
            LOGGER.info(masksNode.getPath());
            Node testClassNode = addNodeAndProceed(masksNode, testClass);
            LOGGER.info(testClassNode.getPath());
            Node testNameNode = addNodeAndProceed(testClassNode, testName);
            LOGGER.info(testNameNode.getPath());
            Node beforeOrAfterNode = addNodeAndProceed(testNameNode, beforeOrAfter);
            LOGGER.info(beforeOrAfterNode.getPath());
            Node idNode = addNodeAndProceed(beforeOrAfterNode, mask.getMaskID().toString());
            LOGGER.info(idNode.getPath());
            Node contentNode = addNodeAndProceed(idNode, Property.JCR_CONTENT);
            contentNode.setProperty(Property.JCR_DATA, session.getValueFactory().createBinary(new FileInputStream(ImageCreator.createImageFromBase64String(mask.getSourceData(), mask.getMaskID().toString()))));
            session.save();
            mask.setSourceUrl(gVC.get().getJcrContextRootURL() + "/binary" + contentNode.getProperty(Property.JCR_DATA).getPath());
        } catch (VersionException ex) {
            Logger.getLogger(JCRBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LockException ex) {
            Logger.getLogger(JCRBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConstraintViolationException ex) {
            Logger.getLogger(JCRBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(JCRBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JCRBean.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JCRBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
