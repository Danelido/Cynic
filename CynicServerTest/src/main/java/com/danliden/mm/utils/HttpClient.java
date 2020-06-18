package com.danliden.mm.utils;

import com.danliden.mm.execution.Main;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpClient {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public JSONObject get(String requestPath, int retries, long interval) throws Exception {
        String fullPath = "http://" + Main.SERVER_ADDRESS + ":" + Main.PORT + "/" + requestPath;
        logger.info("Full request path: " + fullPath);

        for (int i = 0; i < retries; i++) {
            HttpGet request = new HttpGet(fullPath);

            try {
                CloseableHttpResponse response = httpClient.execute(request);

                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    // return it as a String
                    String payload = EntityUtils.toString(entity);

                    return new JSONObject(payload);
                }

            } catch (Exception e) {
                logger.error("Failed to get http request from server");
                logger.warn(e.getMessage());
            }
            logger.info("Could not fetch http request, retrying... ");
            Thread.sleep(interval);
        }
        logger.error("Failed to get http request from server");
        return null;
    }

    public void close() throws IOException {
        httpClient.close();
    }

}
