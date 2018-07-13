package extensions.progess;

import java.util.LinkedHashSet;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class ProgressableBase implements IProgressable {

    // =============================
    //           members
    // =============================

    private Set<IProgressListener> listeners = new LinkedHashSet<>();

    private double currentProgress = 0;

    private int stepsCount = 0;
    private int totalSteps = 1;

    // =============================
    //    register / unregister
    // =============================

    /**
     * This method registers an event listener.
     *
     * @param listener the listener to be registered
     */
    @Override
    public void register(IProgressListener listener) {
        listeners.add(listener);
    }

    /**
     * This method unregisters an event listener.
     *
     * @param listener the listener to be unregistered
     */
    @Override
    public void unregister(IProgressListener listener) {
        listeners.remove(listener);
    }

    // =============================
    //          progress
    // =============================

    /**
     * This method resets the current progress to 0.
     */
    @Override
    public void resetProgress() {
        currentProgress = 0;
        stepsCount = 0;
    }

    /**
     * This method notifies all registered listeners.
     *
     * @param newProgress the percentage of the new progress (value between 0 and 1)
     */
    @Override
    public void notifyProgress(double newProgress) {
        currentProgress = newProgress;
        listeners.forEach(x -> x.onProgressChanged(newProgress));
    }

    // =============================
    //       progress (steps)
    // =============================

    /**
     * This method sets the the total totalSteps of the progress scala.
     */
    public void setTotalProgressSteps(int steps) {
        this.totalSteps = steps;
    }

    /**
     * This method increases the steps by one and notifies all registered listeners afterwards.
     */
    public void stepProgress() {
        stepProgress(1);
    }

    /**
     * This method increases the steps by the given amount and notifies all registered listeners afterwards.
     *
     * @param steps the amount of steps to be increased
     */
    public void stepProgress(int steps) {
        stepsCount += steps;
        notifyProgress(((double)stepsCount) / totalSteps);
    }

}
