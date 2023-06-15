package pro.sky.telegrambotshelter.enums;

public enum ButtonCommands {

    STAGE_0_CATS("st0_cat_shelters"),
    STAGE_0_DOGS("st0_dog_shelters");

    private final String s;
    ButtonCommands(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return this.s;
    }
}
