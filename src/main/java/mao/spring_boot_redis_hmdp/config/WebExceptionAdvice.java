package mao.spring_boot_redis_hmdp.config;


import lombok.extern.slf4j.Slf4j;
import mao.spring_boot_redis_hmdp.dto.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice
{

    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e)
    {
        log.error(e.toString(), e);
        return Result.fail("服务器异常");
    }
}
