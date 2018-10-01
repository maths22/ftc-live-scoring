package com.maths22.ftclivescoring.data;

import lombok.Getter;
import lombok.Setter;

public class PenaltyScore {
    @Getter
    @Setter
    private int minor;

    @Getter
    @Setter
    private int major;

    public int score() {
        return minor * 10 + major * 40;
    }
}
