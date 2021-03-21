package org.delmesoft.crazyblocks.utils;

/**
 * Created by sergi on 26/08/17.
 */

public class TimeMeter {

    public static final TimeMeter tmp = new TimeMeter();

    public enum TimeResolution {

        MILLIS{
            public long now(){
                return System.currentTimeMillis();
            }

        },
        NANOS {
            public long now(){
                return System.nanoTime();
            }
        };

        public abstract long now();

    }

    private long startTime;
    private TimeResolution timeResolution;

    public void begin(TimeResolution timeResolution) {
        this.timeResolution = timeResolution;
        startTime = timeResolution.now();
    }

    public long end() {
        return timeResolution.now() - startTime;
    }

    public String end(String format) {
        long result = end();
        return String.format(format, result);
    }

}
