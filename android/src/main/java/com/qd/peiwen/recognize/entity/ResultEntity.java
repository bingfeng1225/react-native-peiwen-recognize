package com.qd.peiwen.recognize.entity;

public class ResultEntity {
    private String sn;
    private String desc;
    private int error = -1;
    private int subError = -1;
    private String resultType;
    private String origalResult;
    private String[] resultsRecognition;

    public ResultEntity() {
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getSubError() {
        return subError;
    }

    public void setSubError(int subError) {
        this.subError = subError;
    }

    public boolean hasError() {
        return error != 0;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getOrigalResult() {
        return origalResult;
    }

    public void setOrigalResult(String origalResult) {
        this.origalResult = origalResult;
    }

    public String[] getResultsRecognition() {
        return resultsRecognition;
    }

    public void setResultsRecognition(String[] resultsRecognition) {
        this.resultsRecognition = resultsRecognition;
    }
}
