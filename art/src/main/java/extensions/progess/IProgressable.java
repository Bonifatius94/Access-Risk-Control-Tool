package extensions.progess;

public interface IProgressable {

    /**
     * This method registers an event listener.
     *
     * @param listener the listener to be registered
     */
    void register(IProgressListener listener);

    /**
     * This method unregisters an event listener.
     *
     * @param listener the listener to be unregistered
     */
    void unregister(IProgressListener listener);

    /**
     * This method resets the current progress to 0.
     */
    void resetProgress();

    /**
     * This method notifies all registered listeners.
     *
     * @param newProgress the percentage of the new progress (value between 0 and 1)
     */
    void notifyProgress(double newProgress);

    /**
     * This method sets the the total totalSteps of the progress scala.
     */
    void setTotalProgressSteps(int steps);

    /**
     * This method increases the steps by one and notifies all registered listeners afterwards.
     */
    void stepProgress();

    /**
     * This method increases the steps by the given amount and notifies all registered listeners afterwards.
     *
     * @param steps the amount of steps to be increased
     */
    void stepProgress(int steps);

}
