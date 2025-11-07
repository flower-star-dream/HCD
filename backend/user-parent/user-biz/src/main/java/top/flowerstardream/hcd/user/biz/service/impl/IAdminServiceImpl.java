package top.flowerstardream.hcd.user.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import top.flowerstardream.hcd.tools.context.RequestContext;
import top.flowerstardream.hcd.tools.properties.JwtProperties;
import top.flowerstardream.hcd.tools.properties.MyGatewayProperties;
import top.flowerstardream.hcd.tools.utils.JwtUtil;
import top.flowerstardream.hcd.tools.utils.TtlContextHolder;
import top.flowerstardream.hcd.user.ao.req.AdminInfoREQ;
import top.flowerstardream.hcd.user.ao.req.LoginREQ;
import top.flowerstardream.hcd.user.ao.res.LoginRES;
import top.flowerstardream.hcd.user.biz.mapper.AdminMapper;
import top.flowerstardream.hcd.user.biz.service.IAdminService;
import top.flowerstardream.hcd.user.bo.eo.AdminEO;
import top.flowerstardream.hcd.tools.constant.JwtClaimsConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:57
 * @Description: 后管管理员服务实现类
 */
@Slf4j
@Service
public class IAdminServiceImpl extends ServiceImpl<AdminMapper, AdminEO> implements IAdminService {

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MyGatewayProperties gatewayProp;
    /**
     * 登录
     * @param loginREQ
     * @return 登录成功后的管理员信息
     */
    @Override
    public LoginRES login(LoginREQ loginREQ) {
        //1 根据手机号/用户名查询账号信息
        AdminEO admin = null;
        if(StringUtils.isNotBlank(loginREQ.getUsername())){
            admin = getOne(Wrappers.<AdminEO>lambdaQuery().eq(AdminEO::getUsername, loginREQ.getUsername()));
        }else{
            admin = getOne(Wrappers.<AdminEO>lambdaQuery().eq(AdminEO::getPhone, loginREQ.getPhone()));
        }
        if(admin == null){
            USER_NOT_EXIST.throwException();
        }

        //2 比对密码
        String password = loginREQ.getPassword();
        String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        if(!pwd.equals(admin.getPassword())){
            USER_PASSWORD_ERROR.throwException();
        }

        //3 返回数据  jwt  admin
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, admin.getId());
        claims.put(JwtClaimsConstant.PERMISSION_LEVEL, admin.getPermissionLevel());
        String token = JwtUtil.getToken(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        //4 保存token至redis
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String redisKey = gatewayProp.getRedisTokenPrefix() + token;
        operations.set(redisKey, token, jwtProperties.getAdminTtl(), TimeUnit.SECONDS);

        //5 封装数据
        return LoginRES.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .token(token)
                .build();
    }

    @Override
    public AdminEO getInfo() {
        RequestContext ctx = TtlContextHolder.get();
        Long id = ctx.getTenantId();
        log.info("获取当前登录用户信息：{}", id);
        return getById(id);
    }

    @Override
    public void logout() {
        RequestContext ctx = TtlContextHolder.get();
        String token = ctx.getToken();
        String redisKey = gatewayProp.getRedisTokenPrefix() + token;
        String redisToken = stringRedisTemplate.opsForValue().get(redisKey);
        if (redisToken != null) {
            stringRedisTemplate.delete(redisKey);
        }
    }

    /**
     * 启用或禁用管理员账号
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        AdminEO admin = AdminEO.builder()
                .id(id)
                .status(status)
                .build();
        updateById(admin);
    }

    /**
     * 更新当前登录用户信息
     *
     * @param adminInfoREQ
     */
    @Override
    public void updateInfo(AdminInfoREQ adminInfoREQ) {
        AdminEO admin = AdminEO.builder()
                .id(adminInfoREQ.getId())
                .username(adminInfoREQ.getUsername())
                .nickname(adminInfoREQ.getNickname())
                .phone(adminInfoREQ.getPhone())
                .avatar(adminInfoREQ.getAvatar())
                .affiliatedSite(adminInfoREQ.getAffiliatedSite())
                .permissionLevel(adminInfoREQ.getPermissionLevel())
                .build();
        updateById(admin);
    }
}