package tools.tracing;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TraceOut {

    private static PrintStream out;
    private static TraceMode mode;
    private static TraceLevel level;

    private static String traceFilePath;
    private static LocalDate date = LocalDate.now();

    @SuppressWarnings("unused")
    public static void enable(String filePath, TraceMode mode, TraceLevel level) throws Exception {

        // init trace level and mode
        TraceOut.mode = mode;
        TraceOut.level = level;
        TraceOut.traceFilePath = filePath;

        initOutputStream(filePath);
    }

    private static void initOutputStream(String filePath) throws Exception {

        if (mode == TraceMode.FilePerDay) {

            filePath = filePath.replace("%YEAR%", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy")));
            filePath = filePath.replace("%MONTH%", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("MM")));
            filePath = filePath.replace("%DAY%", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd")));
        }

        // init output file stream
        boolean append = (TraceOut.mode != TraceMode.Overwrite);
        TraceOut.out = new PrintStream(new FileOutputStream(filePath, append));
    }

    @SuppressWarnings("unused")
    public static PrintStream getOutputStream() {

        return TraceOut.out;
    }

    @SuppressWarnings("unused")
    public static void enter() {

        // write enter with default trace level 'verbose'
        enter(TraceLevel.Verbose);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void enter(TraceLevel level) {

        if (TraceOut.level.ordinal() >= level.ordinal()) {

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

    @SuppressWarnings("unused")
    public static void leave() {

        // write leave with default trace level 'verbose'
        leave(TraceLevel.Verbose);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void leave(TraceLevel level) {

        if (TraceOut.level.ordinal() >= level.ordinal()) {

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

    @SuppressWarnings("unused")
    public static void writeInfo(String text) {

        // write info with default trace level 'Info'
        writeInfo(text, TraceLevel.Info);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void writeInfo(String text, TraceLevel level) {

        if (TraceOut.level.ordinal() >= level.ordinal()) {
            writeToTrace("INFO ", text);
        }
    }

    @SuppressWarnings("unused")
    public static void writeException(Throwable throwable) {

        // write exception with default trace level 'error'
        writeException(throwable, TraceLevel.Error);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void writeException(Throwable throwable, TraceLevel level) {

        if (TraceOut.level.ordinal() >= level.ordinal()) {
            writeToTrace("ERROR", throwable.getMessage());
        }
    }

    @SuppressWarnings({"unused", "EmptyCatchBlock"})
    private static void writeToTrace(String messageType, String text) {

        if (TraceOut.mode == TraceMode.FilePerDay && date.isEqual(LocalDate.now())) {

            try {
                initOutputStream(TraceOut.traceFilePath);
            } catch (Exception ex) { }
        }

        // get UTC timestamp
        String timestamp = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // get process id
        long processId = ProcessHandle.current().pid();

        // write message to trace
        out.println(timestamp + "\t\t" + processId + "\t" + messageType + "\t" + text);
        out.flush();
    }

}
