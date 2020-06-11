package com.danliden.mm.game.server;

import com.danliden.mm.rest.HTTPResponse;
import com.danliden.mm.rest.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {JmxAutoConfiguration.class})
@RestController
public class GameServer {
    private ServerManager serverManager;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GameServer() {
        serverManager = new ServerManager();
    }

    public String findAvailableGameSession() throws Exception {
        return serverManager.findAvailableSession().toString();
    }

    @GetMapping(path= Paths.FindServerSession)
    public String FindServerSession(){
        try {
            return findAvailableGameSession();
        } catch (Exception e) {
            logger.debug(e.getMessage());
            return new HTTPResponse()
                    .setStatusCode(HttpStatus.NOT_FOUND.value())
                    .toString();
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(GameServer.class, args);
    }

}
