package top.flowerstardream.hcd.user.biz.service;

import top.flowerstardream.hcd.user.ao.req.LoginREQ;
import top.flowerstardream.hcd.user.ao.res.LoginRES;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:56
 * @Description: 后管管理员服务
 */
public interface IAdminService {

    /**
     * 登录
     *
     * @param loginRES
     * @return 登录成功后的管理员信息
     */
    LoginRES login(LoginREQ loginRES);
}
