package top.flowerstardream.hcd.order.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import top.flowerstardream.hcd.order.bo.OrderEO;

import java.time.LocalDateTime;

/**
 * @Author: 花海
 * @Date: 2025/11/11
 * @Description: 订单Mapper接口
 */
public interface OrderMapper extends BaseMapper<OrderEO> {
}