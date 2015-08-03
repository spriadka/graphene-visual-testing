package org.arquillian.graphene.visual.testing.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.arquillian.extension.recorder.screenshooter.ScreenshooterConfiguration;
import org.arquillian.graphene.visual.testing.api.SamplesAndDiffsHandler;
import org.arquillian.graphene.visual.testing.configuration.GrapheneVisualTestingConfiguration;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.rusheye.arquillian.configuration.RusheyeConfiguration;
import org.jboss.rusheye.arquillian.event.ParsingDoneEvent;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.SAXException;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.http.entity.StringEntity;
import org.jboss.rusheye.suite.ResultConclusion;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author jhuska
 */
public class JCRSamplesAndDiffsHandler implements SamplesAndDiffsHandler {

    private static final Logger LOGGER = Logger.getLogger(JCRSamplesAndDiffsHandler.class.getName());

    @Inject
    private Instance<RusheyeConfiguration> rusheyeConf;

    @Inject
    private Instance<GrapheneVisualTestingConfiguration> grapheneVisualTestingConf;

    @Inject
    private Instance<ScreenshooterConfiguration> screenshooterConf;

    @Inject
    private Instance<DiffsUtils> diffsUtils;

    private final Map<String, Long> sampleAndItsIDs = new HashMap<>();

    private Document resultXML = null;

    @Override
    public void saveSamplesAndDiffs(@Observes ParsingDoneEvent parsingDoneEvent) {
        Date timestamp = new Date();
        String timestampWithoutWhiteSpaces = "" + timestamp.getTime();
        String suiteName = grapheneVisualTestingConf.get().getTestSuiteName();
        GrapheneVisualTestingConfiguration gVC = grapheneVisualTestingConf.get();
        CloseableHttpClient httpclient = RestUtils.getHTTPClient(gVC.getJcrContextRootURL(), gVC.getJcrUserName(), gVC.getJcrPassword());
        File resultDescriptor = new File(rusheyeConf.get().getWorkingDirectory()
                + File.separator
                + rusheyeConf.get().getResultOutputFile());

        //UPLOADING RESULT DESCRIPTOR
        HttpPost postResultDescriptor = new HttpPost(gVC.getJcrContextRootURL() + "/upload/" + suiteName + "/runs/"
                + timestampWithoutWhiteSpaces + "/result.xml");
        FileEntity descriptorEntity = new FileEntity(resultDescriptor, ContentType.APPLICATION_XML);
        postResultDescriptor.setEntity(descriptorEntity);
        RestUtils.executePost(postResultDescriptor, httpclient,
                String.format("Suite result descriptor for %s uploaded!", suiteName),
                String.format("Error while uploading test suite result descriptor for test suite: %s", suiteName));

        //CREATE TEST SUITE RUN IN DATABASE
        HttpPost postCreateSuiteRun = new HttpPost(gVC.getManagerContextRootURL() + "graphene-visual-testing-webapp/rest/runs");
        postCreateSuiteRun.setHeader("Content-Type", "application/json");
        StringEntity suiteRunEntity = new StringEntity(
                "{\"timestamp\":\"" + timestamp.getTime() + "\",\"projectRevision\":\"ffff1111\","
                + "\"numberOfFailedFunctionalTests\":\"" + getNumberOfFailed() + "\","
                + "\"numberOfFailedComparisons\":\"" + getDiffNames().size() + "\","
                + "\"numberOfSuccessfullComparisons\":\"" + getSame() + "\","
                + "\"testSuite\":{\"name\":\"" + suiteName + "\"}}", ContentType.APPLICATION_JSON);
        postCreateSuiteRun.setEntity(suiteRunEntity);
        String testSuiteRunID = RestUtils.executePost(postCreateSuiteRun, httpclient,
                String.format("SuiteRun in database for %s created!", suiteName),
                String.format("Error while SuiteRun name in database for test suite: %s", suiteName));

        //UPLOADING DIFFS AND SAMPLES IF ANY
        Map<String, String> patternsNamesAndCorrespondingDiffs = getDiffNames();
        if (!patternsNamesAndCorrespondingDiffs.isEmpty()) {
            diffsUtils.get().setDiffCreated(true);
            uploadSamples(patternsNamesAndCorrespondingDiffs, timestampWithoutWhiteSpaces, testSuiteRunID);
            uploadDiffs(patternsNamesAndCorrespondingDiffs, timestampWithoutWhiteSpaces, testSuiteRunID);
        }
    }

