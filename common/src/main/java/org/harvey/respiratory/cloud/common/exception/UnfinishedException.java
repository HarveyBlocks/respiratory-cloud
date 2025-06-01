package org.harvey.respiratory.cloud.common.exception;

/**
 * 未完成的异常
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-12 23:42
 */
public class UnfinishedException extends ServerException {
    public UnfinishedException(Object... destroy) {
        super();
    }

    public UnfinishedException(String message) {
        super(message);
    }

    public UnfinishedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnfinishedException(Throwable cause) {
        super(cause);
    }

    protected UnfinishedException(
            String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
