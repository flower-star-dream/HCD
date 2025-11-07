package top.flowerstardream.hcd.base.bo.eo;

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
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    protected Long id;
    @TableField("create_time")
    protected LocalDateTime createTime;
    @TableField("update_time")
    protected LocalDateTime updateTime;
    @TableField("create_person")
    protected String createPerson;
    @TableField("update_person")
    protected String updatePerson;
}