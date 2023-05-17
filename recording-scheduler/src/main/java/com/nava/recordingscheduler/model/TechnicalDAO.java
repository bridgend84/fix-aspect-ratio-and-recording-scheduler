package com.nava.recordingscheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class TechnicalDAO {
    @JsonIgnore
    private String audioMode;
    private int framePerSec;
    @JsonIgnore
    private Object audioBlockList;
    @JsonIgnore
    private String resolution;
    @JsonIgnore
    private String aspratioRFName;
    @JsonIgnore
    private String afdrfshortName;
}
