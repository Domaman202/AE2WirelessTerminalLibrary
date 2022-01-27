package de.mari_023.ae2wtlib.client;

import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import de.mari_023.ae2wtlib.networking.NetworkingManager;
import de.mari_023.ae2wtlib.networking.c2s.HotkeyPacket;
import de.mari_023.ae2wtlib.networking.s2c.RestockAmountPacket;
import de.mari_023.ae2wtlib.networking.s2c.UpdateRestockPacket;
import de.mari_023.ae2wtlib.networking.s2c.UpdateWUTPackage;
import de.mari_023.ae2wtlib.wat.WATMenu;
import de.mari_023.ae2wtlib.wat.WATScreen;
import de.mari_023.ae2wtlib.wct.WCTMenu;
import de.mari_023.ae2wtlib.wct.WCTScreen;
import de.mari_023.ae2wtlib.wet.WETMenu;
import de.mari_023.ae2wtlib.wet.WETScreen;

import appeng.api.IAEAddonEntrypoint;
import appeng.init.client.InitScreens;

@Environment(EnvType.CLIENT)
public class AE2wtlibclient implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        InitScreens.register(WCTMenu.TYPE, WCTScreen::new, "/screens/wtlib/wireless_crafting_terminal.json");
        InitScreens.register(WETMenu.TYPE, WETScreen::new, "/screens/wtlib/wireless_pattern_encoding_terminal.json");
        InitScreens.register(WATMenu.TYPE, WATScreen::new, "/screens/pattern_access_terminal.json");

        NetworkingManager.registerClientBoundPacket(UpdateWUTPackage.NAME, UpdateWUTPackage::new);
        NetworkingManager.registerClientBoundPacket(UpdateRestockPacket.NAME, UpdateRestockPacket::new);
        NetworkingManager.registerClientBoundPacket(RestockAmountPacket.NAME, RestockAmountPacket::new);
        registerKeybindings();
    }

    public static void registerKeybindings() {
        KeyMapping wct = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.wct",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping wpt = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.wpt",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping wit = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.wit",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping toggleRestock = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.toggleRestock",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));
        KeyMapping toggleMagnet = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.ae2wtlib.toggleMagnet",
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            checkKeybindings(wct, "crafting");
            checkKeybindings(wpt, "pattern_encoding");
            checkKeybindings(wit, "pattern_access");
            checkKeybindings(toggleRestock, "toggleRestock");
            checkKeybindings(toggleMagnet, "toggleMagnet");
        });
    }

    private static void checkKeybindings(KeyMapping binding, String type) {
        while (binding.consumeClick()) {
            NetworkingManager.sendToServer(new HotkeyPacket(type));
        }
    }
}
