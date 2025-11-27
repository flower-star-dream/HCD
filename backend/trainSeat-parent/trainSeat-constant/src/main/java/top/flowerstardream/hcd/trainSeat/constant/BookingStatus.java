package top.flowerstardream.hcd.trainSeat.constant;

import java.math.BigDecimal;

public enum BookingStatus {
    NOT_BOOKED(0,"未预订"),
    BOOKED(1,"已预订");




    private final int value;
    private final String description;

    BookingStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
