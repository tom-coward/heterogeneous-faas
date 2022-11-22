package com.tomcoward.heterogeneousfaas.resourcemanager.models;

public class Function {
    private String name;
    private byte[] sourceCode;
    private SourceCodeRuntime sourceCodeRuntime;
    private boolean edgeSupported;
    private boolean cloudAWSSupported;

    public enum SourceCodeRuntime {
        JDK11
    }

    public Function(String name, byte[] sourceCode, SourceCodeRuntime sourceCodeRuntime, boolean edgeSupported, boolean cloudAWSSupported) {
        this.name = name;
        this.sourceCode = sourceCode;
        this.sourceCodeRuntime = sourceCodeRuntime;
        this.edgeSupported = edgeSupported;
        this.cloudAWSSupported = cloudAWSSupported;
    }


    public String getName() {
        return name;
    }

    public byte[] getSourceCode() {
        return sourceCode;
    }

    public SourceCodeRuntime getSourceCodeRuntime() {
        return sourceCodeRuntime;
    }

    public boolean isEdgeSupported() {
        return edgeSupported;
    }

    public boolean isCloudAWSSupported() {
        return cloudAWSSupported;
    }
}
