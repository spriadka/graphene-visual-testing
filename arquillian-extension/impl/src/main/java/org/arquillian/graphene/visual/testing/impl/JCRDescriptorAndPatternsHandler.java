package org.arquillian.graphene.visual.testing.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.arquillian.extension.recorder.screenshooter.ScreenshooterConfiguration;
import org.arquillian.graphene.visual.testing.api.DescriptorAndPatternsHandler;
import org.arquillian.graphene.visual.testing.configuration.GrapheneVisualTestingConfiguration;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.rusheye.arquillian.configuration.RusheyeConfiguration;
import org.jboss.rusheye.arquillian.event.FailedTestsCollection;
import org.jboss.rusheye.arquillian.event.StartCrawlMissingTestsEvent;
import org.jboss.rusheye.arquillian.event.VisuallyUnstableTestsCollection;
import org.json.JSONObject;

/**
 *
 * @author jhuska
 */
public class JCRDescriptorAndPatternsHandler implements DescriptorAndPatternsHandler {

    public static final String PATTERNS_DEFAULT_DIR = "target/patterns";

    private static final Logger LOGGER = Logger.getLogger(JCRDescriptorAndPatternsHandler.class.getName());

    @Inject
    private Instance<ScreenshooterConfiguration> screenshooterConf;

    @Inject
    private Instance<RusheyeConfiguration> rusheyeConf;

    @Inject
    private Instance<GrapheneVisualTestingConfiguration> grapheneVisualTestingConf;

    @Inject
    private Instance<FailedTestsCollection> failedTestCollection;

    @Inject
    private Instance<VisuallyUnstableTestsCollection> visuallyUnstableTestCollection;

    @Override
    public boolean saveDescriptorAndPatterns() {
        GrapheneVisualTestingConfiguration gVC = grapheneVisualTestingConf.get();
        CloseableHttpClient httpclient = RestUtils.getHTTPClient(gVC.getJcrContextRootURL(), gVC.getJcrUserName(), gVC.getJcrPassword());
        File patternsRootDir = screenshooterConf.get().getRootDir();
        File suiteDescriptor = new File(rusheyeConf.get().getWorkingDirectory().getAbsolutePath()
                + File.separator
                + rusheyeConf.get().getSuiteDescriptor());
        String suiteName = grapheneVisualTestingConf.get().getTestSuiteName();
        Date now = new Date();
        String timestamp = "" + now.getTime();

        //UPLOADING TEST SUITE DESCRIPTOR
        HttpPost postSuiteDescriptor = new HttpPost(gVC.getJcrContextRootURL() + "/upload/" + suiteName + "/suite.xml");
        FileEntity descriptorEntity = new FileEntity(suiteDescriptor, ContentType.APPLICATION_XML);
        postSuiteDescriptor.setEntity(descriptorEntity);
        RestUtils.executePost(postSuiteDescriptor, httpclient,
                String.format("Suite descriptor for %s uploaded!", suiteName),
                String.format("Error while uploading test suite descriptor for test suite: %s", suiteName));

        //CREATE SUITE NAME IN DATABASE
        HttpPost postCreateSuiteName = new HttpPost(gVC.getManagerContextRootURL() + "graphene-visual-testing-webapp/rest/suites");
        postCreateSuiteName.setHeader("Content-Type", "application/json");
        StringEntity suiteNameEntity = new StringEntity(
                "{\"name\":\"" + suiteName + "\",\"numberOfFunctionalTests\":\"" + getNumberOfTests()
                + "\",\"numberOfVisualComparisons\":\"" + getNumberOfComparisons() + "\"}", ContentType.APPLICATION_JSON);
        postCreateSuiteName.setEntity(suiteNameEntity);
        RestUtils.executePost(postCreateSuiteName, httpclient,
                String.format("Suite name in database for %s created!", suiteName),
                String.format("Error while creating suite name in database for test suite: %s", suiteName));

        //UPLOADING PATTERNS
        return crawlAndUploadPatterns(patternsRootDir, patternsRootDir.getName(), httpclient,timestamp);
    }
    
    
    @Override
    public boolean saveDescriptorAndMissingPatterns(StartCrawlMissingTestsEvent event){
        System.out.println("GONNA UPLOAD DESCRIPTOR AND MISSING PATTERNS!!!!!!!!!!!!!!!!!");
        Date now = new Date();
        String timestamp = "" + now.getTime();
        GrapheneVisualTestingConfiguration gVC = grapheneVisualTestingConf.get();
        CloseableHttpClient httpclient = RestUtils.getHTTPClient(gVC.getJcrContextRootURL(), gVC.getJcrUserName(), gVC.getJcrPassword());
        File patternsRootDir = screenshooterConf.get().getRootDir();
        File suiteDescriptor = new File(rusheyeConf.get().getWorkingDirectory().getAbsolutePath()
                + File.separator
                + rusheyeConf.get().getSuiteDescriptor());
        String suiteName = grapheneVisualTestingConf.get().getTestSuiteName();

        //UPLOADING TEST SUITE DESCRIPTOR
        HttpPost postSuiteDescriptor = new HttpPost(gVC.getJcrContextRootURL() + "/upload/" + suiteName + "/suite.xml");
        FileEntity descriptorEntity = new FileEntity(suiteDescriptor, ContentType.APPLICATION_XML);
        postSuiteDescriptor.setEntity(descriptorEntity);
        RestUtils.executePost(postSuiteDescriptor, httpclient,
                String.format("Suite descriptor for %s uploaded!", suiteName),
                String.format("Error while uploading test suite descriptor for test suite: %s", suiteName));

        //CREATE SUITE NAME IN DATABASE
        HttpPost postCreateSuiteName = new HttpPost(gVC.getManagerContextRootURL() + "graphene-visual-testing-webapp/rest/suites");
        postCreateSuiteName.setHeader("Content-Type", "application/json");
        StringEntity suiteNameEntity = new StringEntity(
                "{\"name\":\"" + suiteName + "\",\"numberOfFunctionalTests\":\"" + getNumberOfTests()
                + "\",\"numberOfVisualComparisons\":\"" + getNumberOfComparisons() + "\"}", ContentType.APPLICATION_JSON);
        postCreateSuiteName.setEntity(suiteNameEntity);
        RestUtils.executePost(postCreateSuiteName, httpclient,
                String.format("Suite name in database for %s created!", suiteName),
                String.format("Error while creating suite name in database for test suite: %s", suiteName));

        //UPLOADING PATTERNS
        return crawlAndUploadMissingPatterns(patternsRootDir,patternsRootDir.getName(),httpclient,event.getMissingTests(),timestamp);
        
    }

