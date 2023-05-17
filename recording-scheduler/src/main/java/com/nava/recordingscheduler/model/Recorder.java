package com.nava.recordingscheduler.model;

import lombok.*;

import java.util.TreeSet;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Recorder {
    private RecordingTask currentRecordingTask;
    private boolean isRecording;
    private TreeSet<RecordingTask> recordingTasks;

    public void addRecordingTask(RecordingTask recordingTask) {
        if (currentRecordingTask == null) {
            this.currentRecordingTask = recordingTask;
        } else {
            recordingTasks.add(recordingTask);
        }
    }

    public RecordingTask peekNextTask() {
        return recordingTasks.isEmpty() ? null : recordingTasks.first();
    }

    public void discardCurrentAndCueNextTask() {
        currentRecordingTask = recordingTasks.pollFirst();
    }
}
