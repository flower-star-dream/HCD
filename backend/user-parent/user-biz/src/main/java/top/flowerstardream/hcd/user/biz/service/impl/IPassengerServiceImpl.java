package top.flowerstardream.hcd.user.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.flowerstardream.hcd.tools.result.PageResult;
import top.flowerstardream.hcd.user.ao.req.PassengerPageQueryREQ;
import top.flowerstardream.hcd.user.ao.req.PassengerREQ;
import top.flowerstardream.hcd.user.ao.res.PassengerRES;
import top.flowerstardream.hcd.user.biz.mapper.PassengerMapper;
import top.flowerstardream.hcd.user.biz.mapper.UserMapper;
import top.flowerstardream.hcd.user.biz.mapper.UserPassengerMapper;
import top.flowerstardream.hcd.user.biz.service.IPassengerService;
import top.flowerstardream.hcd.user.bo.eo.PassengerEO;
import top.flowerstardream.hcd.user.bo.eo.UserEO;
import top.flowerstardream.hcd.user.bo.eo.UserPassengerEO;

import java.util.List;
import java.util.stream.Collectors;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;
import static top.flowerstardream.hcd.tools.utils.GetInfoUtil.*;
import static top.flowerstardream.hcd.user.constant.UserExceptionEnum.*;

/**
 * @Author: 花海
 * @Date: 2025-11-10
 * @Description: 乘车人服务实现类
 */
@Service
@Slf4j
public class IPassengerServiceImpl extends ServiceImpl<PassengerMapper, PassengerEO> implements IPassengerService {

    @Resource
    private PassengerMapper passengerMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserPassengerMapper userPassengerMapper;

    @Resource
    @Lazy
    private IPassengerServiceImpl self;

