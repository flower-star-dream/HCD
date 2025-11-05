package top.flowerstardream.hcd.user.biz.service;

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
     * @param id
     * @return 当前登录用户信息
     */
    AdminEO getInfo(Long id);

    /**
     * 登出
     * @param token
     */
    void logout(String token);
}
