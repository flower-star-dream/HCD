package top.flowerstardream.hcd.user.biz.service.impl;

import org.springframework.stereotype.Service;
import top.flowerstardream.hcd.user.ao.req.LoginREQ;
import top.flowerstardream.hcd.user.ao.res.LoginRES;
import top.flowerstardream.hcd.user.biz.service.IAdminService;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:57
 * @Description: 后管管理员服务实现类
 */
@Service
public class IAdminServiceImpl implements IAdminService {

    /**
     * 登录
     * @param loginRES
     * @return 登录成功后的管理员信息
     */
    @Override
    public LoginRES login(LoginREQ loginRES) {

        return null;
    }
}
