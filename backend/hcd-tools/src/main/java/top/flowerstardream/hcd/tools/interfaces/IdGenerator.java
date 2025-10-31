package top.flowerstardream.hcd.tools.interfaces;

import java.util.UUID;

public interface IdGenerator {
    long nextId();  // 生成 BIGINT 类型的 ID
    UUID generateId();  // 生成 UUID 类型的 ID
}

