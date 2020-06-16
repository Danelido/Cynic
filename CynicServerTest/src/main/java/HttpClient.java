import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpClient {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public String get(String uri, int retries, long interval) throws Exception {
        for(int i = 0; i < retries; i++) {
            HttpGet request = new HttpGet(uri);

            // add request headers
            try (CloseableHttpResponse response = httpClient.execute(request)) {

                // Get HttpResponse Status
                logger.info("Status line: ", response.getStatusLine().toString());
                HttpEntity entity = response.getEntity();

                if(entity != null) {
                    // return it as a String
                    String payload =  EntityUtils.toString(entity);
                    logger.info("Receive payload: ", payload);
                    return payload;
                }

            }
            logger.info("Could not fetch http request, retrying... ");
            Thread.sleep(interval);
        }
        logger.error("Failed to get http request from server");
        return "";
    }

    public void close() throws IOException {
        httpClient.close();
    }

}
