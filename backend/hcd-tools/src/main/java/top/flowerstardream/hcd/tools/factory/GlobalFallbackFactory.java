package top.flowerstardream.hcd.tools.factory;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import top.flowerstardream.hcd.tools.result.Result;
import top.flowerstardream.hcd.tools.utils.ResultAwareErrorDecoder;

/**
 * @Author: 花海
 * @Date: 2025/11/13/17:00
 * @Description: 全局OpenFeign错误处理工厂类
 */
@Component
public class GlobalFallbackFactory implements FallbackFactory<Object> {

    @Override
    public Object create(Throwable cause) {
        /* 说明是 ResultVoidException，里面已经有 Result */
        if (cause instanceof ResultAwareErrorDecoder.ResultWrappedException ex) {
            return ex.getResult();
        }
        /* 其它异常（网络超时、连接拒绝等）也包成 Result 返回 */
        return Result.error("网络/服务异常");
    }
}