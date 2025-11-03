package top.flowerstardream.hcd.tools.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.flowerstardream.hcd.tools.interfaces.IdGenerator;
import top.flowerstardream.hcd.tools.properties.IdGeneratorProperties;

import java.util.UUID;

import static top.flowerstardream.hcd.tools.exception.ExceptionEnum.*;

@Component
public class SnowflakeIdGenerator implements IdGenerator {

    private final long twepoch;
    private final long workerId;
    private final long workerIdBits;
    private final long sequenceBits;

    private final long maxWorkerId;
    private final long workerIdShift;
    private final long timestampLeftShift;
    private final long sequenceMask;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    @Autowired
    public SnowflakeIdGenerator(IdGeneratorProperties idGeneratorProperties) {
        this.twepoch = idGeneratorProperties.getTwepoch();
        this.workerId = idGeneratorProperties.getWorkerId();
        this.workerIdBits = idGeneratorProperties.getWorkerIdBits();
        this.sequenceBits = idGeneratorProperties.getSequenceBits();

        this.maxWorkerId = ~(-1L << workerIdBits);
        this.workerIdShift = sequenceBits;
        this.timestampLeftShift = sequenceBits + workerIdBits;
        this.sequenceMask = ~(-1L << sequenceBits);

        if (workerId > maxWorkerId || workerId < 0) {
            WORKER_ID_OUT_OF_BOUNDS.throwException();
        }
    }

    @Override
    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            CLOCK_ROLLED_BACK.throwException();
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}
