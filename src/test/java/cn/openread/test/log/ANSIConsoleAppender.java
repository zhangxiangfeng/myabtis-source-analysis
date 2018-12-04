package cn.openread.test.log;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 日志带颜色
 * https://blog.csdn.net/leiyong0326/article/details/78494115
 *
 * @author simon
 * @create 2018-05-29 下午5:38
 **/
public class ANSIConsoleAppender extends ConsoleAppender {
    private static final String NORMAL = "0";//无  
    private static final String BRIGHT = "1";//高亮
    private static final String UNDER = "4";//下划线,无效  
    private static final String BLINK = "5";//闪烁  
    private static final String REVERSE = "7";//反显  
    private static final String HIDE = "8";//消隐  
    private static final String FOREGROUND_BLACK = "30";
    private static final String FOREGROUND_RED_31 = "31";
    private static final String FOREGROUND_RED_41 = "41";
    private static final String FOREGROUND_GREEN = "32";
    private static final String FOREGROUND_YELLOW = "33";
    private static final String FOREGROUND_BLUE = "34";
    //    紫色
    private static final String FOREGROUND_MAGENTA = "35";
    private static final String FOREGROUND_CYAN = "36";
    private static final String FOREGROUND_WHITE = "37";

    private static final String ONLY_WHITE = "17";

    private static final String PREFIX = "\u001b[";
    private static final String SUFFIX = "m";
    private static final String SEPARATOR = ";";
    private static final String END_COLOUR = PREFIX + SUFFIX;

    private static final String FATAL_COLOUR = getColor(BRIGHT, FOREGROUND_RED_41);
    private static final String ERROR_COLOUR = getColor(BRIGHT, FOREGROUND_RED_31);
    private static final String WARN_COLOUR = getColor(BRIGHT, FOREGROUND_YELLOW);
    private static final String INFO_COLOUR = getColor(NORMAL, FOREGROUND_GREEN);
    private static final String DEBUG_COLOUR = getColor(NORMAL, FOREGROUND_CYAN);
    private static final String TRACE_COLOUR = getColor(NORMAL, FOREGROUND_WHITE);

    private static String getColor(CharSequence... style) {
        String s = PREFIX;
        s += String.join(SEPARATOR, style);
        s += SUFFIX;
        return s;
    }

    /**
     * Wraps the ANSI control characters around the
     * output from the super-class Appender.
     */
    @Override
    protected void subAppend(LoggingEvent event) {
        this.qw.write(getColour(event.getLevel()));
        super.subAppend(event);
        this.qw.write(END_COLOUR);

        if (this.immediateFlush) {
            this.qw.flush();
        }
    }

    /**
     * Get the appropriate control characters to change
     * the colour for the specified logging level.
     */
    private String getColour(Level level) {
        switch (level.toInt()) {
            case Priority.FATAL_INT:
                return FATAL_COLOUR;
            case Priority.ERROR_INT:
                return ERROR_COLOUR;
            case Priority.WARN_INT:
                return WARN_COLOUR;
            case Priority.INFO_INT:
                return INFO_COLOUR;
            case Priority.DEBUG_INT:
                return DEBUG_COLOUR;
            default:
                return TRACE_COLOUR;
        }
    }
}  