package fr.octopiastudios.api.utils.menu;

import lombok.Getter;

@Getter
public enum Size {

    ONE_LINE(9),
    TWO_LINES(18),
    THREE_LINES(27),
    FOUR_LINES(36),
    FIVE_LINES(45),
    SIX_LINES(54);

    private final int size;

    Size(int size) {
        this.size = size;
    }

    public static Size autoAsign(int slots) {
        if (slots < 10) {
            return ONE_LINE;
        }
        if (slots < 19) {
            return TWO_LINES;
        }
        if (slots < 28) {
            return THREE_LINES;
        }
        if (slots < 37) {
            return FOUR_LINES;
        }
        if (slots < 46) {
            return FIVE_LINES;
        }
        return SIX_LINES;
    }
}

