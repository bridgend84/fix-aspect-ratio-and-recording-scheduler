package com.nava.recordingscheduler.model;

import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Event {
    private String startTime;
    private String durationTC;
    private String requestType;
    private String seriesTitle;
    private String programmeTitle;
    private int FPS;
    private boolean recordable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return
                FPS == event.FPS &&
                recordable == event.recordable &&
                Objects.equals(startTime, event.startTime) &&
                Objects.equals(durationTC, event.durationTC) &&
                Objects.equals(requestType, event.requestType) &&
                Objects.equals(seriesTitle, event.seriesTitle) &&
                Objects.equals(programmeTitle, event.programmeTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, durationTC, requestType, seriesTitle, programmeTitle, FPS, recordable);
    }
}
