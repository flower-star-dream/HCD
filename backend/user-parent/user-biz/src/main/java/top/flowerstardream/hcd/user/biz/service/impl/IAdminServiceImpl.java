package top.flowerstardream.hcd.user.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import top.flowerstardream.hcd.tools.context.RequestContext;
import top.flowerstardream.hcd.tools.properties.JwtProperties;
import top.flowerstardream.hcd.tools.properties.MyGatewayProperties;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.utils.JwtUtil;
import top.flowerstardream.hcd.tools.utils.TtlContextHolder;
import top.flowerstardream.hcd.user.ao.req.AdminInfoREQ;
import top.flowerstardream.hcd.user.ao.req.AdminPageQueryREQ;
import top.flowerstardream.hcd.user.ao.req.AdminREQ;
import top.flowerstardream.hcd.user.ao.req.LoginREQ;
import top.flowerstardream.hcd.user.ao.res.LoginRES;
import top.flowerstardream.hcd.user.biz.mapper.AdminMapper;
import top.flowerstardream.hcd.user.biz.service.IAdminService;
import top.flowerstardream.hcd.user.bo.eo.AdminEO;
import top.flowerstardream.hcd.tools.constant.JwtClaimsConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static top.flowerstardream.hcd.tools.constant.StatusConstant.*;
import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.user.constant.DefaultParams.*;

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

    @Resource
    private AdminMapper adminMapper;
    /**
     * 登录
     * @param loginREQ
     * @return 登录成功后的管理员信息
     */
    @Override
    public LoginRES login(LoginREQ loginREQ) {
        //1 根据手机号/用户名查询账号信息
        AdminEO admin = validateAdminIsEmpty(loginREQ.getUsername(), loginREQ.getPhone());

        //2 比对密码
        String password = loginREQ.getPassword();
        String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        if(!pwd.equals(admin.getPassword())){
            USER_PASSWORD_ERROR.throwException();
        }

        //3 返回数据  jwt  admin
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, admin.getId());
        claims.put(JwtClaimsConstant.ADMIN_NAME, admin.getUsername());
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
        AdminEO admin = new AdminEO();
        BeanUtils.copyProperties(adminInfoREQ, admin);
        updateById(admin);
    }

    /**
     * 获取管理员列表
     * @param adminPageQueryREQ
     * @return
     */
    @Override
    public PageResult<AdminEO> list(AdminPageQueryREQ adminPageQueryREQ) {
        // 参数校验
        if (adminPageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        
        // 设置默认值
        if (adminPageQueryREQ.getPage() <= 0) {
            adminPageQueryREQ.setPage(1);
        }
        if (adminPageQueryREQ.getPageSize() <= 0) {
            adminPageQueryREQ.setPageSize(10);
        }
        
        // 构建分页查询条件
        Page<AdminEO> page = new Page<>(adminPageQueryREQ.getPage(), adminPageQueryREQ.getPageSize());
        LambdaQueryWrapper<AdminEO> queryWrapper = Wrappers.lambdaQuery();
        
        // 添加查询条件
        if (StringUtils.isNotBlank(adminPageQueryREQ.getUsername())) {
            queryWrapper.like(AdminEO::getUsername, adminPageQueryREQ.getUsername());
        }
        if (StringUtils.isNotBlank(adminPageQueryREQ.getPhone())) {
            queryWrapper.like(AdminEO::getPhone, adminPageQueryREQ.getPhone());
        }
        if (adminPageQueryREQ.getPermissionLevel() != null) {
            queryWrapper.eq(AdminEO::getPermissionLevel, adminPageQueryREQ.getPermissionLevel());
        }
        
        // 执行分页查询
        page = adminMapper.selectPage(page, queryWrapper);
        
        // 封装返回结果
        PageResult<AdminEO> pageResult = new PageResult<>();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getRecords());
        
        // 返回结果
        return pageResult;
    }

    /**
     * 批量删除管理员
     *
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 批量删除管理员
        removeByIds(ids);
    }

    /**
     * 新增管理员账号
     *
     * @param adminREQ
     */
    @Override
    public void add(AdminREQ adminREQ) {
        validateAdminIsNotEmpty(adminREQ.getUsername(), adminREQ.getPhone());
        String tenantName = TtlContextHolder.get().getTenantName();
        AdminEO admin = new AdminEO();
        BeanUtils.copyProperties(adminREQ, admin);
        admin.setAvatar(Avatar);
        admin.setPassword(DigestUtils.md5DigestAsHex(adminREQ.getPassword().getBytes()));
        admin.setStatus(DISABLE);
        admin.setCreatePerson(tenantName);
        admin.setUpdatePerson(tenantName);
        save(admin);
    }

    /**
     * 修改管理员账号
     *
     * @param adminREQ
     */
    @Override
    public void update(AdminREQ adminREQ) {
        validateAdminIsEmpty(adminREQ.getUsername(), adminREQ.getPhone());
        String tenantName = TtlContextHolder.get().getTenantName();
        AdminEO admin = new AdminEO();
        BeanUtils.copyProperties(adminREQ, admin);
        admin.setUpdatePerson(tenantName);
        updateById(admin);
    }

    /**
     * 通过用户名或手机号查询管理员账号
     * @param username
     * @param phone
     * @return
     */
    private AdminEO getAdmin(String username, String phone) {
        AdminEO admin = null;
        if(StringUtils.isNotBlank(username)){
            admin = getOne(Wrappers.<AdminEO>lambdaQuery().eq(AdminEO::getUsername, username));
        }
        if(StringUtils.isNotBlank(phone)){
            admin = getOne(Wrappers.<AdminEO>lambdaQuery().eq(AdminEO::getPhone, phone));
        }
        return admin;
    }

    /**
     * 验证管理员账号是否为空
     * @param username
     * @param phone
     * @return
     */
    private AdminEO validateAdminIsEmpty(String username, String phone) {
        AdminEO adminEO = getAdmin(username, phone);
        if (adminEO != null) {
            return adminEO;
        }
        USER_NOT_EXIST.throwException(); // 用户不存在
        return null;
    }

    /**
     * 验证管理员账号是否非空
     * @param username
     * @param phone
     * @return
     */
    private void validateAdminIsNotEmpty(String username, String phone) {
        AdminEO adminEO = getAdmin(username, phone);
        if (adminEO == null) {
            return; // 用户不存在
        }
        USER_ALREADY_EXISTS.throwException(); // 用户已存在
    }
}