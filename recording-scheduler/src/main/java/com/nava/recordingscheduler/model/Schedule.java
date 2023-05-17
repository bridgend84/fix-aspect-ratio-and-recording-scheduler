package com.nava.recordingscheduler.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Schedule {
    private LocalDateTime txDayDate;
    private String formattedTxDayDate;
    private String channelID;
    private List<Event> events;
}
