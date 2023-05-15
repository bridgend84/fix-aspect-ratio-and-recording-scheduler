package com.nava.recordingscheduler.model;

import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class EventDTO {
    private Map<String, String> start;
    @JsonIgnore
    private Map<String, Object> technical;
    @JsonIgnore
    private Map<String, Object> secondaryEventList;
    private Map<String, String> programmeProperties;
    @JsonIgnore
    private Map<String, Object> source;
    @JsonIgnore
    private Map<String, Object> caflagList;
}
