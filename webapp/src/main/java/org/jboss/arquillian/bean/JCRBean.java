package org.jboss.arquillian.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Pattern;
import org.jboss.arquillian.model.testSuite.Sample;

@Named("jcrBean")
@ApplicationScoped
public class JCRBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(JCRBean.class.getSimpleName());

    @Resource(mappedName = "java:/jcr/graphene-visual-testing")
    private javax.jcr.Repository repository;

    @Inject
    private BasicAuthSessionStore sessionStore;

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

    
}
