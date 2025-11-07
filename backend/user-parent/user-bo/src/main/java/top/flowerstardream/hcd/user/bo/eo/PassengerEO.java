package top.flowerstardream.hcd.user.bo.eo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import top.flowerstardream.hcd.base.bo.eo.BaseEO;

/**
 * @Author: 花海
 * @Date: 2025/11/06/20:41
 * @Description: 乘客人实体对象
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@TableName("hcd_passenger")
public class PassengerEO extends BaseEO {

    /** 真实姓名 */
    @TableField("real_name")
    private String realName ;

    /** 证件类型 */
    @TableField("card_type")
    private String cardType ;

    /** 身份证 */
    @TableField("id_card")
    private String idCard ;
}