    @Override
    public String retrieveDescriptorAndPatterns() {

        try {
            GrapheneVisualTestingConfiguration gVC = grapheneVisualTestingConf.get();
            CloseableHttpClient httpClient = RestUtils.getHTTPClient(gVC.getJcrContextRootURL(), gVC.getJcrUserName(), gVC.getJcrPassword());
            String suiteName = grapheneVisualTestingConf.get().getTestSuiteName();

            HttpGet getDescriptor = new HttpGet(gVC.getJcrContextRootURL() + "/binary/" + suiteName + "/suite.xml/jcr%3acontent/jcr%3adata");
            createDir(PATTERNS_DEFAULT_DIR);
            RestUtils.executeGetAndSaveToFile(getDescriptor, httpClient, PATTERNS_DEFAULT_DIR + "/suite.xml",
                    String.format("Suite descriptor for %s was retrieved.", suiteName),
                    String.format("ERROR occurred while retrieving suite descriptor for %s", suiteName));

            HttpGet getAllChildren = new HttpGet(gVC.getJcrContextRootURL() + "/items/" + suiteName + "?depth=-1");
            getAllChildren.addHeader("Accept", "application/json");
            JSONObject allSuiteChildren = new JSONObject(RestUtils.executeGet(getAllChildren, httpClient, "All children retrieved",
                    "Error while retrieving all children"));
            JSONObject testClasses = allSuiteChildren.getJSONObject("children").getJSONObject("patterns").getJSONObject("children");

            findAndDownloadScreenshot(testClasses, suiteName, httpClient);
        } catch (Exception e) {
            LOGGER.info("SUITE NOT FOUND, GOING TO CREATE NEW");

        } finally {
            return PATTERNS_DEFAULT_DIR;
        }

    }

