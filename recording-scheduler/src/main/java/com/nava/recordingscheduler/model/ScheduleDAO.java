package com.nava.recordingscheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ScheduleDAO {
    private String txDayDate;
    private String channelID;
    @JsonIgnore
    private int alternative;
    private Map<String, List<EventDAO>> eventList;
}
