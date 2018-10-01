package com.maths22.ftclivescoring.controllers;

import com.maths22.ftclivescoring.data.*;
import com.maths22.ftclivescoring.data.roverRuckus.AutoScore;
import com.maths22.ftclivescoring.data.roverRuckus.EndgameScore;
import com.maths22.ftclivescoring.data.roverRuckus.Score;
import com.maths22.ftclivescoring.data.roverRuckus.TeleopScore;
import com.maths22.ftclivescoring.exceptions.ResourceNotFoundException;
import com.maths22.ftclivescoring.messaging.Messages;
import com.maths22.ftclivescoring.repositories.MatchRepository;
import com.maths22.ftclivescoring.services.ScoringService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Transactional
public class MatchController {
    private final MatchRepository matchRepository;

    private final ScoringService scoringService;

    private final SimpMessagingTemplate stompClient;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public MatchController(MatchRepository matchRepository, ScoringService scoringService, SimpMessagingTemplate stompClient, ApplicationEventPublisher applicationEventPublisher) {
        this.matchRepository = matchRepository;
        this.scoringService = scoringService;
        this.stompClient = stompClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @GetMapping("/api/match")
    public List<String> getMatches() {
        List<String> ret = new ArrayList<>();
        for (Match match : matchRepository.findAllByOrderByIdxAsc()) {
            ret.add(match.getNumber());
        }
        return ret;
    }

    @GetMapping("/api/match/{id}")
    public Match getMatch(@PathVariable String id) {
        return matchRepository.findByNumber(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found!"));
    }

    @PostMapping("/api/match/{id}/reset")
    public Match resetMatch(@PathVariable String id) {
        Match ret = matchRepository.findByNumber(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found!"));
        matchRepository.delete(ret);

        Match newMatch = new Match();
        newMatch.setIdx(ret.getIdx());
        newMatch.setId(ret.getId());
        newMatch.setScoringSystemRaw(ret.getScoringSystemRaw());
        newMatch.setNumber(ret.getNumber());
        newMatch.setRedAlliance(ret.getRedAlliance());
        newMatch.setBlueAlliance(ret.getBlueAlliance());
        newMatch.setBlueScore(new Score());
        newMatch.setRedScore(new Score());

        matchRepository.save(newMatch);
        return ret;
    }

    @PostMapping("/api/match/{id}/random")
    public Match saveRandom(@RequestHeader("Client-Id") String clientId,
                            @PathVariable String id,
                            @RequestBody RandomUpdateRequest request) {
        Match ret = matchRepository.findByNumber(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found!"));
        if(ret.isStarted()) return new Match();
        ret.setField(request.getField());
        ret.setRandom(request.getRandom());
        ret = matchRepository.save(ret);

        Messages.MatchUpdate message = new Messages.MatchUpdate(clientId, ret.getNumber());
        message.getGlobalChanges().put("field", ret.getField());
        message.getGlobalChanges().put("random", ret.getRandom());
        stompClient.convertAndSend("/topic/match", message);

        applicationEventPublisher.publishEvent(new MatchUpdateEvent(this, ret));
        return ret;
    }

    public static class RandomUpdateRequest {
        @Getter
        @Setter
        private int field;

        @Getter
        @Setter
        private int random;
    }

    @PostMapping("/api/match/{id}/auto")
    public Match saveAuto(@RequestHeader("Client-Id") String clientId,
                          @PathVariable String id,
                          @RequestBody UpdateRequest<AutoScore> request) {
        Match ret = matchRepository.findByNumber(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found!"));
        switch (request.getAlliance()) {
            case RED:
                ret.getRedScore().setAuto(request.getData());
                break;
            case BLUE:
                ret.getBlueScore().setAuto(request.getData());
                break;
        }
        ret = matchRepository.save(ret);

        boolean isRed = request.getAlliance().equals(AllianceColor.RED);
        Messages.MatchUpdate message = new Messages.MatchUpdate(clientId, ret.getNumber());
        if(isRed) {
            message.getRedScoreChanges().put("auto", request.getData());
        } else {
            message.getBlueScoreChanges().put("auto", request.getData());
        }
        stompClient.convertAndSend("/topic/match", message);

        applicationEventPublisher.publishEvent(new MatchUpdateEvent(this, ret));
        return ret;
    }

    @PostMapping("/api/match/{id}/teleop")
    public Match saveTeleop(@RequestHeader("Client-Id") String clientId,
                            @PathVariable String id,
                            @RequestBody UpdateRequest<TeleopScore> request) {
        Match ret = matchRepository.findByNumber(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found!"));
        switch(request.getAlliance()) {
            case RED:
                ret.getRedScore().setTeleop(request.getData());
                break;
            case BLUE:
                ret.getBlueScore().setTeleop(request.getData());
                break;
        }
        ret = matchRepository.save(ret);

        boolean isRed = request.getAlliance().equals(AllianceColor.RED);
        Messages.MatchUpdate message = new Messages.MatchUpdate(clientId, ret.getNumber());
        if(isRed) {
            message.getRedScoreChanges().put("teleop", request.getData());
        } else {
            message.getBlueScoreChanges().put("teleop", request.getData());
        }
        stompClient.convertAndSend("/topic/match", message);

        applicationEventPublisher.publishEvent(new MatchUpdateEvent(this, ret));
        return ret;
    }

    @PostMapping("/api/match/{id}/endgame")
    public Match saveEndgame(@RequestHeader("Client-Id") String clientId,
                             @PathVariable String id,
                             @RequestBody UpdateRequest<EndgameScore> request) {
        Match ret = matchRepository.findByNumber(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found!"));
        switch(request.getAlliance()) {
            case RED:
                ret.getRedScore().setEndgame(request.getData());
                break;
            case BLUE:
                ret.getBlueScore().setEndgame(request.getData());
                break;
        }
        ret = matchRepository.save(ret);

        boolean isRed = request.getAlliance().equals(AllianceColor.RED);
        Messages.MatchUpdate message = new Messages.MatchUpdate(clientId, ret.getNumber());
        if(isRed) {
            message.getRedScoreChanges().put("endgame", request.getData());
        } else {
            message.getBlueScoreChanges().put("endgame", request.getData());
        }
        stompClient.convertAndSend("/topic/match", message);

        applicationEventPublisher.publishEvent(new MatchUpdateEvent(this, ret));
        return ret;
    }

    @PostMapping("/api/match/{id}/penalties")
    public Match savePenalties(@RequestHeader("Client-Id") String clientId,
                               @PathVariable String id,
                               @RequestBody UpdateRequest<PenaltyScore> request) {
        Match ret = matchRepository.findByNumber(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found!"));
        switch(request.getAlliance()) {
            case RED:
                ret.getRedScore().setPenalties(request.getData());
                break;
            case BLUE:
                ret.getBlueScore().setPenalties(request.getData());
                break;
        }
        ret = matchRepository.save(ret);

        boolean isRed = request.getAlliance().equals(AllianceColor.RED);
        Messages.MatchUpdate message = new Messages.MatchUpdate(clientId, ret.getNumber());
        if(isRed) {
            message.getRedScoreChanges().put("penalties", request.getData());
        } else {
            message.getBlueScoreChanges().put("penalties", request.getData());
        }
        stompClient.convertAndSend("/topic/match", message);

        applicationEventPublisher.publishEvent(new MatchUpdateEvent(this, ret));
        return ret;
    }

    @PostMapping("/api/match/{id}/alliance")
    public Match saveAlliance(@RequestHeader("Client-Id") String clientId,
                               @PathVariable String id,
                               @RequestBody UpdateRequest<Alliance> request) {
        Match ret = matchRepository.findByNumber(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found!"));
        switch(request.getAlliance()) {
            case RED:
                //TODO should this really replace it?
                ret.setRedAlliance(request.getData());
                break;
            case BLUE:
                //TODO should this really replace it?
                ret.setBlueAlliance(request.getData());
                break;
        }
        ret = matchRepository.save(ret);

        boolean isRed = request.getAlliance().equals(AllianceColor.RED);
        Messages.MatchUpdate message = new Messages.MatchUpdate(clientId, ret.getNumber());
        if(isRed) {
            message.getGlobalChanges().put("redAlliance", request.getData());
        } else {
            message.getGlobalChanges().put("blueAlliance", request.getData());
        }
        stompClient.convertAndSend("/topic/match", message);

        applicationEventPublisher.publishEvent(new MatchUpdateEvent(this, ret));
        return ret;
    }

    @PostMapping("/api/match/{id}/submit")
    public Match submitScores(@RequestHeader("Client-Id") String clientId,
                              @PathVariable String id,
                              @RequestBody SubmitRequest request) {
        Match ret = matchRepository.findByNumber(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found!"));

        Messages.MatchUpdate message = new Messages.MatchUpdate(clientId, ret.getNumber());

        if(request.isRedAuto()) {
            ret.getRedScore().setAutoSubmitted(true);
            message.getRedScoreChanges().put("autoSubmitted", true);
        }
        if(request.isBlueAuto()) {
            ret.getBlueScore().setAutoSubmitted(true);
            message.getBlueScoreChanges().put("autoSubmitted", true);
        }
        if(request.isRed()) {
            ret.getRedScore().setSubmitted(true);
            message.getRedScoreChanges().put("submitted", true);
            if(request.isFinalize()) {
                ret.getRedScore().setFinalized(true);
                message.getRedScoreChanges().put("finalized", true);
            }
        }
        if(request.isBlue()) {
            ret.getBlueScore().setSubmitted(true);
            message.getBlueScoreChanges().put("submitted", true);
            if(request.isFinalize()) {
                ret.getBlueScore().setFinalized(true);
                message.getBlueScoreChanges().put("finalized", true);
            }
        }
        ret = matchRepository.save(ret);
        if(ret.getRedScore().isFinalized() && ret.getBlueScore().isFinalized()) {
            ret.setRevision(ret.getRevision() + 1);
            ret = matchRepository.save(ret);
            scoringService.submitScores(ret);
        }


        stompClient.convertAndSend("/topic/match", message);

        applicationEventPublisher.publishEvent(new MatchUpdateEvent(this, ret));
        return ret;
    }

    @PostMapping("/api/match/{id}/start")
    public Match submitScores(@RequestHeader("Client-Id") String clientId,
                              @PathVariable String id) {
        Match ret = matchRepository.findByNumber(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found!"));
        ret.setStarted(true);
        ret = matchRepository.save(ret);

        Messages.MatchUpdate message = new Messages.MatchUpdate(clientId, ret.getNumber());
        message.getGlobalChanges().put("started", true);
        stompClient.convertAndSend("/topic/match", message);

        applicationEventPublisher.publishEvent(new MatchUpdateEvent(this, ret));
        return ret;
    }

    public static class SubmitRequest {
        @Getter
        @Setter
        private boolean redAuto;

        @Getter
        @Setter
        private boolean blueAuto;

        @Getter
        @Setter
        private boolean red;

        @Getter
        @Setter
        private boolean blue;

        @Getter
        @Setter
        private boolean finalize;
    }

    public static class UpdateRequest<T> {
        @Getter
        @Setter
        private AllianceColor alliance;

        @Getter
        @Setter
        private T data;
    }

    public class MatchUpdateEvent extends ApplicationEvent {
        @Getter
        private Match match;

        public MatchUpdateEvent(Object source, Match match) {
            super(source);
            this.match = match;
        }
    }
}
