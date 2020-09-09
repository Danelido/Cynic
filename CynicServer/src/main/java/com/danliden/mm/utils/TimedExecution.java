package com.danliden.mm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimedExecution {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int time;
    private int intervalTime;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Execution intervalExecution;
    private Execution postTimerExecution;

    public TimedExecution() {

    }

    public TimedExecution start() {
        if (!running.get()) {
            running.set(true);
            setupIntervalTimer();
        }

        return this;
    }

    public TimedExecution setTime(int time) {
        this.time = time;
        return this;
    }

    public TimedExecution setPostTimerExecution(Execution execution) {
        postTimerExecution = execution;
        return this;
    }

    public TimedExecution setIntervalExecution(Execution intervalExecution, int intervalMS) {
        this.intervalExecution = intervalExecution;
        this.intervalTime = intervalMS;
        return this;
    }

    private void setupIntervalTimer() {
        if (intervalTime == 0) {
            intervalTime = time;
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private int executionTimes = (int) (Math.ceil((double) time / (double) intervalTime));

            @Override
            public void run() {
                if (!running.get()) {
                    return;
                }
                logger.debug("Execution time: {}\nCurrentTimeMillis: {}", executionTimes, System.currentTimeMillis());
                executionTimes--;
                runIntervalExecution();

                if (executionTimes == 0) {
                    runPostExecution();
                    reset();
                }
            }

            private void runIntervalExecution() {
                if (intervalExecution != null) {
                    intervalExecution.run();
                }
            }

            private void runPostExecution() {
                if (postTimerExecution != null) {
                    postTimerExecution.run();
                }
            }

            private void reset() {
                running.set(false);
                this.cancel();
            }
        }, 0, 500);
    }

    public boolean isRunning() {
        return running.get();
    }
}
