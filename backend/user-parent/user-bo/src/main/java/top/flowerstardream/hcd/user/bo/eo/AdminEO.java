package top.flowerstardream.hcd.user.bo.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:00
 * @Description: 管理员实体对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("hcd_admin")
public class AdminEO extends BaseEO {

    // 用户名
    @TableField("username")
    private String username ;

    // 权限等级
    @TableField("permission_level")
    private String permissionLevel ;

    // 手机号
    @TableField("phone")
    private String phone ;

    // 密码
    @TableField("password")
    private String password ;

    // 所属站点
    @TableField("affiliated_site")
    private String affiliatedSite ;
}
