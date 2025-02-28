package res;

import java.awt.Color;

public enum Colors {
    DARKBLUE(54, 116, 181),
    MIDDLEBLUE(87, 143, 202),
    LIGHTBLUE(161, 227, 249),
    WHITEBLUE(209, 248, 239);

    private final Color awtColor;

    Colors(int r, int g, int b) {
        this.awtColor = new Color(r, g, b);
    }

    public Color getAwtColor() {
        return awtColor;
    }
}
