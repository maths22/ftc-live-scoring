package com.maths22.ftclivescoring.data.scoring;

import lombok.Getter;
import lombok.Setter;

public class ScoringMatch {
    @Getter
    @Setter
    private String event;

    @Getter
    @Setter
    private String revision;

    @Getter
    @Setter
    private String number;

    @Getter
    @Setter
    private String field;

    @Getter
    @Setter
    private ScoringAlliance red;

    @Getter
    @Setter
    private ScoringAlliance blue;

    public int redScore() {
        return red.getAuto().score() + red.getTeleop().score() + red.getEndgame().score() + blue.getPenalties().score();
    }

    public int blueScore() {
        return blue.getAuto().score() + blue.getTeleop().score() + blue.getEndgame().score() + red.getPenalties().score();
    }
}
