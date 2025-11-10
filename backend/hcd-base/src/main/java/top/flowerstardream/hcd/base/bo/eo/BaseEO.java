package top.flowerstardream.hcd.base.bo.eo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 花海
 * @Date: 2025/10/15/11:03
 * @Description: 基础实体类
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class BaseEO implements Serializable {
//    @TableId(value = "id", type = IdType.INPUT)
    @TableId(value = "id", type = IdType.ASSIGN_ID) //默认自增
    @TableField(value = "id", fill = FieldFill.INSERT)
    protected Long id;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected LocalDateTime createTime;
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updateTime;
    @TableField(value = "create_person", fill = FieldFill.INSERT)
    protected String createPerson;
    @TableField(value = "update_person", fill = FieldFill.INSERT_UPDATE)
    protected String updatePerson;
}