package top.flowerstardream.hcd.user.bo.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;


/**
 *
 * @author 花海
 * @date 2025-10-14
 * @Description: 用户实体对象
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_user")
public class UserEO extends BaseEO {

    @TableField("openid")
    private String openid;

    // 用户名
    @TableField("username")
    private String username;

    // 头像
    @TableField("avatar")
    private String avatar;

    // 邮箱
    @TableField("email")
    private String email;

    // 手机号
    @TableField("phone")
    private String phone;

    // 乘车人id
    @TableField("passenger_id")
    private Long passengerId ;

    // 状态
    @TableField("status")
    private Integer status ;

}