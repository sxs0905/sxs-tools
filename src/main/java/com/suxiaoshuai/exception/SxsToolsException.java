package com.suxiaoshuai.exception;

/**
 * 工具类库的自定义运行时异常
 * 
 * 继承自 RuntimeException，用于封装工具类库中的异常情况
 */
public class SxsToolsException extends RuntimeException {
    
    /**
     * 构造一个无参的异常对象
     */
    public SxsToolsException() {
    }

    /**
     * 构造一个带有错误信息的异常对象
     *
     * @param message 错误信息
     */
    public SxsToolsException(String message) {
        super(message);
    }

    /**
     * 构造一个带有错误信息和原因的异常对象
     *
     * @param message 错误信息
     * @param cause   异常原因
     */
    public SxsToolsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造一个带有原因的异常对象
     *
     * @param cause 异常原因
     */
    public SxsToolsException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造一个带有完整参数的异常对象
     *
     * @param message            错误信息
     * @param cause             异常原因
     * @param enableSuppression 是否启用异常抑制
     * @param writableStackTrace 是否生成堆栈跟踪
     */
    public SxsToolsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
