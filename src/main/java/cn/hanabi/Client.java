package cn.hanabi;

import cn.hanabi.events.EventLoop;
import cn.hanabi.events.EventWorldChange;
import cn.hanabi.gui.font.FontLoaders;
import cn.hanabi.utils.fontmanager.FontManager;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import org.lwjgl.opengl.Display;

public class Client {
    public static boolean onDebug = false;

    // Map Must Know
    public static boolean map = true;

    public static WorldClient worldChange;
    public static boolean isGameInit = false;

    public static float pitch;

    public static void onGameLoop() {
        isGameInit = true;
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (worldChange == null) {
            worldChange = world;
            return;
        }

        if (world == null) {
            worldChange = null;
            return;
        }

        if (worldChange != world) {
            worldChange = world;
            EventManager.call(new EventWorldChange());
        }

        EventManager.call(new EventLoop());
    }

    public static void makeSense() {
        Display.setTitle(Hanabi.CLIENT_NAME + " " + Hanabi.CLIENT_VERSION);
        Hanabi.INSTANCE.fontManager = new FontManager();
        Hanabi.INSTANCE.fontManager.initFonts();
    }

    public static void doLogin() {
        new Hanabi();
    }
}

