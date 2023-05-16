package com.nava.recordingscheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class EventDTO {
    private Map<String, String> start;
    private TechnicalDTO technical;
    @JsonIgnore
    private Map<String, Object> secondaryEventList;
    private Map<String, String> programmeProperties;
    @JsonIgnore
    private Map<String, Object> source;
    @JsonIgnore
    private Map<String, Object> caflagList;

    public int getFPS() {
        return this.technical.getFramePerSec();
    }
}
