package com.maths22.ftclivescoring.data.scoring.roverRuckus;

import lombok.Getter;
import lombok.Setter;

public class ScoringEndgame {
    @Getter
    @Setter
    private int latchedRobots;

    @Getter
    @Setter
    private int robotsInCrater;

    @Getter
    @Setter
    private int robotsCompletelyInCrater;

    public int score() {
        return latchedRobots * 50
                + robotsInCrater * 15
                + robotsCompletelyInCrater * 15;
    }
}
