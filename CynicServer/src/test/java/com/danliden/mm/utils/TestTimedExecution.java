package com.danliden.mm.utils;

import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class TestTimedExecution {

    @Test(timeout = 10000)
    public void executeAfterTimerEnds() {
        Execution postExecutionMock =  mock(Execution.class);
        TimedExecution timedExecution = new TimedExecution()
                .setTime(TimeMeasurement.of(5, TimeUnits.MILLISECONDS))
                .setPostTimerExecution(postExecutionMock)
                .start();

        waitForFinish(timedExecution);
        verify(postExecutionMock, times(1)).run();
    }

    @Test(timeout = 10000)
    public void executeInIntervals() {
        Execution intervalExecutionMock =  mock(Execution.class);
        TimedExecution timedExecution = new TimedExecution()
                .setTime(TimeMeasurement.of(5, TimeUnits.MILLISECONDS))
                .setIntervalExecution(intervalExecutionMock, TimeMeasurement.of(1, TimeUnits.MILLISECONDS))
                .start();

        waitForFinish(timedExecution);
        verify(intervalExecutionMock, times(5)).run();
    }

    private void waitForFinish(TimedExecution timedExecution) {
        while(timedExecution.isRunning());
    }


}
