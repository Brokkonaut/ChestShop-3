package com.Acrobot.Breeze.Utils;

import java.util.HashMap;
import org.bukkit.FireworkEffect;

public class FireworkEffectTypeNames {
    private static final HashMap<FireworkEffect.Type, String> typeToName;
    static {
        typeToName = new HashMap<FireworkEffect.Type, String>();
        addType(FireworkEffect.Type.BALL, "Small Ball");
        addType(FireworkEffect.Type.BALL_LARGE, "Large Ball");
        addType(FireworkEffect.Type.BURST, "Burst");
        addType(FireworkEffect.Type.STAR, "Star-shaped");
        addType(FireworkEffect.Type.CREEPER, "Creeper-shaped");
    }

    private static void addType(FireworkEffect.Type type, String name) {
        typeToName.put(type, name);
    }

    public static String getName(FireworkEffect.Type type) {
        if (type == null) {
            return null;
        }
        String name = typeToName.get(type);
        if (name != null) {
            return name;
        }
        return StringUtil.capitalizeFirstLetter(type.name(), '_');
    }
}