    /**
     * 分页查询乘车人列表
     *
     * @param queryREQ 查询条件
     * @return 乘车人列表分页结果
     */
    @Override
    public PageResult<PassengerEO> pageQuery(PassengerPageQueryREQ queryREQ) {
        
        // 参数校验
        if (queryREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        // 设置默认值
        if (queryREQ.getPage() <= 0) {
            queryREQ.setPage(1);
        }
        if (queryREQ.getPageSize() <= 0) {
            queryREQ.setPageSize(10);
        }
        
        // 创建分页对象
        Page<PassengerEO> page = new Page<>(queryREQ.getPage(), queryREQ.getPageSize());
        LambdaQueryWrapper<PassengerEO> queryWrapper = Wrappers.lambdaQuery();
        
        // 真实姓名模糊查询
        if (StringUtils.isNotBlank(queryREQ.getRealName())) {
            queryWrapper.like(PassengerEO::getRealName, queryREQ.getRealName());
        }
        
        // 证件类型精确查询
        if (StringUtils.isNotBlank(queryREQ.getCardType())) {
            queryWrapper.eq(PassengerEO::getCardType, queryREQ.getCardType());
        }
        
        // 身份证号模糊查询
        if (StringUtils.isNotBlank(queryREQ.getIdCard())) {
            queryWrapper.like(PassengerEO::getIdCard, queryREQ.getIdCard());
        }
        
        // 执行分页查询
        IPage<PassengerEO> passengerPage = passengerMapper.selectPage(page, queryWrapper);

        // 封装返回结果
        PageResult<PassengerEO> pageResult = new PageResult<>();
        pageResult.setTotal(passengerPage.getTotal());
        pageResult.setRecords(passengerPage.getRecords());
        return pageResult;
    }

    /**
     * 根据ID查询乘车人详情
     *
     * @param id 乘车人ID
     * @return 乘车人详情
     */
    @Override
    public PassengerEO query(Long id) {
        // 参数校验
        if (id == null || id < 0) {
            PARAM_ERROR.throwException();
        }
        // 根据ID查询乘车人
        return getById(id);
    }

    /**
     * 获取当前用户关联的乘车人列表
     *
     * @param userId 用户ID
     * @return 乘车人列表
     */
    @Override
    public List<PassengerRES> getUserPassengers(Long userId) {
        // 参数校验
        if (userId == null || userId < 0) {
            PARAM_ERROR.throwException();
        }

        // 查询用户关联的乘车人ID列表
        LambdaQueryWrapper<UserPassengerEO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserPassengerEO::getUserId, userId);
        List<UserPassengerEO> userPassengerList = userPassengerMapper.selectList(wrapper);

        // 如果没有关联的乘车人，返回空列表
        if (userPassengerList.isEmpty()) {
            return List.of();
        }

        // 提取乘车人ID列表
        List<Long> passengerIds = userPassengerList.stream()
                .map(UserPassengerEO::getPassengerId)
                .toList();

        // 查询乘车人详情
        List<PassengerEO> passengerList = passengerMapper.selectBatchIds(passengerIds);

        // 转换为DTO
        return passengerList.stream()
                .map(passenger -> {
                    PassengerRES dto = new PassengerRES();
                    BeanUtils.copyProperties(passenger, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 新增乘车人
     *
     * @param userId 用户ID
     * @param passengerREQ 乘车人信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPassenger(Long userId, PassengerREQ passengerREQ) {
        // 参数校验
        if (userId == null || userId <= 0) {
            PARAM_ERROR.throwException();
        }
        if (passengerREQ == null) {
            THE_QUERY_PARAMETER_CANNOT_BE_EMPTY.throwException();
        }

        // 根据身份证号查询是否已存在乘车人
        LambdaQueryWrapper<PassengerEO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(PassengerEO::getIdCard, passengerREQ.getIdCard());
        PassengerEO existingPassenger = passengerMapper.selectOne(queryWrapper);

        Long passengerId;

        if (existingPassenger != null) {
            // 如果已存在乘车人，直接关联
            passengerId = existingPassenger.getId();
        } else {
            // 如果不存在，新增乘车人
            PassengerEO passenger = new PassengerEO();
            BeanUtils.copyProperties(passengerREQ, passenger);
            passengerMapper.insert(passenger);
            passengerId = passenger.getId();
        }

        // 检查是否已关联
        LambdaQueryWrapper<UserPassengerEO> userPassengerWrapper = Wrappers.lambdaQuery();
        userPassengerWrapper.eq(UserPassengerEO::getUserId, userId)
                .eq(UserPassengerEO::getPassengerId, passengerId);
        UserPassengerEO existingUserPassenger = userPassengerMapper.selectOne(userPassengerWrapper);

        if (existingUserPassenger == null) {
            // 创建用户乘车人关联
            UserPassengerEO userPassenger = UserPassengerEO.builder()
                    .userId(userId)
                    .passengerId(passengerId)
                    .build();
            userPassengerMapper.insert(userPassenger);
        }

        // 检查用户的passengerId是否为空，如果为空则更新
        UserEO user = userMapper.selectById(userId);
        if (user != null && user.getPassengerId() == 0) {
            user.setPassengerId(passengerId);
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultPassenger(Long userId, Long passengerId) {
        // 参数校验
        if (userId == null || userId <= 0) {
            PARAM_ERROR.throwException();
        }
        if (passengerId == null || passengerId <= 0) {
            PARAM_ERROR.throwException();
        }

        // 验证乘车人是否存在
        PassengerEO passenger = passengerMapper.selectById(passengerId);
        if (passenger == null) {
            DATA_DOES_NOT_EXIST.throwException();
        }

        // 验证乘车人是否与当前用户关联
        LambdaQueryWrapper<UserPassengerEO> userPassengerWrapper = Wrappers.lambdaQuery();
        userPassengerWrapper.eq(UserPassengerEO::getUserId, userId)
                .eq(UserPassengerEO::getPassengerId, passengerId);
        UserPassengerEO userPassenger = userPassengerMapper.selectOne(userPassengerWrapper);
        if (userPassenger == null) {
            // 乘车人未关联到当前用户
            THIRD_PARTY_DATA_DOES_NOT_EXIST.throwException();
        }

        // 更新用户的默认乘车人ID
        UserEO user = userMapper.selectById(userId);
        if (user == null) {
            USER_NOT_EXIST.throwException();
        }
        user.setPassengerId(passengerId);
        userMapper.updateById(user);
    }
}
