package com.alibaba.cloud.ai.example.manus.tool.calculator;

/**
 * 计算器操作枚举
 */
public enum CalculatorCommandEnum {
    ADDITION("addition"),
    SUBTRACTION("subtraction"),
    MULTIPLICATION("multiplication"),
    DIVISION("division");

    private String value;

    CalculatorCommandEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
