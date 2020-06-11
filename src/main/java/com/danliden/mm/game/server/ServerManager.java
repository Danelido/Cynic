package com.danliden.mm.game.server;

import com.danliden.mm.rest.HTTPResponse;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerManager {
    private static final int START_PORT = 32000;
    private static final int MAX_THREADS = 5;
    private ExecutorService executorService;
    private ArrayList<ServerInstance> instances;
    private List<Integer> ports;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServerManager(){
        logger.info("Creating thread pool");
        executorService = Executors.newFixedThreadPool(MAX_THREADS);
        ports = new ArrayList<Integer>();

        logger.info("Creating ports");
        for(int i = START_PORT; i < START_PORT + MAX_THREADS; i++){
            ports.add(i);
        }

        instances = new ArrayList<ServerInstance>();
    }

    public HTTPResponse findAvailableSession() throws Exception {
        // Search the already created servers to find an available session
        HTTPResponse response = searchForAvailableSessions();
        if (response != null){
            return response;
        }

        // Try to create a new server instance and return a new instance
        response = tryCreateNewServerInstance();
        if (response != null){
            return response;
        }

        logger.info("Could not find any available sessions");
        return new HTTPResponse().setStatusCode(HttpStatus.NOT_FOUND.value());
    }

    @Nullable
    private HTTPResponse tryCreateNewServerInstance() throws Exception {
        if(instances.size() < MAX_THREADS){
            ServerInstance instance = createAndRunNewInstance();
            HTTPResponse response = instance.getAvailableGameSession();
            if(response.StatusCode() == HttpStatus.OK.value()){
                logger.info("Found no available sessions, created a new session");
                return response;
            }
        }
        return null;
    }

    @Nullable
    private HTTPResponse searchForAvailableSessions() {
        logger.info("Finding available session");
        for(int i = 0; i < instances.size(); i++){
            ServerInstance instance = instances.get(i);
            HTTPResponse response = instance.getAvailableGameSession();
            if(response.StatusCode() == HttpStatus.OK.value()){
                return response;
            }
        }
        return null;
    }

    private ServerInstance createAndRunNewInstance() throws Exception {
        ServerInstance inst = new ServerInstance();
        executorService.execute(inst);
        instances.add(inst);
        return inst;
    }


}
