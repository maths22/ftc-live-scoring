package com.maths22.ftclivescoring.listeners;

import com.maths22.ftclivescoring.controllers.MatchController;
import com.maths22.ftclivescoring.data.Match;
import com.maths22.ftclivescoring.data.scoring.ScoringMatch;
import com.maths22.ftclivescoring.services.ScoringService;
import com.maths22.ftclivescoring.services.ScoringSystemService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MatchUpdateListener implements ApplicationListener<MatchController.MatchUpdateEvent> {
    private final ScoringService scoringService;

    private final ScoringSystemService scoringSystemService;

    @Autowired
    public MatchUpdateListener(ScoringService scoringService, ScoringSystemService scoringSystemService) {
        this.scoringService = scoringService;
        this.scoringSystemService = scoringSystemService;
    }

    @Override
    public void onApplicationEvent(MatchController.MatchUpdateEvent event) {
        ScoringMatch match = scoringService.transformMatch(event.getMatch());

        DisplayScore score = new DisplayScore(event.getMatch(), match.redScore(), match.blueScore());
        scoringSystemService.sendLiveScore(match.getNumber(), score);
    }

    public static class DisplayScore {
        @Getter
        @Setter
        private Match match;

        @Getter
        @Setter
        private int redScore;

        @Getter
        @Setter
        private int blueScore;

        public DisplayScore(Match match, int redScore, int blueScore) {
            this.match = match;
            this.redScore = redScore;
            this.blueScore = blueScore;
        }
    }
}
