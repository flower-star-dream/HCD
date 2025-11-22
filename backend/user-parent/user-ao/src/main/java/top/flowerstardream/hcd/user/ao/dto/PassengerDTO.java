package top.flowerstardream.hcd.user.ao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 乘车人数据传输对象
 *
 * @author 花海
 * @date 2025-11-10
 */
@Data
@Schema(description = "乘车人数据传输对象")
public class PassengerDTO implements Serializable {

    @Schema(description = "乘车人ID")
    private Long id;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "证件类型")
    private String cardType;

    @Schema(description = "身份证号")
    private String idCard;
}