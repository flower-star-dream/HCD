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
import top.flowerstardream.hcd.tools.properties.MyGatewayProperties;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * @Author: 花海
 * @Date: 2025/11/04/17:09
 * @Description: JWT 验证工具
 */
@Slf4j
@UtilityClass
public class JwtValidator {
    private static final String BEARER = "Bearer ";

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
                                   StringRedisTemplate redis,
                                   MyGatewayProperties gatewayProp) {
        // 1. 提取并校验 token
        if (isBlank(tokenHeader)) {
            return ValidateResult.builder().valid(false).msg("未认证").build();
        }
        String rawToken = tokenHeader.startsWith(BEARER)
                ? tokenHeader.substring(BEARER.length())
                : tokenHeader;
        if (isBlank(rawToken)) {
            return ValidateResult.builder().valid(false).msg("token不存在").build();
        }

        // 2. 校验 biz_side
        BizSide side;
        try {
            side = BizSide.valueOf(bizSideHeader.toUpperCase());
        } catch (Exception e) {
            return ValidateResult.builder().valid(false).msg("非法biz_side参数").build();
        }

        // 3. Redis 存在性
        String redisKey = gatewayProp.getRedisTokenPrefix() + rawToken;
        String redisToken = redis.opsForValue().get(redisKey);
        if (isBlank(redisToken)) {
            return ValidateResult.builder().valid(false).msg("Token未找到").build();
        }

        // 4. 解析 JWT
        String secret = side == BizSide.ADMIN ? prop.getAdminSecretKey() : prop.getUserSecretKey();
        Claims claims;
        try {
            claims = JwtUtil.getClaimsBody(secret, rawToken);
        } catch (Exception e) {
            log.warn("JWT解析失败", e);
            return ValidateResult.builder().valid(false).msg("Token无效").build();
        }
        int verify = JwtUtil.verifyToken(claims, side == BizSide.ADMIN
                ? prop.getAdminRefreshTime()
                : prop.getUserRefreshTime());
        if (verify != -1 && verify != 0) {
            return ValidateResult.builder().valid(false).msg("Token过期").build();
        }

        // 5. 取 userId
        String idClaim = side == BizSide.ADMIN
                ? JwtClaimsConstant.ADMIN_ID
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

        // 6. 取权限
        String permStr = claims.get(JwtClaimsConstant.PERMISSION_LEVEL) != null
        ? claims.get(JwtClaimsConstant.PERMISSION_LEVEL).toString()
        : null;
        PermissionLevel perm = (permStr != null && !permStr.isEmpty())
                ? PermissionLevel.valueOf(permStr)
                : PermissionLevel.USER;  // 默认最低权限
        return ValidateResult.builder().valid(true).side(side).userId(userId).permissionLevel(perm).token(rawToken).build();
    }
}
