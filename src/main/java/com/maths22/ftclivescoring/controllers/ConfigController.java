package com.maths22.ftclivescoring.controllers;

import com.maths22.ftclivescoring.repositories.ConfigRepository;
import com.maths22.ftclivescoring.repositories.MatchRepository;
import com.maths22.ftclivescoring.services.ConfigService;
import com.maths22.ftclivescoring.services.ScoringPrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ConfigController {
    private final ConfigService configService;
    private final ScoringPrintService printService;
    private final ConfigRepository configRepository;
    private final MatchRepository matchRepository;

    @Autowired
    public ConfigController(ConfigService configService, ScoringPrintService printService, ConfigRepository configRepository, MatchRepository matchRepository) {
        this.configService = configService;
        this.printService = printService;
        this.configRepository = configRepository;
        this.matchRepository = matchRepository;
    }

    @GetMapping("/api/config/{field}")
    public Map<String, Object> getConfig(@PathVariable int field) {
        return configService.getConfig(field);
    }

    @PostMapping("/api/config/{field}")
    public boolean saveConfig(@PathVariable int field, @RequestBody Map<String, Object> request, @RequestHeader("Client-Id") String clientId) {
        configService.setConfig(field, request, clientId);
        return true;
    }

    @PostMapping("/api/config/printer")
    public boolean reconfigurePrinter() {
        return printService.reconfigurePrintService();
    }

    @PostMapping("/api/config/reset")
    public boolean reset() {
        configRepository.deleteAll();
        matchRepository.deleteAll();
        return true;
    }

}
