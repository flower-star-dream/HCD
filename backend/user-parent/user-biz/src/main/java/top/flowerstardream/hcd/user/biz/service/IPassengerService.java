package top.flowerstardream.hcd.user.biz.service;

import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.user.ao.dto.PassengerDTO;
import top.flowerstardream.hcd.user.ao.req.PassengerPageQueryREQ;
import top.flowerstardream.hcd.user.ao.req.PassengerREQ;
import top.flowerstardream.hcd.user.ao.res.PassengerRES;
import top.flowerstardream.hcd.user.bo.eo.PassengerEO;

import java.util.List;

/**
 * @Author: 花海
 * @Date: 2025-11-10
 * @Description: 乘车人服务接口
 */
public interface IPassengerService {

    /**
     * 分页查询乘车人列表
     *
     * @param queryREQ 查询条件
     * @return 乘车人列表分页结果
     */
    PageResult<PassengerEO> pageQuery(PassengerPageQueryREQ queryREQ);

    /**
     * 根据ID查询乘车人详情
     *
     * @param id 乘车人ID
     * @return 乘车人详情
     */
    PassengerEO query(Long id);

    /**
     * 获取当前用户关联的乘车人列表
     *
     * @param userId 用户ID
     * @return 乘车人列表
     */
    List<PassengerRES> getUserPassengers(Long userId);

    /**
     * 新增乘车人
     *
     * @param userId 用户ID
     * @param passengerREQ 乘车人信息
     */
    void addPassenger(Long userId, PassengerREQ passengerREQ);

    /**
     * 设置默认乘车人
     *
     * @param userId 用户ID
     * @param passengerId 乘车人ID
     */
    void setDefaultPassenger(Long userId, Long passengerId);

    /**
     * 根据乘车人姓名获取乘车人列表
     *
     * @param passengerName 乘车人姓名
     * @return 乘车人列表
     */
    List<Long> getPassengersByName(String passengerName);

    /**
     * 根据乘车人ID列表获取乘车人信息列表
     *
     * @param passengerIds 乘车人ID列表
     * @return 乘车人信息列表
     */
    List<PassengerDTO> getPassengersByIds(List<Long> passengerIds);
}