    private void findAndDownloadScreenshot(JSONObject testClasses, String suiteName, CloseableHttpClient httpClient) {
        StringBuilder builder = new StringBuilder();
        for (Object testClass : testClasses.keySet()) {
            builder.append(testClass.toString());
            JSONObject tests = testClasses.getJSONObject(testClass.toString()).getJSONObject("children");
            for (Object test : tests.keySet()) {
                builder = appendWrappedStringWithSeparator(builder, test.toString());
                File testDir = new File(PATTERNS_DEFAULT_DIR + File.separator + "screenshots"
                        + File.separator + builder.toString());
                testDir.mkdirs();
                JSONObject screenshots = tests.getJSONObject(test.toString()).getJSONObject("children");
                for (Object screenshot : screenshots.keySet()) {
                    builder.append(screenshot.toString());
                    String screenURL = suiteName + "/patterns/"
                            + testClass.toString() + "/" + test.toString() + "/" + screenshot.toString();
                    HttpGet getScreenshot = new HttpGet(grapheneVisualTestingConf.get().getJcrContextRootURL() + "/binary/" + screenURL
                            + "/jcr%3acontent/jcr%3adata");
                    File fileToSave = new File(testDir.getAbsolutePath() + File.separator + screenshot.toString());
                    RestUtils.executeGetAndSaveToFile(getScreenshot, httpClient, fileToSave.getAbsolutePath(), "Screenshot retrieved from URL: " + screenURL,
                            "Error while retrieving screenshot: " + screenURL);
                    builder = new StringBuilder();
                    builder.append(testClass.toString());
                    builder = appendWrappedStringWithSeparator(builder, test.toString());
                }
                builder = new StringBuilder();
                builder.append(testClass.toString());
            }
            builder = new StringBuilder();
        }
    }

    private int getNumberOfTests() {
        int result = 0;
        for (File testClassDir : screenshooterConf.get().getRootDir().listFiles()) {
            result += testClassDir.listFiles().length;
        }
        return result;
    }

    private int getNumberOfComparisons() {
        int result = 0;
        for (File testClassDir : screenshooterConf.get().getRootDir().listFiles()) {
            result += getNumberOfScreenshotsRecursively(testClassDir);
        }
        return result;
    }

    private int getNumberOfScreenshotsRecursively(File rootToStartFrom) {
        int result = 0;
        for (File i : rootToStartFrom.listFiles()) {
            if (i.isDirectory()) {
                result += getNumberOfScreenshotsRecursively(i);
            } else {
                result += rootToStartFrom.listFiles().length;
                break;
            }
        }
        return result;
    }

    private StringBuilder appendWrappedStringWithSeparator(StringBuilder builder, String toBeWrapped) {
        builder.append(File.separator);
        builder.append(toBeWrapped);
        builder.append(File.separator);
        return builder;
    }

