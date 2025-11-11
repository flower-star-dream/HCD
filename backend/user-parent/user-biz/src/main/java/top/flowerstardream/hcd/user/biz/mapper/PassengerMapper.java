package top.flowerstardream.hcd.user.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.flowerstardream.hcd.user.bo.eo.PassengerEO;

/**
 * @Author: 花海
 * @Date: 2025/11/10/22:38
 * @Description: 乘车人Mapper
 */
@Mapper
public interface PassengerMapper extends BaseMapper<PassengerEO> {
}