    private void uploadSamples(Map<String, String> patternsNamesAndCorrespondingDiffs, String timestamp, String testSuiteRunID) {
        final File screenshotsDir = screenshooterConf.get().getRootDir();
        GrapheneVisualTestingConfiguration gVC = grapheneVisualTestingConf.get();
        final String suiteName = gVC.getTestSuiteName();
        CloseableHttpClient httpclient = RestUtils.getHTTPClient(gVC.getJcrContextRootURL(), gVC.getJcrUserName(), gVC.getJcrPassword());

        NodeList testNodes = getDOMFromSuiteXML().getElementsByTagName("test");
        for (Map.Entry<String, String> entry : patternsNamesAndCorrespondingDiffs.entrySet()) {
            for (int i = 0; i < testNodes.getLength(); i++) {
                if (testNodes.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(entry.getKey())) {
                    String patternSource = testNodes.item(i).getChildNodes().item(1).getAttributes()
                            .getNamedItem("source").getNodeValue();
                    //UPLOAD SAMPLE
                    File sampleToUpload = new File(screenshotsDir + File.separator + patternSource);
                    String url = grapheneVisualTestingConf.get().getJcrContextRootURL() + "/upload/" + suiteName + "/runs/"
                            + timestamp + "/samples/" + patternSource;
                    HttpPost postResultDescriptor = new HttpPost(url);
                    FileEntity sampleEntity = new FileEntity(sampleToUpload);
                    postResultDescriptor.setEntity(sampleEntity);
                    RestUtils.executePost(postResultDescriptor, httpclient,
                            String.format("Sample for %s uploaded!", suiteName),
                            String.format("Error while uploading sample for test suite: %s", suiteName));

                    //CREATE SAMPLE IN DATABASE
                    HttpPost postCreateSample = new HttpPost(gVC.getManagerContextRootURL() + "graphene-visual-testing-webapp/rest/samples");
                    postCreateSample.setHeader("Content-Type", "application/json");
                    StringEntity toDatabaseSample = new StringEntity(
                            "{\"name\":\"" + patternSource + "\",\"urlOfScreenshot\":\""
                            + url.replace("/upload/", "/binary/") + "/jcr%3acontent/jcr%3adata"
                            + "\",\"testSuiteRun\":{\"testSuiteRunID\":\"" + testSuiteRunID
                            + "\"}}", ContentType.APPLICATION_JSON);
                    postCreateSample.setEntity(toDatabaseSample);
                    String sampleID = RestUtils.executePost(postCreateSample, httpclient,
                            String.format("Sample in database for %s created!", suiteName),
                            String.format("Error while Sample in database for test suite: %s", suiteName));
                    sampleAndItsIDs.put(patternSource.replaceAll("/", "\\.")
                            .replaceAll("\\." + screenshooterConf.get().getScreenshotType().toLowerCase(), ""), Long.valueOf(sampleID));
                }
            }
        }
    }