    private void createDir(String path) {
        File theDir = new File(path);

        if (!theDir.exists()) {
            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                throw new RuntimeException(se);
            }
        }
    }

    private boolean crawlAndUploadPatterns(File patternsDir, String rootOfPatterns, CloseableHttpClient httpClient, String timestamp) {
        GrapheneVisualTestingConfiguration gVC = grapheneVisualTestingConf.get();
        boolean result = true;
        for (File dirOrFile : patternsDir.listFiles()) {
            if (dirOrFile.isDirectory()) {
                result = crawlAndUploadPatterns(dirOrFile, rootOfPatterns, httpClient,timestamp);
            } else {
                
                String suiteName = grapheneVisualTestingConf.get().getTestSuiteName();
                String absolutePath = dirOrFile.getAbsolutePath();
                String patternRelativePath = absolutePath.split(rootOfPatterns + File.separator)[1];
                String urlOfScreenshot = grapheneVisualTestingConf.get().getJcrContextRootURL() + "/upload/"
                        + suiteName + "/patterns/"
                        + patternRelativePath;
                HttpPost postPattern = new HttpPost(urlOfScreenshot);
                FileEntity screenshot = new FileEntity(dirOrFile);
                postPattern.setEntity(screenshot);
                result = !RestUtils.executePost(postPattern, httpClient,
                        String.format("Pattern: %s uploaded to test suite: %s", dirOrFile.getName(), suiteName),
                        String.format("ERROR: pattern %s was not uploaded to test suite %s", dirOrFile.getName(), suiteName)).isEmpty();

                //UPLOAD INFO ABOUT PATTERN TO DATABASE
                HttpPost postCreatePattern = new HttpPost(gVC.getManagerContextRootURL()
                        + "graphene-visual-testing-webapp/rest/patterns");
                postCreatePattern.setHeader("Content-Type", "application/json");
                String urlOfScreenshotContent = urlOfScreenshot.replace("/upload/", "/binary/") + "/jcr%3acontent/jcr%3adata";
                StringEntity patternEntity
                        = new StringEntity("{\"name\":\"" + patternRelativePath + "\",\"urlOfScreenshot\":\""
                                + urlOfScreenshotContent + "\",\"testSuite\":{\"name\":\"" + suiteName + "\"},\"lastModificationDate\":\"" + timestamp + "\"}", ContentType.APPLICATION_JSON);
                postCreatePattern.setEntity(patternEntity);
                RestUtils.executePost(postCreatePattern, httpClient,
                        String.format("Pattern in database for %s created!", suiteName),
                        String.format("Error while creating pattern in database for test suite: %s", suiteName));

            }
            //if partial result is false, finish early with false status
            if (!result) {
                return result;
            }
        }
        return result;
    }

    

    private boolean crawlAndUploadMissingPatterns(File patternsDir, String rootOfPatterns, CloseableHttpClient httpclient, List<String> missingTests,String timestamp) {
        GrapheneVisualTestingConfiguration gVC = grapheneVisualTestingConf.get();
        boolean result = true;
        for (File dirOrFile : patternsDir.listFiles()) {
            if (dirOrFile.isDirectory()) {
                result = crawlAndUploadMissingPatterns(dirOrFile, rootOfPatterns, httpclient, missingTests,timestamp);
            } else {
                String suiteName = grapheneVisualTestingConf.get().getTestSuiteName();
                String absolutePath = dirOrFile.getAbsolutePath();
                String patternRelativePath = absolutePath.split(rootOfPatterns + File.separator)[1];
                String patternNameWithoutSlash = patternRelativePath.replace("/", ".");
                String patternName = patternNameWithoutSlash.substring(0, patternNameWithoutSlash.lastIndexOf("."));
                System.out.println("MISSING PATTERN" + patternName);
                if (missingTests.contains(patternName)) {
                    String urlOfScreenshot = grapheneVisualTestingConf.get().getJcrContextRootURL() + "/upload/"
                            + suiteName + "/patterns/"
                            + patternRelativePath;
                    HttpPost postPattern = new HttpPost(urlOfScreenshot);
                    FileEntity screenshot = new FileEntity(dirOrFile);
                    postPattern.setEntity(screenshot);
                    result = !RestUtils.executePost(postPattern, httpclient,
                            String.format("Pattern: %s uploaded to test suite: %s", dirOrFile.getName(), suiteName),
                            String.format("ERROR: pattern %s was not uploaded to test suite %s", dirOrFile.getName(), suiteName)).isEmpty();

                    //UPLOAD INFO ABOUT PATTERN TO DATABASE
                    HttpPost postCreatePattern = new HttpPost(gVC.getManagerContextRootURL()
                            + "graphene-visual-testing-webapp/rest/patterns");
                    postCreatePattern.setHeader("Content-Type", "application/json");
                    String urlOfScreenshotContent = urlOfScreenshot.replace("/upload/", "/binary/") + "/jcr%3acontent/jcr%3adata";
                    StringEntity patternEntity
                            = new StringEntity("{\"name\":\"" + patternRelativePath + "\",\"urlOfScreenshot\":\""
                                    + urlOfScreenshotContent + "\",\"testSuite\":{\"name\":\"" + suiteName + "\"},\"lastModificationDate\":\"" + timestamp + "\"}", ContentType.APPLICATION_JSON);
                    postCreatePattern.setEntity(patternEntity);
                    RestUtils.executePost(postCreatePattern, httpclient,
                            String.format("Pattern in database for %s created!", suiteName),
                            String.format("Error while creating pattern in database for test suite: %s", suiteName));

                }
                //if partial result is false, finish early with false status
                if (!result) {
                    return result;
                }

            }

        }
        return result;
    }

}
