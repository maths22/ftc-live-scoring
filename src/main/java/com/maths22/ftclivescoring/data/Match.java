package com.maths22.ftclivescoring.data;

import com.maths22.ftclivescoring.data.roverRuckus.Score;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@TypeDef(
        name = "json",
        typeClass = JsonStringType.class
)
public class Match {

    public Match() {
    }

    public Match(String number) {
        this.number = number;
    }

    @Getter
    @Setter
    @GeneratedValue
    @Id
    private Integer id;

    @Getter
    @Setter
    private int idx;

    @UpdateTimestamp
    @Getter
    @Setter
    private LocalDateTime modifyDate;

    @Getter
    @Setter
    @Column(name = "num", nullable = false)
    private String number;

    @Getter
    @Setter
    private int field;

    @Getter
    @Setter
    private int revision;

    @Getter
    @Setter
    private int random;

    @Getter
    @Setter
    private boolean started = false;

    @Getter
    @Setter
    @Column(columnDefinition = "LONGVARCHAR", nullable = false)
    private String scoringSystemRaw;

    @Type( type = "json" )
    @Getter
    @Setter
    @Column(columnDefinition = "LONGVARCHAR", nullable = false)
    private Alliance redAlliance;

    @Type( type = "json" )
    @Getter
    @Setter
    @Column(columnDefinition = "LONGVARCHAR", nullable = false)
    private Alliance blueAlliance;

    @Type( type = "json" )
    @Getter
    @Setter
    @Column(columnDefinition = "LONGVARCHAR", nullable = false)
    private Score redScore;

    @Type( type = "json" )
    @Getter
    @Setter
    @Column(columnDefinition = "LONGVARCHAR", nullable = false)
    private Score blueScore;


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
