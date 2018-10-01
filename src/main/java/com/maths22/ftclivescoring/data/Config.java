package com.maths22.ftclivescoring.data;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@Entity
@TypeDef(
        name = "json",
        typeClass = JsonStringType.class
)
public class Config {
    //Note field 0 is global config!
    @Getter
    @Setter
    @Id
    private int field;

    @Type( type = "json" )
    @Getter
    @Setter
    @Column(columnDefinition = "LONGVARCHAR", nullable = false)
    private Map<String, Object> values = new HashMap<>();
}
