package top.flowerstardream.hcd.user.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.flowerstardream.hcd.user.bo.eo.UserEO;

/**
 * @Author: 花海
 * @Date: 2025/11/01/01:10
 * @Description: 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEO> {

    /**
     * 根据openid查询用户信息
     *
     * @param openid
     * @return
     */
    @Select("select * from hcd_user where openid = #{openid}")
    UserEO getByOpenId(String openid);
}
