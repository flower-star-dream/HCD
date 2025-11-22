package top.flowerstardream.hcd.user.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import top.flowerstardream.hcd.tools.properties.JwtProperties;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.utils.JwtUtil;
import top.flowerstardream.hcd.user.ao.req.*;
import top.flowerstardream.hcd.user.ao.res.LoginRES;
import top.flowerstardream.hcd.user.biz.mapper.EmployeeMapper;
import top.flowerstardream.hcd.user.biz.service.IEmployeeService;
import top.flowerstardream.hcd.user.bo.eo.EmployeeEO;
import top.flowerstardream.hcd.tools.constant.JwtClaimsConstant;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static top.flowerstardream.hcd.base.constant.RedisPrefixConstant.*;
import static top.flowerstardream.hcd.base.constant.StatusConstant.*;
import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.*;
import static top.flowerstardream.hcd.user.constant.DefaultParams.*;
import static top.flowerstardream.hcd.user.constant.UserRedisPrefixConstant.*;
import static top.flowerstardream.hcd.user.constant.UserExceptionEnum.*;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:57
 * @Description: 后管员工服务实现类
 */
@Slf4j
@Service
public class IEmployeeServiceImpl extends ServiceImpl<EmployeeMapper, EmployeeEO> implements IEmployeeService {

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    @Lazy
    private IEmployeeServiceImpl self;
    /**
     * 登录
     * @param loginREQ
     * @return 登录成功后的员工信息
     */
    @Override
    public LoginRES login(LoginREQ loginREQ) {
        //1 根据手机号/用户名查询账号信息
        EmployeeEO employee = validateEmployeeIsEmpty(loginREQ.getUsername(), loginREQ.getPhone());

        //2 比对密码
        String password = loginREQ.getPassword();
        String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        if(!pwd.equals(employee.getPassword())){
            USER_PASSWORD_ERROR.throwException();
        }
        if(Objects.equals(employee.getStatus(), DISABLE)){
            THE_ACCOUNT_HAS_BEEN_DISABLED.throwException();
        }

        //3 返回数据  jwt  employee
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMPLOYEE_ID, employee.getId());
        claims.put(JwtClaimsConstant.EMPLOYEE_NAME, employee.getUsername());
        claims.put(JwtClaimsConstant.PERMISSION_LEVEL, employee.getPermissionLevel());
        String token = JwtUtil.getToken(
                jwtProperties.getEmployeeSecretKey(),
                jwtProperties.getEmployeeTtl(),
                claims);

        //4 保存token至redis
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String redisKey = EMPLOYEE_TOKEN_PREFIX + employee.getId();
        operations.set(redisKey, token, jwtProperties.getEmployeeTtl(), TimeUnit.SECONDS);