    private void uploadDiffs(Map<String, String> patternsNamesAndCorrespondingDiffs, String timestampWithoutWhiteSpaces,
            String testSuiteRunID) {
        String suiteName = grapheneVisualTestingConf.get().getTestSuiteName();
        GrapheneVisualTestingConfiguration gVC = grapheneVisualTestingConf.get();
        CloseableHttpClient httpclient = RestUtils.getHTTPClient(gVC.getJcrContextRootURL(), gVC.getJcrUserName(), gVC.getJcrPassword());
        for (final Map.Entry<String, String> patternAndDiff : patternsNamesAndCorrespondingDiffs.entrySet()) {
            File diffsDir = new File(rusheyeConf.get().getWorkingDirectory()
                    + File.separator
                    + rusheyeConf.get().getDiffsDir());
            File[] diffsWithSearchedName = diffsDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains(patternAndDiff.getValue());
                }
            });
            if (diffsWithSearchedName.length > 1) {
                throw new RuntimeException("There are two or more diffs which names contains: " + patternAndDiff.getValue());
            } else if (diffsWithSearchedName.length == 0) {
                throw new RuntimeException("Diff with filename: " + patternAndDiff.getValue() + " was not found!");
            } else {
                //UPLOADING DIFF
                File diffToUpload = diffsWithSearchedName[0];
                String diffURL = gVC.getJcrContextRootURL() + "/upload/" + suiteName + "/runs/"
                        + timestampWithoutWhiteSpaces + "/diffs/" + diffToUpload.getName();
                HttpPost postResultDescriptor = new HttpPost(diffURL);
                FileEntity diffEntity = new FileEntity(diffToUpload);
                postResultDescriptor.setEntity(diffEntity);
                RestUtils.executePost(postResultDescriptor, httpclient,
                        String.format("Diff for %s uploaded!", suiteName),
                        String.format("Error while uploading diff for test suite: %s", suiteName));

                //CREATING DIFF IN DATABASE
                HttpPost postCreateDiff = new HttpPost(gVC.getManagerContextRootURL() + "graphene-visual-testing-webapp/rest/diffs");
                postCreateDiff.setHeader("Content-Type", "application/json");
                Long sampleID = sampleAndItsIDs.get(patternAndDiff.getKey());
                StringEntity toDatabaseDiff = new StringEntity(
                        "{\"name\":\"" + diffToUpload.getName() + "\",\"urlOfScreenshot\":\""
                        + diffURL.replace("/upload/", "/binary/") + "/jcr%3acontent/jcr%3adata"
                        + "\",\"sample\":{\"sampleID\":\"" + sampleID
                        + "\"},\"testSuiteRun\":{\"testSuiteRunID\":\"" + testSuiteRunID + "\"}}", ContentType.APPLICATION_JSON);
                postCreateDiff.setEntity(toDatabaseDiff);
                RestUtils.executePost(postCreateDiff, httpclient,
                        String.format("Diff in database for %s created!", suiteName),
                        String.format("Error while diff in database for test suite: %s", suiteName));
            }
        }
    }

    private Map<String, String> getDiffNames() {
        Map<String, String> result = new HashMap<>();
        try {
            NodeList testNodes = getDOMFromResultXMLazily().getElementsByTagName("test");
            for (int i = 0; i < testNodes.getLength(); i++) {
                Node patternTag = testNodes.item(i).getChildNodes().item(1);
                String resultAttribute = patternTag.getAttributes().
                        getNamedItem("result").getNodeValue();
                if (ResultConclusion.valueOf(resultAttribute).equals(ResultConclusion.DIFFER)) {
                    String outputAttrValue = patternTag.getAttributes().getNamedItem("output").getNodeValue();
                    String nameAttrValue = patternTag.getAttributes().getNamedItem("name").getNodeValue();
                    result.put(nameAttrValue, outputAttrValue);
                }
            }
        } catch (NullPointerException e) {

        }
        return result;
    }

    private int getSame() {
        int result = 0;
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            Document resultXml = getDOMFromResultXMLazily();
            result = Integer.parseInt(xPath.compile("count(/visual-suite-result/test/pattern[@result=" + "'" + ResultConclusion.SAME.toString() + "'])").evaluate(resultXml));
        } catch (NullPointerException e) {

        } catch (XPathExpressionException ex) {
            Logger.getLogger(JCRSamplesAndDiffsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    
    private int getNumberOfFailed() {
        int result = 0;
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();
            Document resultXml = getDOMFromResultXMLazily();
            result = Integer.parseInt(xPath.compile("count(/visual-suite-result/test/pattern[@result=" + "'" + ResultConclusion.ERROR.toString() + "'])").evaluate(resultXml));
        } catch (NullPointerException e) {

        } catch (XPathExpressionException ex) {
            Logger.getLogger(JCRSamplesAndDiffsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    

    private Document getDOMFromResultXMLazily() {
        String resultFilePath = rusheyeConf.get().getWorkingDirectory()
                + File.separator + rusheyeConf.get().getResultOutputFile();
        return getDOMFromXMLFile(new File(resultFilePath));
    }

    private Document getDOMFromSuiteXML() {
        String suiteFilePath = JCRDescriptorAndPatternsHandler.PATTERNS_DEFAULT_DIR
                + File.separator + rusheyeConf.get().getSuiteDescriptor().getName();
        return getDOMFromXMLFile(new File(suiteFilePath));
    }

    private Document getDOMFromXMLFile(File xmlFile) {
        Document result = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();
            result = db.parse(xmlFile);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(JCRSamplesAndDiffsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public Instance<RusheyeConfiguration> getRusheyeConf() {
        return rusheyeConf;
    }

    public void setRusheyeConf(Instance<RusheyeConfiguration> rusheyeConf) {
        this.rusheyeConf = rusheyeConf;
    }

    public Instance<GrapheneVisualTestingConfiguration> getGrapheneVisualTestingConf() {
        return grapheneVisualTestingConf;
    }

    public void setGrapheneVisualTestingConf(Instance<GrapheneVisualTestingConfiguration> grapheneVisualTestingConf) {
        this.grapheneVisualTestingConf = grapheneVisualTestingConf;
    }
}
