package com.maths22.ftclivescoring.repositories;

import com.maths22.ftclivescoring.data.Match;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface MatchRepository extends CrudRepository<Match, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Match> findByNumber(String num);

    Iterable<Match> findAllByOrderByIdxAsc();
}
