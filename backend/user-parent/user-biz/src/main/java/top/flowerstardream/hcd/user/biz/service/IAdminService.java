package top.flowerstardream.hcd.user.biz.service;

import top.flowerstardream.hcd.user.ao.req.AdminInfoREQ;
import top.flowerstardream.hcd.user.ao.req.LoginREQ;
import top.flowerstardream.hcd.user.ao.res.LoginRES;
import top.flowerstardream.hcd.user.bo.eo.AdminEO;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:56
 * @Description: 后管管理员服务
 */
public interface IAdminService {

    /**
     * 登录
     *
     * @param loginREQ
     * @return 登录成功后的管理员信息
     */
    LoginRES login(LoginREQ loginREQ);

    /**
     * 获取当前登录用户信息
     * @return 当前登录用户信息
     */
    AdminEO getInfo();

    /**
     * 登出
     */
    void logout();

    /**
     * 启用或禁用管理员账号
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 更新当前登录用户信息
     * @param adminInfoREQ
     */
    void updateInfo(AdminInfoREQ adminInfoREQ);
}
