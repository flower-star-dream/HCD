package top.flowerstardream.hcd.tools.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import top.flowerstardream.hcd.tools.interfaces.IdGenerator;

/**
 * @Author: 花海
 * @Date: 2025/10/31/19:00
 * @Description: id生成器插入处理
 */
@Component
@RequiredArgsConstructor
public class MyMetaObjectHandler implements MetaObjectHandler {

    private final IdGenerator idGenerator;
    private final String idFieldName = "id";

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.getValue(idFieldName) == null) {
            // 判断是否存在 id 字段，并根据类型填充
            if (hasFieldOfType(metaObject, Long.class)) {
                long id = idGenerator.nextId();
                this.strictInsertFill(metaObject, idFieldName, Long.class, id);
            } else if (hasFieldOfType(metaObject, String.class)) {
                String id = idGenerator.generateId().toString();
                this.strictInsertFill(metaObject, idFieldName, String.class, id);
            }
        }
    }

    /**
     * 检查指定字段是否存在且为特定类型
     */
    private boolean hasFieldOfType(MetaObject metaObject, Class<?> targetType) {
        try {
            Object value = metaObject.getValue(idFieldName);
            return value == null || targetType.isInstance(value);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时无需处理
    }
}