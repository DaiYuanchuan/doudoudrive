package com.doudoudrive.common.global;

import java.io.Serial;

/**
 * <p>自定义的消费者异常类</p>
 * <p>2022-03-10 23:25</p>
 *
 * @author Dan
 **/
public class ConsumeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4093867789628938836L;

    public ConsumeException(String message) {
        super(message);
    }

    public ConsumeException(Throwable cause) {
        super(cause);
    }

    public ConsumeException(String message, Throwable cause) {
        super(message, cause);
    }

}
