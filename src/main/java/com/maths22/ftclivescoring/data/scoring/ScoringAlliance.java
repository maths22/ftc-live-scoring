package com.maths22.ftclivescoring.data.scoring;

import com.maths22.ftclivescoring.data.MatchTeam;
import com.maths22.ftclivescoring.data.PenaltyScore;
import com.maths22.ftclivescoring.data.scoring.roverRuckus.ScoringAuto;
import com.maths22.ftclivescoring.data.scoring.roverRuckus.ScoringEndgame;
import com.maths22.ftclivescoring.data.scoring.roverRuckus.ScoringTeleop;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ScoringAlliance {
    @Getter
    @Setter
    private ScoringAuto auto;

    @Getter
    @Setter
    private ScoringTeleop teleop;

    @Getter
    @Setter
    private ScoringEndgame endgame;

    @Getter
    @Setter
    private PenaltyScore penalties;

    @Getter
    @Setter
    private List<MatchTeam> alliance;
}
