package pers.apong.lefriend.model.enums;

public enum Gender {
    /**
     * 男
     */
    MALE(0, "男"),
    /**
     * 女
     */
    FEMALE(1, "女"),
    ;
    private int value;
    private String text;

    Gender(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static int getValueByText(String text) {
        for (Gender gender : Gender.values()) {
            if (gender.getText().equals(text)) {
                return gender.getValue();
            }
        }
        return -1;
    }
}
