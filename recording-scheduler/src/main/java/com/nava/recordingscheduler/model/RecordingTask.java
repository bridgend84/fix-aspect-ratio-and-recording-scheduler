package com.nava.recordingscheduler.model;

import lombok.*;
import org.moormanity.smpte.timecode.TimecodeRecord;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RecordingTask {
    private LocalDateTime scheduleDay;
    private TimecodeRecord startTime;
    private TimecodeRecord stopTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordingTask that = (RecordingTask) o;
        return Objects.equals(scheduleDay, that.scheduleDay) && Objects.equals(startTime, that.startTime) && Objects.equals(stopTime, that.stopTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleDay, startTime, stopTime);
    }
}
