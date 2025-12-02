package top.flowerstardream.hcd.trainSeat.biz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import top.flowerstardream.hcd.trainSeat.bo.SeatReservationEO;

import java.util.List;
import java.util.Map;

@Mapper
public interface SeatReservationMapper extends BaseMapper<SeatReservationEO> {

    void insertBatchSomeColumn(List<SeatReservationEO> seatReservations);

    @Select("select booking_status, count(*) as count from hcd_seat_reservation group by booking_status")
    List<Map<String, Object>> count();
}
