package top.flowerstardream.hcd.user.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import top.flowerstardream.hcd.base.constant.StatusConstant;
import top.flowerstardream.hcd.tools.constant.JwtClaimsConstant;
import top.flowerstardream.hcd.tools.properties.JwtProperties;
import top.flowerstardream.hcd.tools.properties.WeChatProperties;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.tools.utils.HttpClientUtil;
import top.flowerstardream.hcd.tools.utils.JwtUtil;
import top.flowerstardream.hcd.user.ao.req.UserInfoREQ;
import top.flowerstardream.hcd.user.ao.req.UserPageQueryREQ;
import top.flowerstardream.hcd.user.ao.req.WechatLoginREQ;
import top.flowerstardream.hcd.user.ao.res.WxLoginRES;
import top.flowerstardream.hcd.user.biz.mapper.UserMapper;
import top.flowerstardream.hcd.user.biz.service.IUserService;
import top.flowerstardream.hcd.user.bo.eo.UserEO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static top.flowerstardream.hcd.base.constant.RedisPrefixConstant.USER_TOKEN_PREFIX;
import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.user.constant.UserExceptionEnum.*;
import static top.flowerstardream.hcd.user.constant.UserRedisPrefixConstant.USER_INFO_CACHE_NAME;

/**
 * 用户服务实现类
 *
 * @author 花海
 * @date 2025-11-15
 */
@Slf4j
@Service
public class IUserServiceImpl extends ServiceImpl<UserMapper, UserEO> implements IUserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Resource
    private UserMapper userMapper;

    @Resource
    private WeChatProperties weChatProperties;

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    @Lazy
    private IUserServiceImpl self;

    @Override
    public WxLoginRES wechatLogin(WechatLoginREQ loginREQ) {
        // 1. 调用微信接口服务，获取当前微信用户的openid
        String openid = getOpenId(loginREQ.getCode());
        // 判断openid是否为空，为空则表示登录失败，抛出业务异常
        if (openid == null) {
            OPENID_CANNOT_BE_EMPTY.throwException();
        }
        // 2. 根据openid查询用户信息，判断用户是否为新用户
        UserEO user = userMapper.getByOpenId(openid);
        // 如果是新用户则创建用户信息，自动完成注册，否则直接返回用户信息
        if (user == null) {
            user = UserEO.builder()
                    .openid(openid)
                    .status(StatusConstant.ENABLE)
                    .build();
            userMapper.insert(user);
        }

        //3 返回数据  jwt  employee
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        claims.put(JwtClaimsConstant.USER_NAME, user.getUsername());
        String token = JwtUtil.getToken(
                jwtProperties.getEmployeeSecretKey(),
                jwtProperties.getEmployeeTtl(),
                claims);

        //4 保存token至redis
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String redisKey = USER_TOKEN_PREFIX + user.getId();
        operations.set(redisKey, token, jwtProperties.getUserRefreshTime(), TimeUnit.SECONDS);
        
        // 5. 返回登录响应
        return WxLoginRES.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .username(user.getUsername())
                .token(token)
                .build();
    }

    @Override
    @Cacheable(cacheNames = USER_INFO_CACHE_NAME, key = "#ids", unless = "#ids == null")
    public List<UserEO> getUserInfo(List<Long> ids) {
        return self.listByIds(ids);
    }

    @Override
    public void updateUserInfo(UserInfoREQ userInfoREQ) {
        UserEO user = new UserEO();
        BeanUtils.copyProperties(userInfoREQ, user);
        boolean update = updateById(user);
        if (!update) {
            log.error("更新当前登录用户信息失败：{}", user);
            MODIFICATION_FAILED.throwException();
        }
    }

    @Override
    public PageResult<UserEO> list(UserPageQueryREQ queryREQ) {
        // 构建分页对象
        Page<UserEO> page = new Page<>(queryREQ.getPage(), queryREQ.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<UserEO> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按用户名模糊查询
        if (StrUtil.isNotBlank(queryREQ.getUsername())) {
            queryWrapper.like(UserEO::getUsername, queryREQ.getUsername());
        }
        
        // 按手机号查询
        if (StrUtil.isNotBlank(queryREQ.getPhone())) {
            queryWrapper.eq(UserEO::getPhone, queryREQ.getPhone());
        }
        
        // 执行查询
        Page<UserEO> resultPage = userMapper.selectPage(page, queryWrapper);
        
        // 封装分页结果
        PageResult<UserEO> pageResult = new PageResult<>();
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setRecords(resultPage.getRecords());
        
        return pageResult;
    }

    @Override
    public void updateUserStatus(Integer status, Long userId) {
        UserEO user = UserEO.builder()
                .id(userId)
                .status(status)
                .build();
        boolean update = updateById(user);
        if (!update) {
            log.error("更新状态失败：{}", user);
            MODIFICATION_FAILED.throwException();
        }
    }

    /**
     * 调取微信接口服务，获取微信用户的openid
     * @param code
     * @return
     */
    private String getOpenId(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }
}
