package com.maths22.ftclivescoring.data.scoring.roverRuckus;

import lombok.Getter;
import lombok.Setter;

public class ScoringAuto {
    @Getter
    @Setter
    private int landedRobots;

    @Getter
    @Setter
    private int depotsClaimed;

    @Getter
    @Setter
    private int parkedRobots;

    @Getter
    @Setter
    private int mineralsSampled;

    public int score() {
        return landedRobots * 30
                + depotsClaimed * 15
                + parkedRobots * 10
                + mineralsSampled * 25;
    }
}
