package org.arquillian.graphene.visual.testing.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author jhuska
 */
public class RestUtils {

    private static final Logger LOGGER = Logger.getLogger(RestUtils.class.getName());

    public static String executeGet(HttpGet httpGet, CloseableHttpClient httpclient, String successLog, String errorLog) {
        CloseableHttpResponse response = null;
        BufferedReader bfr = null;
        StringBuilder builder = new StringBuilder();
        try {
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 200) {
                if (errorLog != null) {
                    LOGGER.severe(errorLog);
                }
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                bfr = new BufferedReader(new InputStreamReader(entity.getContent()));

                String line = bfr.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bfr.readLine();
                }
                EntityUtils.consume(entity);
                if (successLog != null) {
                    LOGGER.info(successLog);
                }
            }
        } catch (IOException ex) {
            if (errorLog != null) {
                LOGGER.severe(String.format("%s %s", errorLog, ex.getMessage()));
            }
        } finally {
            if (bfr != null) {
                try {
                    bfr.close();
                } catch (IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }
        }
        return builder.toString();
    }

    public static void executeGetAndSaveToFile(HttpGet httpGet, CloseableHttpClient httpclient, String pathToSaveResponse, String successLog, String errorLog) {
        CloseableHttpResponse response = null;
        OutputStream os = null;
        InputStream is = null;
        try {
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 200) {
                if (errorLog != null) {
                    LOGGER.severe(errorLog + "Status line: "
                            + response.getStatusLine().getReasonPhrase() + " "
                            + response.getStatusLine().getStatusCode());
                }
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                is = entity.getContent();
                os = new FileOutputStream(new File(pathToSaveResponse));
                LOGGER.info(pathToSaveResponse);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = is.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }
                EntityUtils.consume(entity);
                if (successLog != null) {
                    LOGGER.info(successLog);
                }
            }
        } catch (IOException ex) {
            if (errorLog != null) {
                LOGGER.severe(String.format("%s %s", errorLog, ex.getMessage()));
                ex.printStackTrace();
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }
        }
    }
    
    public static String executePut(HttpPut put, CloseableHttpClient httpclient, String successLog, String errorLog){
        CloseableHttpResponse response = null;
        BufferedReader bfr = null;
        StringBuilder builder = new StringBuilder();
        try {
            response = httpclient.execute(put);
            if (!isOKOrCreated(response)) {
                StatusLine status = response.getStatusLine();
                LOGGER.info(String.format("%s %s %s", errorLog, status.getReasonPhrase(), status.getStatusCode()));
                return builder.toString();
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                bfr = new BufferedReader(new InputStreamReader(entity.getContent()));

                String line = bfr.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bfr.readLine();
                }
                EntityUtils.consume(entity);
                if (successLog != null) {
                    LOGGER.info(successLog);
                }
            }
            LOGGER.info(successLog);
            return builder.toString();
        } catch (IOException ex) {
            LOGGER.severe(String.format(errorLog + " %s", ex.getMessage()));
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
        return builder.toString();
    }

    public static String executePost(HttpPost post, CloseableHttpClient httpclient, String successLog, String errorLog) {
        CloseableHttpResponse response = null;
        BufferedReader bfr = null;
        StringBuilder builder = new StringBuilder();
        try {
            response = httpclient.execute(post);
            if (!isOKOrCreated(response)) {
                StatusLine status = response.getStatusLine();
                LOGGER.info(String.format("%s %s %s", errorLog, status.getReasonPhrase(), status.getStatusCode()));
                return builder.toString();
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                bfr = new BufferedReader(new InputStreamReader(entity.getContent()));

                String line = bfr.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bfr.readLine();
                }
                EntityUtils.consume(entity);
                if (successLog != null) {
                    LOGGER.info(successLog);
                }
            }
            LOGGER.info(successLog);
            return builder.toString();
        } catch (IOException ex) {
            LOGGER.severe(String.format(errorLog + " %s", ex.getMessage()));
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
        return builder.toString();
    }

    public static boolean isOKOrCreated(CloseableHttpResponse response) {
        return response.getStatusLine().getStatusCode() == 200
                || response.getStatusLine().getStatusCode() == 201;
    }

    public static CloseableHttpClient getHTTPClient(String jcrContextRootUrl, String jcrUserName, String jcrPassword) {
        URL jcrUrl = null;
        try {
            jcrUrl = new URL(jcrContextRootUrl);
        } catch (MalformedURLException ex) {
            //OK validated on another level
        }
        HttpHost target = new HttpHost(jcrUrl.getHost(), jcrUrl.getPort(), jcrUrl.getProtocol());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials(jcrUserName, jcrPassword));
        return HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();
    }
    
}
