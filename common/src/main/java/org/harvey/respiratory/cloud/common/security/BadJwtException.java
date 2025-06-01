package org.harvey.respiratory.cloud.common.security;

import org.harvey.respiratory.cloud.common.exception.UnauthorizedException;

/**
 * BadJwt的异常
 *
 * @author <a href="mailto:harvey.blocks@outlook.com">Harvey Blocks</a>
 * @version 1.0
 * @date 2025-05-27 14:50
 */
public class BadJwtException extends UnauthorizedException {
    public BadJwtException() {
        super();
    }

    public BadJwtException(String message) {
        super(message);
    }

    public BadJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
