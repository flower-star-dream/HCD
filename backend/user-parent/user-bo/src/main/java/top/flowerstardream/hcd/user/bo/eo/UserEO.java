package top.flowerstardream.hcd.user.bo.eo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;

import java.time.LocalDateTime;

/**
 *
 * @author 花海
 * @date 2025-10-14
 * @Description: 用户实体对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("hcd_user")
public class UserEO extends BaseEO {

    // 用户名
    @TableField("username")
    private String username;

    // 邮箱
    @TableField("email")
    private String email;

    // 手机号
    @TableField("phone")
    private String phone;

    // 真实姓名
    @TableField("real_name")
    private String realName;

    // 证件类型
    @TableField("card_type")
    private String cardType;

    // 证件号码
    @TableField("id_card")
    private String idCard;

}