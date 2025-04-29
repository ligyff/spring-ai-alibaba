package com.alibaba.cloud.ai.example.manus.tool.calculator;

import com.alibaba.cloud.ai.example.manus.tool.ToolCallBiFunctionDef;
import com.alibaba.cloud.ai.example.manus.tool.code.ToolExecuteResult;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ToolContext;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

/**
 * 计算器工具类
 *
 * @author ligengying
 * @version 1.0
 * @date 2023/9/27
 */
public class CalculatorTool implements ToolCallBiFunctionDef {
    private static final Logger log = LoggerFactory.getLogger(CalculatorTool.class);

    //计划id
    private String planId;
    //结果
    private String result;
    //操作数
    private String operand;
    //被操作数
    private String operatedNumber;
    //运算符
    private String command;

    @Override
    public String getServiceGroup() {
        return "default-service-group";
    }

    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "计算器工具类，提供加减乘除算法";
    }

    @Override
    public String getParameters() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "command": {
                            "description": "计算器执行功能, Available commands: ADDITION，SUBTRACTION，MULTIPLICATION，DIVISION",
                                "enum": [
                                    "ADDITION","SUBTRACTION","MULTIPLICATION","DIVISION"
                                ],
                            "type": "string"
                        },
                        "operand": {
                            "description": "操作数，是计算公式的第一个参数",
                            "type": "string"
                        },
                        "operatedNumber": {
                            "description": "被操作数，是计算公式的第二个参数",
                                    "type": "string"
                        },
                    },
                    "required": ["command"，"operand"，"operatedNumber"]
                }
                """;
    }

    @Override
    public Class<?> getInputType() {
        return String.class;
    }

    @Override
    public boolean isReturnDirect() {
        return false;
    }

    @Override
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @Override
    public String getCurrentToolStateString() {
        return String.format("""
                计算器执行:
                - 计算公式: %s %s %s
                - 计算结果: %s
                """, this.operand, this.command, this.operatedNumber, this.result);
    }

    @Override
    public void cleanup(String planId) {

    }

    @Override
    public ToolExecuteResult apply(String toolInput, ToolContext toolContext) {
        return run(toolInput);
    }

    public ToolExecuteResult run(String toolInput) {
        try {
            Map<String, Object> input = JSON.parseObject(toolInput, new TypeReference<Map<String, Object>>() {
            });
            this.command = (String) input.get("command");
            this.operand = (String) input.get("operand");
            this.operatedNumber = (String) input.get("operatedNumber");

            if (Objects.equals(CalculatorCommandEnum.ADDITION.getValue(), command)) {
                this.result = new BigDecimal(operand).add(new BigDecimal(operatedNumber)).toString();
            } else if (Objects.equals(CalculatorCommandEnum.SUBTRACTION.getValue(), command)) {
                this.result = new BigDecimal(operand).subtract(new BigDecimal(operatedNumber)).toString();
            } else if (Objects.equals(CalculatorCommandEnum.MULTIPLICATION.getValue(), command)) {
                this.result = new BigDecimal(operand).multiply(new BigDecimal(operatedNumber)).toString();
            } else if (Objects.equals(CalculatorCommandEnum.DIVISION.getValue(), command)) {
                this.result = new BigDecimal(operand).divide(new BigDecimal(operatedNumber)).toString();
            } else {
                log.info("收到无效的命令: {}", command);
                throw new IllegalArgumentException("Invalid command: " + command);
            }
            return new ToolExecuteResult(this.getCurrentToolStateString());
        } catch (Exception e) {
            log.info("执行计划工具时发生错误", e);
            return new ToolExecuteResult("Error executing planning tool: " + e.getMessage());
        }
    }
}
