package com.nava.recordingscheduler.model;

import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Schedule {
    private String txDayDate;
    private String channelID;
    private int alternative;
    private List<Event> events;
}
