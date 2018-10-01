package com.maths22.ftclivescoring.data.scoring.roverRuckus;

import lombok.Getter;
import lombok.Setter;

public class ScoringTeleop {
    @Getter
    @Setter
    private int depotMinerals;

    @Getter
    @Setter
    private int goldCargo;

    @Getter
    @Setter
    private int silverCargo;

    public int score() {
        return depotMinerals * 2
                + goldCargo * 5
                + silverCargo * 5;
    }
}
