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
    @CqlName("worker")
    private String worker;
    @CqlName("input_size")
    private int inputSize;
    @CqlName("duration")
    private long duration;
    @CqlName("predicted_duration")
    private long predictedDuration;
    @CqlName("is_success")
    private boolean isSuccess;


    public FunctionExecution() {}

    public FunctionExecution(UUID id, String functionName, String worker, int inputSize, long duration, long predictedDuration, boolean isSuccess) {
        this.id = id;
        this.functionName = functionName;
        this.worker = worker;
        this.inputSize = inputSize;
        this.duration = duration;
        this.predictedDuration = predictedDuration;
        this.isSuccess = isSuccess;
    }

    public FunctionExecution(String functionName, String worker, int inputSize, long duration, long predictedDuration, boolean isSuccess) {
        this.id = UUID.randomUUID();
        this.functionName = functionName;
        this.worker = worker;
        this.inputSize = inputSize;
        this.duration = duration;
        this.predictedDuration = predictedDuration;
        this.isSuccess = isSuccess;
    }


    public UUID getId() {
        return id;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getWorker() {
        return worker;
    }

    public int getInputSize() {
        return inputSize;
    }

    public long getDuration() {
        return duration;
    }

    public long getPredictedDuration() {
        return predictedDuration;
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

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public void setInputSize(int inputSize) {
        this.inputSize = inputSize;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setPredictedDuration(long predictedDuration) {
        this.predictedDuration = predictedDuration;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}