        //5 封装数据
        return LoginRES.builder()
                .id(employee.getId())
                .username(employee.getUsername())
                .token(token)
                .build();
    }

    @Override
    @Cacheable(cacheNames = EMPLOYEE_INFO_CACHE_NAME, key = "#id", unless = "#id == null")
    public EmployeeEO getInfo(Long id) {
        log.info("获取当前登录用户信息：{}", id);
        return self.getById(id);
    }

    @Override
    public void logout() {
        Long id = getTenantId(); // 获取当前登录用户id
        String redisKey = EMPLOYEE_TOKEN_PREFIX + id;
        String redisToken = stringRedisTemplate.opsForValue().get(redisKey);
        if (redisToken != null) {
            stringRedisTemplate.delete(redisKey);
        }
    }

    /**
     * 启用或禁用员工账号
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        EmployeeEO employee = EmployeeEO.builder()
                .id(id)
                .status(status)
                .build();
        boolean update = updateById(employee);
        if (!update) {
            log.error("更新状态失败：{}", employee);
            MODIFICATION_FAILED.throwException();
        }
    }

    /**
     * 更新当前登录用户信息
     *
     * @param employeeInfoREQ
     */
    @Override
    public void updateInfo(EmployeeInfoREQ employeeInfoREQ) {
        EmployeeEO employee = new EmployeeEO();
        BeanUtils.copyProperties(employeeInfoREQ, employee);
        boolean update = updateById(employee);
        if (!update) {
            log.error("更新当前登录用户信息失败：{}", employee);
            MODIFICATION_FAILED.throwException();
        }
    }

    /**
     * 获取员工列表
     * @param employeePageQueryREQ
     * @return
     */
    @Override
    public PageResult<EmployeeEO> list(EmployeePageQueryREQ employeePageQueryREQ) {
        // 参数校验
        if (employeePageQueryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        // 设置默认值
        if (employeePageQueryREQ.getPage() <= 0) {
            employeePageQueryREQ.setPage(1);
        }
        if (employeePageQueryREQ.getPageSize() <= 0) {
            employeePageQueryREQ.setPageSize(10);
        }

        // 构建分页查询条件
        Page<EmployeeEO> page = new Page<>(employeePageQueryREQ.getPage(), employeePageQueryREQ.getPageSize());
        LambdaQueryWrapper<EmployeeEO> queryWrapper = Wrappers.lambdaQuery();

        // 添加查询条件
        if (StringUtils.isNotBlank(employeePageQueryREQ.getUsername())) {
            queryWrapper.like(EmployeeEO::getUsername, employeePageQueryREQ.getUsername());
        }
        if (StringUtils.isNotBlank(employeePageQueryREQ.getPhone())) {
            queryWrapper.like(EmployeeEO::getPhone, employeePageQueryREQ.getPhone());
        }
        if (StringUtils.isNotBlank(employeePageQueryREQ.getPermissionLevel())) {
            queryWrapper.eq(EmployeeEO::getPermissionLevel, employeePageQueryREQ.getPermissionLevel());
        }

        // 执行分页查询
        IPage<EmployeeEO> employeePage = employeeMapper.selectPage(page, queryWrapper);

        // 封装返回结果
        PageResult<EmployeeEO> pageResult = new PageResult<>();
        pageResult.setTotal(employeePage.getTotal());
        pageResult.setRecords(employeePage.getRecords());

        // 返回结果
        return pageResult;
    }

    /**
     * 批量删除员工
     *
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        ids.forEach(id -> {
            // 获取员工信息
            EmployeeEO employee = self.getById(id);
            if (employee == null) {
                return;
            }
            if (Objects.equals(employee.getStatus(), ENABLE)) {
                USER_STATUS_ENABLE.throwException();
            }
        });
        // 批量删除员工
        boolean delete = self.removeByIds(ids);
        if (!delete) {
            log.error("批量删除员工失败：{}", ids);
            DELETION_FAILED.throwException();
        }
    }

    /**
     * 新增员工账号
     *
     * @param employeeREQ
     */
    @Override
    public void add(EmployeeREQ employeeREQ) {
        validateEmployeeIsNotEmpty(employeeREQ.getUsername(), employeeREQ.getPhone());
        EmployeeEO employee = new EmployeeEO();
        BeanUtils.copyProperties(employeeREQ, employee);
        if (employeeREQ.getId() != null) {
            employee.setId(null);
        }
        employee.setAvatar(Avatar);
        employee.setPassword(DigestUtils.md5DigestAsHex(employeeREQ.getPassword().getBytes()));
        boolean save = save(employee);
        if (!save) {
            log.error("新增员工账号失败：{}", employee);
            INSERTION_FAILED.throwException();
        }
    }

    /**
     * 修改员工账号
     *
     * @param employeeREQ
     */
    @Override
    public void update(EmployeeREQ employeeREQ) {
        validateEmployeeIsEmpty(employeeREQ.getUsername(), employeeREQ.getPhone());
        log.info("更新当前登录用户id：{}", employeeREQ.getId());
        EmployeeEO employee = new EmployeeEO();
        BeanUtils.copyProperties(employeeREQ, employee);
        if (employeeREQ.getPassword() != null) {
            employee.setPassword(DigestUtils.md5DigestAsHex(employeeREQ.getPassword().getBytes()));
        }
        log.info("更新当前登录用户信息：{}", employee);
        boolean update = updateById(employee);
        if (!update) {
            log.error("更新员工账号失败：{}", employee);
            MODIFICATION_FAILED.throwException();
        }
    }

    /**
     * 重置密码
     *
     * @param resetPwdREQ
     */
    @Override
    public void resetPassword(ResetPwdREQ resetPwdREQ) {
        if (resetPwdREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }
        EmployeeEO employee = getById(resetPwdREQ.getId());
        if (employee == null) {
            USER_NOT_EXIST.throwException();
        }
        if (!Objects.equals(DigestUtils.md5DigestAsHex(resetPwdREQ.getOldPwd().getBytes()), employee.getPassword())) {
            THE_OLD_PASSWORD_IS_INCORRECT.throwException();
        }
        if (Objects.equals(resetPwdREQ.getOldPwd(), resetPwdREQ.getNewPwd())) {
            THE_OLD_PASSWORD_CANNOT_EQUALS_NEW_ONE.throwException();
        }
        if (!Objects.equals(resetPwdREQ.getNewPwd(), resetPwdREQ.getConfirmPwd())){
            THE_NEW_PASSWORD_IS_INCONSISTENT_WITH_THE_CONFIRMED_PASSWORD.throwException();
        }
        EmployeeEO newEmployee = EmployeeEO.builder()
                .id(resetPwdREQ.getId())
                .password(DigestUtils.md5DigestAsHex(resetPwdREQ.getNewPwd().getBytes()))
                .build();
        boolean update = updateById(newEmployee);
        if (!update) {
            log.error("更新密码失败：{}", employee);
            MODIFICATION_FAILED.throwException();
        }
    }

    /**
     * 通过用户名或手机号查询员工账号
     * @param username
     * @param phone
     * @return
     */
    private EmployeeEO getEmployee(String username, String phone) {
        EmployeeEO employee = null;
        if(StringUtils.isNotBlank(username)){
            employee = getOne(Wrappers.<EmployeeEO>lambdaQuery().eq(EmployeeEO::getUsername, username));
        }
        if(StringUtils.isNotBlank(phone)){
            employee = getOne(Wrappers.<EmployeeEO>lambdaQuery().eq(EmployeeEO::getPhone, phone));
        }
        return employee;
    }

    /**
     * 验证员工账号是否为空
     * @param username
     * @param phone
     * @return
     */
    private EmployeeEO validateEmployeeIsEmpty(String username, String phone) {
        EmployeeEO employeeEO = getEmployee(username, phone);
        if (employeeEO != null) {
            return employeeEO;
        }
        USER_NOT_EXIST.throwException(); // 用户不存在
        return null;
    }

    /**
     * 验证员工账号是否非空
     * @param username
     * @param phone
     * @return
     */
    private void validateEmployeeIsNotEmpty(String username, String phone) {
        EmployeeEO employeeEO = getEmployee(username, phone);
        if (employeeEO == null) {
            return; // 用户不存在
        }
        USER_ALREADY_EXISTS.throwException(); // 用户已存在
    }
}