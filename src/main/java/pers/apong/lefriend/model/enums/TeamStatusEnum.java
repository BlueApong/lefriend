package pers.apong.lefriend.model.enums;

import lombok.Getter;

@Getter
public enum TeamStatusEnum {
    /**
     * 公开的
     */
    PUBLIC(0, "公开的"),
    /**
     * 私密的
     */
    PRIVATE(1, "私密的"),
    /**
     * 加密的
     */
    SAFE(2, "加密的");
    private final int value;
    private final String text;

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据值返回枚举
     *
     * @param value
     * @return
     */
    public static TeamStatusEnum getEnumByValue(int value) {
        for (TeamStatusEnum anEnum : TeamStatusEnum.values()) {
            if (anEnum.getValue() == value) {
                return anEnum;
            }
        }
        return null;
    }

    public boolean equalsValue(int value) {
        return this.value == value;
    }
}
