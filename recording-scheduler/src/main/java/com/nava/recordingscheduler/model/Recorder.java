package com.nava.recordingscheduler.model;

import lombok.*;
import org.moormanity.smpte.timecode.TimecodeRecord;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Recorder {
    private TimecodeRecord recordStarted;
    private boolean isRecording;
    private Set<RecordingTask> recordingTasks;

    public void addRecordingTask(RecordingTask recordingTask) {
        recordingTasks.add(recordingTask);
    }

    public void removeRecordingTask(RecordingTask recordingTask) {
        recordingTasks.remove(recordingTask);
    }
}
