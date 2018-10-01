package com.maths22.ftclivescoring.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maths22.ftclivescoring.data.scoring.ScoringMatch;
import com.maths22.ftclivescoring.listeners.MatchUpdateListener;
import lombok.Getter;
import lombok.Setter;
import net.ser1.stomp.Client;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.maths22.ftclivescoring.services.ConfigService.*;

@Service
public class ScoringSystemService implements ApplicationListener<ConfigService.ConfigUpdateEvent> {
    private static Log log = LogFactory.getLog(ScoringSystemService.class);

    private Client client;
    private String host;
    private int port;

    @Getter
    private String eventName = "";

    private final ObjectMapper mapper;
    private final MatchImportService matchImportService;
    private final ConfigService configService;

    private LocalDateTime lastHeartbeat;
    private boolean disconnected = true;

    @Autowired
    public ScoringSystemService(ObjectMapper mapper, MatchImportService matchImportService, ConfigService configService) {
        this.mapper = mapper;
        this.matchImportService = matchImportService;
        this.configService = configService;
    }

    @PostConstruct
    public void init() {
        lastHeartbeat = LocalDateTime.now();
        java.util.Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(Duration.between(lastHeartbeat, LocalDateTime.now()).getSeconds() > 15) {
                    if(host != null && port != 0) {
                        disconnected = true;
                        log.info(String.format("Scoring system disconnected! Attempting to reconnect to %s:%d", host, port));
                        ScoringSystemService.this.connect();
                    }
                }
            }
        }, 15000, 15000);

        if(this.loadConfig()) {
            log.info(String.format("Connecting to scoring system %s:%d", host, port));
            this.connect();
        }
    }

    private boolean loadConfig() {
        this.host = (String) configService.getConfig(GLOBAL_FIELD).get(SCORING_SYSTEM_HOST);
        Integer division = (Integer) configService.getConfig(GLOBAL_FIELD).get(SCORING_SYSTEM_DIVISION);
        if(host == null || division == null) {
            log.warn("Unable to connect to scoring system: host or division not set");
            return false;
        }
        this.port = 43785 + division;
        return true;
    }

    private boolean connect() {
        if (client != null) {
            client.disconnect();
        }
        try {
            client = new Client(host, port, "user", "password");
            client.subscribe("/timer/status", (map, s) -> {
                //Todo broadcast
            });
            client.subscribe("/display/listmatches", (map, s) -> {
                try {
                    List<ScoringSendMatch> list = mapper.readValue(s, mapper.getTypeFactory().
                            constructCollectionType(List.class, ScoringSendMatch.class));
                    matchImportService.load(list);
                } catch (IOException e) {
                    log.warn("Unable to read match list: " + e.getMessage());
                }
            });
            client.subscribe("/live/divInfo", (map, s) -> {
                try {
                    DivisionInfo info = mapper.readValue(s, DivisionInfo.class);
                    if(info.getEventName().equals(info.getDivisionName())) {
                        eventName = info.getEventName();
                    } else {
                        eventName = info.getEventName() + " - " + info.getDivisionName() + " Division";
                    }
                } catch (IOException e) {
                    log.warn("Unable to read division information: " + e.getMessage());
                }
            });
            client.subscribe("/heartbeat", (a, b) -> heartbeatListener());
            client.send("/display/getmatches","");
            client.send("/live/getinfo","");

            disconnected = false;
        } catch (IOException | LoginException e1) {
            log.warn("Unable to connect to scoring system: " + e1.getMessage());
            return false;
        }
        log.info("Connected to scoring system!");
        return true;
    }

    public boolean submitMatch(ScoringMatch match) {
        if(disconnected) return false;
        try {
            client.send("/match/submit", mapper.writeValueAsString(match));
        } catch (JsonProcessingException e) {
            log.warn("Unable to convert match to string: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean sendLiveScore(String id, MatchUpdateListener.DisplayScore match) {
        if(disconnected) return false;
        try {
            client.send("/match/liveScore", mapper.writeValueAsString(match));
        } catch (JsonProcessingException e) {
            log.warn("Unable to convert match to string: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void heartbeatListener() {
        lastHeartbeat = LocalDateTime.now();
    }

    @Override
    public void onApplicationEvent(ConfigService.ConfigUpdateEvent configUpdateEvent) {
        if(configUpdateEvent.getField() == GLOBAL_FIELD) {
            if(this.loadConfig()) {
                log.info(String.format("Config changed! Connecting to %s:%d", host, port));
                this.connect();
            }
        }
    }

    public static class ScoringSendMatch {
        @Getter
        @Setter
        private String score;

        @Getter
        @Setter
        private String fileString;

        @Getter
        @Setter
        private String id;

        @Getter
        @Setter
        private List<ScoringSendTeam> redAlliance;

        @Getter
        @Setter
        private List<ScoringSendTeam> blueAlliance;
    }

    public static class ScoringSendTeam {
        @Getter
        @Setter
        private int number;

        @Getter
        @Setter
        private String name;

        @Getter
        @Setter
        private String organization;

        @Getter
        @Setter
        private String city;

        @Getter
        @Setter
        private String state;

        @Getter
        @Setter
        private String country;
    }

    public static class DivisionInfo {
        @Getter
        @Setter
        private String eventName;

        @Getter
        @Setter
        private String divisionName;
    }
}
