package org.mason.server_back.entity;

public enum FanSpeed {
    LOW(1),      // 低速
    MEDIUM(2),   // 中速
    HIGH(3);     // 高速

    private final int value;

    FanSpeed(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // 新增根据字符串返回对应 FanSpeed 枚举的函数
    public static FanSpeed fromString(String fanSpeed) {
        switch (fanSpeed.toUpperCase()) {
            case "LOW":
                return LOW;
            case "MEDIUM":
                return MEDIUM;
            case "HIGH":
                return HIGH;
            default:
                throw new IllegalArgumentException("Unknown fan speed: " + fanSpeed);
        }
    }
}
