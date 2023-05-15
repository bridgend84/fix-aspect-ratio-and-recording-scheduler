package com.nava.recordingscheduler.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Event {
    private String startTime;
    private String requestType;
    private String seriesTitle;
    private String programmeTitle;
    private boolean recordable;
}
