package tools.tracing;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class TraceOut {

    private static boolean isEnabled;
    private static PrintStream out;
    private static TraceMode mode;
    private static TraceLevel level;

    private static String traceFilePath;
    private static LocalDate date = LocalDate.now();

    /**
     * This method enables the logging tool.
     *
     * @param filePath the file path of the log file
     * @param mode the log file mode (file per day, append, overwrite)
     * @param level the level of log detail (All > Verbose > Info > Error). Usually test with all / verbose and release with info / error.
     * @throws Exception caused by file writing issues
     */
    public static void enable(String filePath, TraceMode mode, TraceLevel level) throws Exception {

        // init trace level and mode
        TraceOut.mode = mode;
        TraceOut.level = level;
        TraceOut.traceFilePath = filePath;

        initOutputStream(filePath);

        isEnabled = true;
    }

    /**
     * This method switches the logging tool off.
     */
    public static void disable() {

        isEnabled = false;
        out.close();
    }

    private static void initOutputStream(String filePath) throws Exception {

        if (mode == TraceMode.FilePerDay) {

            filePath = filePath.replace("%YEAR%", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy")));
            filePath = filePath.replace("%MONTH%", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("MM")));
            filePath = filePath.replace("%DAY%", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd")));
        }

        // init output file stream
        boolean append = (TraceOut.mode != TraceMode.Overwrite);
        out = new PrintStream(new FileOutputStream(filePath, append));
    }

    /**
     * This methods gets the output stream of the current log file.
     *
     * @return the output stream of the current log file
     */
    public static PrintStream getOutputStream() {
        return TraceOut.out;
    }

    /**
     * This method writes an enter method entry to log file. Class and method name are retrieved from call stack.
     * The default and also highly recommended trace level is 'verbose'.
     */
    public static void enter() {

        // write enter with default trace level 'verbose'
        enter(TraceLevel.Verbose);
    }

    /**
     * This method writes an enter method entry to log file. Class and method name are retrieved from call stack.
     * The default and also highly recommended trace level is 'verbose'.
     *
     * @param level the trace level of the log entry to write
     */
    @SuppressWarnings({"WeakerAccess"})
    public static void enter(TraceLevel level) {

        if (isEnabled && TraceOut.level.ordinal() >= level.ordinal()) {

            // get caller from stack
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StackTraceElement caller = stack[3];

            // get class and method name from caller
            String className = caller.getClassName();
            String methodName = caller.getMethodName();

            // write enter message to trace
            writeToTrace("ENTER", className + " " + methodName);
        }
    }

    /**
     * This method writes a leave method entry to log file. Class and method name are retrieved from call stack.
     * The default and also highly recommended trace level is 'verbose'.
     */
    public static void leave() {

        // write leave with default trace level 'verbose'
        leave(TraceLevel.Verbose);
    }

    /**
     * This method writes a leave method entry to log file. Class and method name are retrieved from call stack.
     * The default and also highly recommended trace level is 'verbose'.
     *
     * @param level the trace level of the log entry to write
     */
    @SuppressWarnings({"WeakerAccess"})
    public static void leave(TraceLevel level) {

        if (isEnabled && TraceOut.level.ordinal() >= level.ordinal()) {

            // get caller from stack
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            StackTraceElement caller = stack[3];

            // get class and method name from caller
            String className = caller.getClassName();
            String methodName = caller.getMethodName();

            // write enter message to trace
            writeToTrace("LEAVE", className + "." + methodName);
        }
    }

    /**
     * This method writes an information entry with the given text to the log file.
     * The default and also highly recommended trace level is 'info'.
     *
     * @param text the text to be written to the log file
     */
    public static void writeInfo(String text) {

        // write info with default trace level 'Info'
        writeInfo(text, TraceLevel.Info);
    }

    /**
     * This method writes an information entry with the given text to the log file.
     * The default and also highly recommended trace level is 'info'.
     *
     * @param text the text to be written to the log file
     * @param level the trace level of the log entry to write
     */
    @SuppressWarnings({"WeakerAccess"})
    public static void writeInfo(String text, TraceLevel level) {

        if (isEnabled && TraceOut.level.ordinal() >= level.ordinal()) {
            writeToTrace("INFO ", text);
        }
    }

    /**
     * This method writes an error entry with the given exception to the log file.
     * The default and also highly recommended trace level is 'error'.
     *
     * @param throwable the exception to be written to the log file
     */
    public static void writeException(Throwable throwable) {

        // write exception with default trace level 'error'
        writeException(throwable, TraceLevel.Error);
    }

    /**
     * This method writes an error entry with the given exception to the log file.
     * The default and also highly recommended trace level is 'error'.
     *
     * @param throwable the exception to be written to the log file
     * @param level the trace level of the log entry to write
     */
    @SuppressWarnings({"WeakerAccess"})
    public static void writeException(Throwable throwable, TraceLevel level) {

        if (isEnabled && TraceOut.level.ordinal() >= level.ordinal()) {
            writeToTrace("ERROR", throwable.toString());
        }
    }

    @SuppressWarnings({"EmptyCatchBlock"})
    private static void writeToTrace(String messageType, String text) {

        if (TraceOut.mode == TraceMode.FilePerDay && date.isEqual(LocalDate.now())) {

            try {
                initOutputStream(TraceOut.traceFilePath);
            } catch (Exception ex) {
                // nothing to do here ...
            }
        }

        // get UTC timestamp
        String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // get process id
        String processId = ManagementFactory.getRuntimeMXBean().getName();

        // this code is better but requires Java 9
        //long processId = ProcessHandle.current().pid();

        // write message to trace
        out.println(timestamp + "\t\t" + processId + "\t" + messageType + "\t" + text);
        out.flush();
    }

}
