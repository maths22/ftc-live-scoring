package com.maths22.ftclivescoring.data.roverRuckus;

import lombok.Getter;
import lombok.Setter;

public class AutoScore {
    @Getter
    @Setter
    private LandedState robot1Landed;

    @Getter
    @Setter
    private LandedState robot2Landed;

    @Getter
    @Setter
    private DepotsClaimed depotsClaimed;

    @Getter
    @Setter
    private ParkedState robot1Parked;

    @Getter
    @Setter
    private ParkedState robot2Parked;

    @Getter
    @Setter
    private MineralsState field1Sampled;

    @Getter
    @Setter
    private MineralsState field2Sampled;

    public enum DepotsClaimed {
        ZERO, ONE, TWO
    }

    public enum LandedState {
        NOT_LANDED, LANDED
    }

    public enum ParkedState {
        NOT_PARKED, PARKED
    }

    public enum MineralsState {
        NOT_SAMPLED, SAMPLED
    }
}
