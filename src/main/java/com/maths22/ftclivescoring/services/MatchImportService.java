package com.maths22.ftclivescoring.services;

import com.maths22.ftclivescoring.data.Alliance;
import com.maths22.ftclivescoring.data.Match;
import com.maths22.ftclivescoring.data.MatchTeam;
import com.maths22.ftclivescoring.data.roverRuckus.Score;
import com.maths22.ftclivescoring.messaging.Messages;
import com.maths22.ftclivescoring.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchImportService {
    private final MatchRepository matchRepository;

    private final SimpMessagingTemplate stompClient;

    @Autowired
    public MatchImportService(MatchRepository matchRepository, SimpMessagingTemplate stompClient) {
        this.matchRepository = matchRepository;
        this.stompClient = stompClient;
    }

    @Transactional
    public void load(List<ScoringSystemService.ScoringSendMatch> list) {
        int i = 0;
        for(ScoringSystemService.ScoringSendMatch m : list) {
            i++;
            String number = m.getId().split(" ")[1];
            Match match = matchRepository.findByNumber(number).orElseGet(() -> new Match(number));
            match.setScoringSystemRaw(m.getFileString());
            match.setIdx(i);

            //TODO make sure the one we have matches
            //TODO what do we do if the match is already scored?
            if(match.getBlueAlliance() == null) {
                match.setBlueAlliance(new Alliance(m.getBlueAlliance().stream().map((sst) -> {
                    MatchTeam mt = new MatchTeam();
                    mt.setNumber(sst.getNumber());
                    if(m.getBlueAlliance().size() == 2) {
                        mt.setParticipating(true);
                    }
                    return mt;
                }).collect(Collectors.toList())));
            }
            if(match.getRedAlliance() == null) {
                match.setRedAlliance(new Alliance(m.getRedAlliance().stream().map((sst) -> {
                    MatchTeam mt = new MatchTeam();
                    mt.setNumber(sst.getNumber());
                    if(m.getRedAlliance().size() == 2) {
                        mt.setParticipating(true);
                    }
                    return mt;
                }).collect(Collectors.toList())));
            }
            if(match.getRedScore() == null) {
                match.setRedScore(new Score());
            }

            if(match.getBlueScore() == null) {
                match.setBlueScore(new Score());
            }
            matchRepository.save(match);
        }

        List<String> send = new ArrayList<>();
        for (Match match : matchRepository.findAllByOrderByIdxAsc()) {
            send.add(match.getNumber());
        }

        stompClient.convertAndSend("/topic/matchList",
                new Messages.MatchList(send));

    }
}
