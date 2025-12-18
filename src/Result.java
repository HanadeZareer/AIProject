public class Result {
   private boolean success;
    private int generations;
    private long timeMs;

    public Result(boolean success, int generations, long timeMs ) {
        this.success = success;
        this.generations=generations;
        this.timeMs=timeMs;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getGenerations() {
        return generations;
    }

    public void setGenerations(int generations) {
        this.generations = generations;
    }

    public long getTimeMs() {
        return timeMs;
    }

    public void setTimeMs(long timeMs) {
        this.timeMs = timeMs;
    }
}
