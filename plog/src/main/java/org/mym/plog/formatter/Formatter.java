package org.mym.plog.formatter;

/**
 * This interface defines how to format log msg.
 * Created by muyangmin on Sep 09, 2016.
 *
 * @author muyangmin
 * @since 1.5.0
 */
public interface Formatter {
    /**
     * Format msg with params and return.
     *
     * @param msg    msg to be formatted. If this is null or empty but params provided, params
     *               still should be formatted.
     * @param params params to format msg. maybe empty.
     * @return formatted msg.
     */
    String format(String msg, Object... params) throws Exception;

    /**
     * Indicate whether the result of this formatter is well line-wrapped. <br>
     * NOTE: This return value is important, and if returns true, then line wrap procedure can be
     * omitted to improve performance.
     */
    boolean isPreWrappedFormat();
}
