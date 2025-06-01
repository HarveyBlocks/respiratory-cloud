package org.harvey.respiratory.cloud.common.exception;

import lombok.Getter;

/**
 * Dao的异常
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-18 21:41
 */
@Getter
public class DaoException extends ServerException {
    private final Operation operation;

    public DaoException(Operation operation) {
        super();
        this.operation = operation;
    }

    public DaoException(Operation operation, String message) {
        super(message);
        this.operation = operation;
    }

    public DaoException(Operation operation, String message, Throwable cause) {
        super(message, cause);
        this.operation = operation;
    }

    public DaoException(Operation operation, Throwable cause) {
        super(cause);
        this.operation = operation;
    }

    protected DaoException(
            Operation operation,
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.operation = operation;
    }

    public enum Operation {
        SELECT_NOTING, SAVE_FAIL, UPDATE_FAIL, DELETE_FAIL
    }
}
