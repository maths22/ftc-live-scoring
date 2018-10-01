package com.maths22.ftclivescoring.data.roverRuckus;

import com.maths22.ftclivescoring.data.PenaltyScore;
import lombok.Getter;
import lombok.Setter;

public class Score {
    @Getter
    @Setter
    private boolean submitted = false;

    @Getter
    @Setter
    private boolean autoSubmitted = false;

    @Getter
    @Setter
    private boolean finalized = false;

    @Getter
    @Setter
    private AutoScore auto = new AutoScore();

    @Getter
    @Setter
    private TeleopScore teleop = new TeleopScore();

    @Getter
    @Setter
    private EndgameScore endgame = new EndgameScore();

    @Getter
    @Setter
    private PenaltyScore penalties = new PenaltyScore();
}
