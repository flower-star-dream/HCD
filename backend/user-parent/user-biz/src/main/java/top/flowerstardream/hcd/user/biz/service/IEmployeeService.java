package top.flowerstardream.hcd.user.biz.service;

import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.user.ao.req.*;
import top.flowerstardream.hcd.user.ao.res.LoginRES;
import top.flowerstardream.hcd.user.bo.eo.EmployeeEO;

import java.util.List;

/**
 * @Author: 花海
 * @Date: 2025/10/26/21:56
 * @Description: 员工服务
 */
public interface IEmployeeService {

    /**
     * 登录
     *
     * @param loginREQ
     * @return 登录成功后的员工信息
     */
    LoginRES login(LoginREQ loginREQ);

    /**
     * 获取当前登录用户信息
     * @param id
     * @return 当前登录用户信息
     */
    EmployeeEO getInfo(Long id);

    /**
     * 登出
     */
    void logout();

    /**
     * 启用或禁用员工账号
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 更新当前登录用户信息
     * @param employeeInfoREQ
     */
    void updateInfo(EmployeeInfoREQ employeeInfoREQ);

    /**
     * 获取员工列表
     * @param employeePageQueryREQ
     * @return
     */
    PageResult<EmployeeEO> list(EmployeePageQueryREQ employeePageQueryREQ);

    /**
     * 批量删除员工
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 新增员工账号
     * @param employeeREQ
     */
    void add(EmployeeREQ employeeREQ);

    /**
     * 修改员工账号
     * @param employeeREQ
     */
    void update(EmployeeREQ employeeREQ);

    /**
     * 重置密码
     * @param resetPwdREQ
     */
    void resetPassword(ResetPwdREQ resetPwdREQ);
}
