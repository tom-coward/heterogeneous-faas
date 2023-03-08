package com.tomcoward.heterogeneousfaas.resourcemanager.models;

import java.util.UUID;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

@Entity
public class FunctionExecution {
    @PartitionKey
    @CqlName("id")
    private UUID id;

    @CqlName("function_name")
    private String functionName;
    @CqlName("worker_id")
    private UUID workerId;
    @CqlName("input_size")
    private int inputSize;
    @CqlName("duration")
    private long duration;
    @CqlName("is_success")
    private boolean isSuccess;


    public FunctionExecution() {}

    public FunctionExecution(UUID id, String functionName, UUID workerId, int inputSize, long duration, boolean isSuccess) {
        this.id = id;
        this.functionName = functionName;
        this.workerId = workerId;
        this.inputSize = inputSize;
        this.duration = duration;
        this.isSuccess = isSuccess;
    }

    public FunctionExecution(String functionName, UUID workerId, int inputSize, long duration, boolean isSuccess) {
        this.id = UUID.randomUUID();
        this.functionName = functionName;
        this.workerId = workerId;
        this.inputSize = inputSize;
        this.duration = duration;
        this.isSuccess = isSuccess;
    }


    public UUID getId() {
        return id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public UUID getWorkerID() {
        return workerId;
    }

    public int getInputSize() {
        return inputSize;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isSuccess() {
        return isSuccess;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public void setWorkerId(UUID workerId) {
        this.workerId = workerId;
    }

    public void setInputSize(int inputSize) {
        this.inputSize = inputSize;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}