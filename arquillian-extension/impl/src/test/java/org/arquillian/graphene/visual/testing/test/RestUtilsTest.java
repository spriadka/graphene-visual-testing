/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arquillian.graphene.visual.testing.test;

import java.util.logging.Logger;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.arquillian.graphene.visual.testing.configuration.GrapheneVisualTestingConfiguration;
import org.arquillian.graphene.visual.testing.impl.RestUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author spriadka
 */
public class RestUtilsTest {

    private final Logger LOGGER = Logger.getLogger(RestUtilsTest.class.getName());
    
    private GrapheneVisualTestingConfiguration conf = new GrapheneVisualTestingConfiguration();
    
    public RestUtilsTest(){
        conf.setJcrUserName("redhat");
        conf.setJcrPassword("redhat2");
    }

    @Test
    public void testResponse() {
        String token = "test-word";
        CloseableHttpClient httpClient = RestUtils.getHTTPClient(conf.getJcrContextRootURL(), conf.getJcrUserName(), conf.getJcrPassword());
        HttpPost postCreateWords = new HttpPost(conf.getManagerContextRootURL() + "graphene-visual-testing-webapp/rest/words");
        StringEntity wordEnity = new StringEntity("{\"value\": \"" + token + "\"}", ContentType.APPLICATION_JSON);
        postCreateWords.setHeader("Content-Type", "application/json");
        postCreateWords.setEntity(wordEnity);
        String response = RestUtils.executePost(postCreateWords, httpClient, token + " created", "FAILED TO CREATE: " + token);
    }
    
    @Test
    public void testJSONParser(){
        String valJSON = "{\"nodeId\":1,\"parent\":{\"nodeId\":2}}";
        JSONObject parser = new JSONObject(valJSON);
        LOGGER.info(Long.toString(parser.getLong("nodeId")));
        LOGGER.info(Long.toString(parser.getJSONObject("parent").getLong("nodeId")));
        Assert.assertEquals((long) 1, parser.getLong("nodeId"));
    }
    

}
