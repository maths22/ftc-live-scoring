package com.maths22.ftclivescoring.data.roverRuckus;

import lombok.Getter;
import lombok.Setter;

public class EndgameScore {
    @Getter
    @Setter
    private RobotEndgamePos robot1Position;

    @Getter
    @Setter
    private RobotEndgamePos robot2Position;

    public enum RobotEndgamePos {
        NONE, LATCHED, PARKED, PARKED_COMPLETELY
    }
}
