package com.maths22.ftclivescoring.services;

import com.maths22.ftclivescoring.data.Config;
import com.maths22.ftclivescoring.repositories.ConfigRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigService {
    public static String SCORING_SYSTEM_HOST = "SCORING_SYSTEM_HOST";
    public static String SCORING_SYSTEM_DIVISION = "SCORING_SYSTEM_DIVISION";
    public static String FIELD_COUNT = "FIELD_COUNT";
    public static String DEVICE_COUNT = "DEVICE_COUNT";
    //Goes in field-specific config
    public static String CURRENT_MATCH = "CURRENT_MATCH";

    private final ConfigRepository configRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    public static int GLOBAL_FIELD = 0;

    @Autowired
    public ConfigService(ConfigRepository configRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.configRepository = configRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Map<String, Object> getConfig(int field) {
        Config globalConfig = configRepository.findById(GLOBAL_FIELD).orElseGet(Config::new);
        Config fieldConfig = configRepository.findById(field).orElseGet(Config::new);
        Map<String, Object> ret = new HashMap<>();
        if(globalConfig.getValues() != null) {
            ret.putAll(globalConfig.getValues());
        }
        if(fieldConfig.getValues() != null) {
            ret.putAll(fieldConfig.getValues());
        }
        return ret;
    }

    public void setConfig(int field, Map<String, Object> params, String origin) {
        Config config = configRepository.findById(field).orElseGet(Config::new);
        config.setField(field);
        if(config.getValues() == null) {
            config.setValues(new HashMap<>());
        }
        config.getValues().putAll(params);
        configRepository.save(config);

        applicationEventPublisher.publishEvent(new ConfigUpdateEvent(this, field, params, origin));
    }

    public class ConfigUpdateEvent extends ApplicationEvent {
        @Getter
        private int field;

        @Getter
        private Map<String, Object> updatedProps;

        @Getter
        private String origin;

        public ConfigUpdateEvent(Object source, int field, Map<String, Object> updatedProps, String origin) {
            super(source);
            this.field = field;
            this.updatedProps = updatedProps;
            this.origin = origin;
        }
    }
}
