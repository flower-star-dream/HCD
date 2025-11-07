package top.flowerstardream.hcd.user.bo.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:00
 * @Description: 管理员实体对象
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_admin")
public class AdminEO extends BaseEO {

    // 用户名
    @TableField("username")
    private String username ;

    // 昵称
    @TableField("nickname")
    private String nickname ;

    // 头像
    @TableField("avatar")
    private String avatar ;

    // 权限等级
    @TableField("permission_level")
    private String permissionLevel ;

    // 手机号
    @TableField("phone")
    private String phone ;

    // 密码
    @TableField("password")
    @JsonIgnore
    private String password ;

    // 所属站点
    @TableField("affiliated_site")
    private String affiliatedSite ;

    @TableField("status")
    private Integer status ;
}