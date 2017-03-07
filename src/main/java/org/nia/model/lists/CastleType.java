package org.nia.model.lists;

import org.nia.strings.Emoji;

/**
 * @author IANazarov
 */
public enum CastleType {
    RED(1, "Красный замок", Emoji.RED_FLAG),
    BLUE(2, "Синий замок", Emoji.BLUE_FLAG),
    WHITE(3, "Белый замок", Emoji.WHITE_FLAG),
    YELLOW(4, "Желтый замок", Emoji.YELLOW_FLAG),
    BLACK(5, "Черный замок", Emoji.BLACK_FLAG),
    FOREST(6, "Лесной форт", Emoji.FOREST),
    MOUNTAIN(7, "Горный форт", Emoji.MOUNTAIN);
    private int publicID;
    private String name;
    private Emoji emoji;

    CastleType(int publicID, String name, Emoji emoji) {
        this.publicID = publicID;
        this.name = name;
        this.emoji = emoji;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public static CastleType getByEmoji(String emoji) {
        for (CastleType ct : values()) {
            if (ct.getEmoji().toString().equals(emoji)) {
                return ct;
            }
        }
        return null;
    }

    public static CastleType getByName(String name) {
        for (CastleType ct : values()) {
            if (ct.getName().equals(name)) {
                return ct;
            }
        }
        return null;
    }

    public int getPublicID() {
        return publicID;
    }

    public String getName() {
        return name;
    }
}
