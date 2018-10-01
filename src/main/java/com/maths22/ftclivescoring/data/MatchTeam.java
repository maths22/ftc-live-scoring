package com.maths22.ftclivescoring.data;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class MatchTeam implements Serializable {
    @Getter
    @Setter
    private int number;

    @Getter
    @Setter
    private boolean noShow;

    @Getter
    @Setter
    private boolean yellowCard;

    @Getter
    @Setter
    private boolean redCard;

    @Getter
    @Setter
    private boolean participating;
}
