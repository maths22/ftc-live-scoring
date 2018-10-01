package com.maths22.ftclivescoring.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maths22.ftclivescoring.data.AllianceColor;
import com.maths22.ftclivescoring.data.Match;
import com.maths22.ftclivescoring.data.MatchTeam;
import com.maths22.ftclivescoring.data.roverRuckus.AutoScore;
import com.maths22.ftclivescoring.data.roverRuckus.Score;
import com.maths22.ftclivescoring.data.scoring.ScoringAlliance;
import com.maths22.ftclivescoring.data.scoring.ScoringMatch;
import com.maths22.ftclivescoring.data.scoring.roverRuckus.ScoringAuto;
import com.maths22.ftclivescoring.data.scoring.roverRuckus.ScoringEndgame;
import com.maths22.ftclivescoring.data.scoring.roverRuckus.ScoringTeleop;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Service
public class ScoringService {
    private static Log log = LogFactory.getLog(ScoringService.class);

    private final ScoringReportService reportService;
    private final ScoringPrintService printService;
    private final ObjectMapper mapper;
    private final ScoringSystemService scoringSystemService;

    private BlockingQueue<ScoringMatch> matchQueue = new LinkedBlockingQueue<>();

    @Autowired
    public ScoringService(ScoringReportService reportService, ScoringPrintService printService, ObjectMapper mapper, ScoringSystemService scoringSystemService) {
        this.reportService = reportService;
        this.printService = printService;
        this.mapper = mapper;
        this.scoringSystemService = scoringSystemService;
    }

    @Scheduled(fixedRate = 15000)
    public void reportCurrentTime() {
        if(matchQueue.size() > 0) {
            log.info("Retrying match submission...");
            for (int i = 0; i < matchQueue.size(); i++) {
                ScoringMatch match = matchQueue.peek();
                if (match == null) break;
                boolean success = scoringSystemService.submitMatch(match);
                if (!success) {
                    log.warn("Still unable to submit match " + match.getNumber() + " to scoring system");
                } else {
                    log.info(match.getNumber() + " submitted to scoring system");
                    matchQueue.remove();
                }
            }
        }

    }

    public boolean submitScores(Match match) {
        ScoringMatch sMatch = transformMatch(match);
        sMatch.getBlue().setAlliance(
                sMatch.getBlue().getAlliance().stream().filter(MatchTeam::isParticipating).collect(Collectors.toList())
        );
        sMatch.getRed().setAlliance(
                sMatch.getRed().getAlliance().stream().filter(MatchTeam::isParticipating).collect(Collectors.toList())
        );
        byte[] matchJson;
        try {
            matchJson = mapper.writeValueAsBytes(sMatch);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        if(!reportService.saveReport(new ByteArrayInputStream(matchJson), "scoring_sheet-" + match.getNumber())) return false;
        if(!printService.printScoresheet("scoring_sheet-" + match.getNumber())) return false;
        //Redo it to add the teams back
        sMatch = transformMatch(match);
        boolean success = scoringSystemService.submitMatch(sMatch);
        if(!success) {
            matchQueue.add(sMatch);
            log.error("Unable to submit match " + sMatch.getNumber() + " to scoring system");
            return false;
        }
        return true;
    }

    public ScoringMatch transformMatch(Match match) {
        ScoringMatch ret = new ScoringMatch();
        ret.setEvent(scoringSystemService.getEventName());
        ret.setNumber(match.getNumber());
        ret.setField(Integer.toString(match.getField()));
        ret.setRevision(Integer.toString(match.getRevision()));
        ret.setBlue(transformAlliance(match, AllianceColor.BLUE));
        ret.setRed(transformAlliance(match, AllianceColor.RED));
        return ret;
    }

    private ScoringAlliance transformAlliance(Match match, AllianceColor color) {
        ScoringAlliance ret = new ScoringAlliance();
        Score score = color.equals(AllianceColor.BLUE) ? match.getBlueScore() : match.getRedScore();
        ret.setAuto(transformAuto(score));
        ret.setTeleop(transformTeleop(score));
        ret.setEndgame(transformEndgame(score));
        ret.setPenalties(score.getPenalties());
        ret.setAlliance(color.equals(AllianceColor.BLUE) ? match.getBlueAlliance() : match.getRedAlliance());
        return ret;
    }

    private ScoringTeleop transformTeleop(Score score) {
        ScoringTeleop ret = new ScoringTeleop();
        ret.setDepotMinerals(score.getTeleop().getDepotMinerals());
        ret.setGoldCargo(score.getTeleop().getGoldCargo());
        ret.setSilverCargo(score.getTeleop().getSilverCargo());
        return ret;
    }


    private ScoringAuto transformAuto(Score score) {
        ScoringAuto ret = new ScoringAuto();
        int landedRobots = 0;
        if(AutoScore.LandedState.LANDED.equals(score.getAuto().getRobot1Landed())) landedRobots++;
        if(AutoScore.LandedState.LANDED.equals(score.getAuto().getRobot2Landed())) landedRobots++;
        ret.setLandedRobots(landedRobots);
        if(score.getAuto().getDepotsClaimed() != null) {
            ret.setDepotsClaimed(score.getAuto().getDepotsClaimed().ordinal());
        }
        int parkedRobots = 0;
        if(AutoScore.ParkedState.PARKED.equals(score.getAuto().getRobot1Parked())) parkedRobots++;
        if(AutoScore.ParkedState.PARKED.equals(score.getAuto().getRobot2Parked())) parkedRobots++;
        ret.setParkedRobots(parkedRobots);
        int mineralsSampled = 0;
        if(AutoScore.MineralsState.SAMPLED.equals(score.getAuto().getField1Sampled())) mineralsSampled++;
        if(AutoScore.MineralsState.SAMPLED.equals(score.getAuto().getField2Sampled())) mineralsSampled++;
        ret.setMineralsSampled(mineralsSampled);
        return ret;
    }


    private ScoringEndgame transformEndgame(Score score) {
        ScoringEndgame ret = new ScoringEndgame();
        int latchedRobots = 0;
        int robotsInCrater = 0;
        int robotsCompletelyInCrater = 0;
        if(score.getEndgame().getRobot1Position() != null) {
            switch (score.getEndgame().getRobot1Position()) {
                case PARKED:
                    robotsInCrater++;
                    break;
                case PARKED_COMPLETELY:
                    robotsCompletelyInCrater++;
                    break;
                case LATCHED:
                    latchedRobots++;
                    break;
            }
        }
        if(score.getEndgame().getRobot2Position() != null) {
            switch (score.getEndgame().getRobot2Position()) {
                case PARKED:
                    robotsInCrater++;
                    break;
                case PARKED_COMPLETELY:
                    robotsCompletelyInCrater++;
                    break;
                case LATCHED:
                    latchedRobots++;
                    break;
            }
        }
        ret.setLatchedRobots(latchedRobots);
        ret.setRobotsInCrater(robotsInCrater);
        ret.setRobotsCompletelyInCrater(robotsCompletelyInCrater);

        return ret;
    }
}
