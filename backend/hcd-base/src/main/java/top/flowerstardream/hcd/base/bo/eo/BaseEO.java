package top.flowerstardream.hcd.base.bo.eo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 花海
 * @Date: 2025/10/15/11:03
 * @Description: 基础实体类
 */
public class BaseEO {
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    protected Long id;
    @TableField("createTime")
    protected LocalDateTime createTime;
    @TableField("updateTime")
    protected LocalDateTime updateTime;
    @TableField("createPerson")
    protected String createPerson;
    @TableField("updatePerson")
    protected String updatePerson;
}
