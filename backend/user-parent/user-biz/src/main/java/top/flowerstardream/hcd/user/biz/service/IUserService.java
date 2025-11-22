package top.flowerstardream.hcd.user.biz.service;

import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.user.ao.req.UserInfoREQ;
import top.flowerstardream.hcd.user.ao.req.UserPageQueryREQ;
import top.flowerstardream.hcd.user.ao.req.WechatLoginREQ;
import top.flowerstardream.hcd.user.ao.res.WxLoginRES;
import top.flowerstardream.hcd.user.bo.eo.UserEO;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author 花海
 * @date 2025-11-15
 */
public interface IUserService {

    /**
     * 微信登录
     *
     * @param loginREQ 微信登录请求
     * @return 登录响应
     */
    WxLoginRES wechatLogin(WechatLoginREQ loginREQ);

    /**
     * 获取当前登录用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    List<UserEO> getUserInfo(List<Long> userId);

    /**
     * 更新用户信息
     *
     * @param userInfoREQ 用户信息请求
     */
    void updateUserInfo(UserInfoREQ userInfoREQ);

    /**
     * 分页查询用户列表（后管端接口使用）
     *
     * @param queryREQ 查询条件
     * @return 用户列表分页结果
     */
    PageResult<UserEO> list(UserPageQueryREQ queryREQ);

    /**
     * 更新用户状态（冻结/解冻）
     *
     * @param status 状态（1-启用，0-禁用）
     * @param userId 用户ID
     */
    void updateUserStatus(Integer status, Long userId);
}
