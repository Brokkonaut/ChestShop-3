package com.Acrobot.ChestShop.Tests;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link com.Acrobot.Breeze.Utils.MaterialUtil}
 *
 * @author Acrobot
 */
@RunWith(JUnit4.class)
public class MaterialTest {

    @Test
    public void testForBlank() {
        ItemStack air = new ItemStack(Material.AIR);

        assertTrue(MaterialUtil.isEmpty(air));
        assertTrue(MaterialUtil.isEmpty(null));
    }

    @Test
    public void testNameCollisions() {
        boolean anyFail = false;
        for (Material m : Material.values()) {
            if (!m.name().startsWith("LEGACY_")) {
                String materialName = MaterialUtil.getSignMaterialName(m, "");
                Material m2 = MaterialUtil.getMaterial(materialName);
                if (m != m2) {
                    System.err.println("Material " + m + " becomes " + m2);
                    anyFail = true;
                }
            }
        }
        if (anyFail) {
            throw new AssertionError();
        }
    }
}
