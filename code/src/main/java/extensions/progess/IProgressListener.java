package extensions.progess;

public interface IProgressListener {

    /**
     * This method is called by a progressable class when the progress has changed.
     *
     * @param percentage the percentage of the progress (between 0 and 1)
     */
    void onProgressChanged(double percentage);

}
