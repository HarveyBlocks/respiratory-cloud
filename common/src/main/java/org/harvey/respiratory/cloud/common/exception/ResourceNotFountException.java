package org.harvey.respiratory.cloud.common.exception;

/**
 * 404的HttpException
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-06-02 13:56
 */
public class ResourceNotFountException extends BadRequestException {
    public ResourceNotFountException() {
        super();
    }

    public ResourceNotFountException(String message) {
        super(message);
    }

    public ResourceNotFountException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFountException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getCode() {
        return 404;
    }
}
