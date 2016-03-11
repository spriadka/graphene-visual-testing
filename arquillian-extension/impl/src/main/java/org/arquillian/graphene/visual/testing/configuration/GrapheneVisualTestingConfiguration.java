package org.arquillian.graphene.visual.testing.configuration;

import java.io.File;

import org.arquillian.extension.recorder.Configuration;
import org.arquillian.extension.recorder.RecorderConfigurationException;
import org.arquillian.extension.recorder.screenshooter.ScreenshooterConfiguration;

public class GrapheneVisualTestingConfiguration extends Configuration<ScreenshooterConfiguration> {

    private String lastValidScreenshots = "target";

    private String result = "target/result.xml";

    private String diffs = "target/diffs";

    private String firstRun = "false";
    
    private String testSuiteName = "pseudo-random";
    
    private String jcrContextRootURL = "http://localhost:8080/modeshape-rest/graphene-visual-testing/default";
    
    private String managerContextRootURL = "http://localhost:8080/";
    
    private String jcrUserName = "graphene-visual-testing";
    
    private String jcrPassword = "graphene-visual-testing";
    
    private String failBuild = "false";
    
    public GrapheneVisualTestingConfiguration() {
    }

    public File getLastValidScreenshots() {
        return new File(getProperty("lastValidScreenshots", lastValidScreenshots));
    }

    public File getResult() {
        return new File(getProperty("result", result));
    }
    
    public String getTestSuiteName() {
        return getProperty("testSuiteName", testSuiteName);
    }
    
    public String getJcrContextRootURL() {
        return getProperty("jcrContextRootURL", jcrContextRootURL);
    }
    
    public String getManagerContextRootURL() {
        return getProperty("managerContextRootURL", managerContextRootURL);
    }
    
    public String getJcrUserName() {
        return getProperty("jcrUserName", jcrUserName);
    }
    
    public String getJcrPassword() {
        return getProperty("jcrPassword", jcrPassword);
    }

    public File getDiffs() {
        return new File(getProperty("diffs", diffs));
    }

    public boolean isFirstRun() {
        return Boolean.parseBoolean(getProperty("firstRun", firstRun));
    }
    
    public boolean isFailBuild() {
        return Boolean.parseBoolean(getProperty("failBuild", failBuild));
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-40s %s\n", "lastValidScreenshots", getLastValidScreenshots()));
        sb.append(String.format("%-40s %s\n", "result", getResult()));
        sb.append(String.format("%-40s %s\n", "diffs", getDiffs()));
        sb.append(String.format("%-40s %s\n", "isFirstRun", isFirstRun()));
        sb.append(String.format("%-40s %s\n", "testSuiteName", getTestSuiteName()));
        return sb.toString();
    }

    @Override
    public void validate() throws RecorderConfigurationException {
        if (!isFirstRun()) {
            checkDirForExistenceAndPermissions(getDiffs(), "diffs");
        }
    }

    private void checkDirForExistenceAndPermissions(File dir, String dirPurpose) {
        try {
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new GrapheneVisualTestingConfigurationException("Unable to create directory for " + dirPurpose
                            + ", with path: " + dir.getAbsolutePath());
                }
            } else {
                if (!dir.isDirectory()) {
                    throw new GrapheneVisualTestingConfigurationException(dirPurpose
                            + " property you specified is not a directory - " + dir.getAbsolutePath());
                }
                if (!dir.canWrite()) {
                    throw new GrapheneVisualTestingConfigurationException("You can not write to '" + dir.getAbsolutePath()
                            + "'.");
                }
            }
        } catch (SecurityException ex) {
            throw new GrapheneVisualTestingConfigurationException("You are not permitted to operate on specified resource: "
                    + dir.getAbsolutePath() + "'.");
        }
    }

    /**
     * @param jcrUserName the jcrUserName to set
     */
    public void setJcrUserName(String jcrUserName) {
        this.jcrUserName = jcrUserName;
    }

    /**
     * @param jcrPassword the jcrPassword to set
     */
    public void setJcrPassword(String jcrPassword) {
        this.jcrPassword = jcrPassword;
    }
}
