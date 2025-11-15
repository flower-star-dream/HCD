package top.flowerstardream.hcd.tools.utils;

import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.flowerstardream.hcd.base.constant.PermissionLevel;
import top.flowerstardream.hcd.tools.constant.JwtClaimsConstant;
import top.flowerstardream.hcd.base.constant.BizSide;
import top.flowerstardream.hcd.tools.properties.JwtProperties;

import static org.apache.commons.lang3.StringUtils.*;
import static top.flowerstardream.hcd.base.constant.CommonConstant.TOKEN_HEADER;
import static top.flowerstardream.hcd.base.constant.RedisPrefixConstant.*;

/**
 * @Author: 花海
 * @Date: 2025/11/04/17:09
 * @Description: JWT 验证工具
 */
@Slf4j
@UtilityClass
public class JwtValidator {

    @Data
    @Builder
    public static class ValidateResult {
        boolean valid;
        BizSide side;
        Long userId;
        String msg;
        PermissionLevel permissionLevel;
        String token;
    }

    public ValidateResult validate(String tokenHeader,
                                   String bizSideHeader,
                                   JwtProperties prop,
                                   StringRedisTemplate redis) {
        // 1. 提取并校验 token
        if (isBlank(tokenHeader)) {
            return ValidateResult.builder().valid(false).msg("未认证").build();
        }
        String rawToken = tokenHeader.startsWith(TOKEN_HEADER)
                ? tokenHeader.substring(TOKEN_HEADER.length())
                : tokenHeader;
        if (isBlank(rawToken)) {
            return ValidateResult.builder().valid(false).msg("token不存在").build();
        }

        // 2. 校验 X-Biz-Side
        BizSide side;
        try {
            side = BizSide.valueOf(bizSideHeader.toUpperCase());
        } catch (Exception e) {
            return ValidateResult.builder().valid(false).msg("非法X-Biz-Side参数").build();
        }

        // 3. 解析 JWT
        String secret = side == BizSide.ADMIN ? prop.getEmployeeSecretKey() : prop.getUserSecretKey();
        Claims claims;
        try {
            claims = JwtUtil.getClaimsBody(secret, rawToken);
        } catch (Exception e) {
            log.warn("JWT解析失败", e);
            return ValidateResult.builder().valid(false).msg("Token无效").build();
        }
        int verify = JwtUtil.verifyToken(claims, side == BizSide.ADMIN
                ? prop.getEmployeeRefreshTime()
                : prop.getUserRefreshTime());
        if (verify != -1 && verify != 0) {
            return ValidateResult.builder().valid(false).msg("Token过期").build();
        }

        // 4. 取 userId
        String idClaim = side == BizSide.ADMIN
                ? JwtClaimsConstant.EMPLOYEE_ID
                : JwtClaimsConstant.USER_ID;
        Long userId = null;
        if (claims != null) {
            Object userIdObj = claims.get(idClaim);
            if (userIdObj != null) {
                if (userIdObj instanceof Long) {
                    userId = (Long) userIdObj;
                } else if (userIdObj instanceof Integer) {
                    userId = ((Integer) userIdObj).longValue();
                } else {
                    userId = Long.valueOf(userIdObj.toString());
                }
            }
        } else {
            return ValidateResult.builder().valid(false).msg("Token无效").build();
        }

        // 5. Redis 存在性
        String tokenPrefix = side == BizSide.ADMIN
                ? EMPLOYEE_TOKEN_PREFIX
                : USER_TOKEN_PREFIX;
        String redisKey = tokenPrefix + userId;
        String redisToken = redis.opsForValue().get(redisKey);
        if (isBlank(redisToken)) {
            return ValidateResult.builder().valid(false).msg("Token未找到").build();
        }

        // 6. 取权限
        String permStr = claims.get(JwtClaimsConstant.PERMISSION_LEVEL) != null
        ? claims.get(JwtClaimsConstant.PERMISSION_LEVEL).toString()
        : null;
        PermissionLevel perm = (permStr != null && !permStr.isEmpty())
                ? PermissionLevel.getByPermission(permStr)
                : PermissionLevel.USER;  // 默认最低权限
        return ValidateResult.builder().valid(true).side(side).userId(userId).permissionLevel(perm).token(rawToken).build();
    }
}
