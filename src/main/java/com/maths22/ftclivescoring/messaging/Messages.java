package com.maths22.ftclivescoring.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messages {
    @AllArgsConstructor
    public static class ConfigUpdate {
        @Getter
        @Setter
        private String origin;

        @Getter
        @Setter
        private int field;

        @Setter
        @Getter
        private Map<String, Object> updatedProps;

    }

    @AllArgsConstructor
    public static class MatchList {
        @Setter
        @Getter
        private List<String> list;

    }

    public static class MatchUpdate {
        @Getter
        @Setter
        private String origin;

        @Getter
        @Setter
        private String match;

        public MatchUpdate(String origin, String match) {
            this.origin = origin;
            this.match = match;
            globalChanges = new HashMap<>();
            redScoreChanges = new HashMap<>();
            blueScoreChanges = new HashMap<>();
        }

        @Getter
        private Map<String, Object> globalChanges;

        @Getter
        private Map<String, Object> redScoreChanges;

        @Getter
        private Map<String, Object> blueScoreChanges;

    }
}
