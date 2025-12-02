package top.flowerstardream.hcd.user.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.flowerstardream.hcd.user.bo.eo.EmployeeEO;

import java.util.List;
import java.util.Map;

/**
 * @Author: 花海
 * @Date: 2025/10/31/17:18
 * @Description: 后管员工Mapper
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<EmployeeEO> {

    @Select("select status, count(*) as count from hcd_employee group by status")
    List<Map<String, Object>> count();
}
