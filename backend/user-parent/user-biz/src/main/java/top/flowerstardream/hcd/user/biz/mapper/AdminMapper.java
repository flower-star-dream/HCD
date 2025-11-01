package top.flowerstardream.hcd.user.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.flowerstardream.hcd.user.bo.eo.AdminEO;

/**
 * @Author: 花海
 * @Date: 2025/10/31/17:18
 * @Description: 后管管理员Mapper
 */
@Mapper
public interface AdminMapper extends BaseMapper<AdminEO> {

}
