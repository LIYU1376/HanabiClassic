package cn.hanabi.modules.modules.combat;


import cn.hanabi.events.*;
import cn.hanabi.injection.interfaces.IC03PacketPlayer;
import cn.hanabi.modules.Category;
import cn.hanabi.modules.Mod;
import cn.hanabi.modules.ModManager;
import cn.hanabi.utils.game.BlockUtils;
import cn.hanabi.utils.game.MoveUtils;
import cn.hanabi.utils.game.PlayerUtil;
import cn.hanabi.utils.math.TimeHelper;
import cn.hanabi.value.Value;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Criticals extends Mod {
    public static boolean isReadyToCritical = false;
    public static Value<String> modes = new Value<String>("Criticals", "Mode", 0)
            .LoadValue(new String[]{"Packet", "AACv4", "NoGround", "Jump", "Semi"});
    public static Value<String> pmode = new Value<String>("Criticals", "Packet Mode", 0)
            .LoadValue(new String[]{"Minus", "Drop", "Offest", "Old", "Hover", "Rise", "Abuse1", "Abuse2"});
    public static Value<String> smode = new Value<String>("Criticals", "Semi Mode", 0)
            .LoadValue(new String[]{"MatrixSemi", "VulcanSemi"});
    public static Value<Double> hurttime = new Value<>("Criticals", "Hurt Time", 15d, 1d, 20d, 1d);
    public static Value<Double> delay = new Value<>("Criticals", "Delay", 100d, 50d, 800d, 10d);
    public static Value<Double> steptick = new Value<>("Criticals", "Step Timer", 100d, 50d, 500d, 10d);
    public static Value<Double> editv = new Value<>("Criticals", "Edit Value", 0.0625d, -0.0725d, 0.0725d, 0.0025d);

    public static Random random = new Random();
    public static Value<Boolean> noti = new Value<>("Criticals", "Notification", false);
    public static Value<Boolean> prefall = new Value<>("Criticals", "Pre-FDistance", false);
    public static Value<Boolean> keep = new Value<>("Criticals", "Keep Packet", false);


    static TimeHelper stepTimer = new TimeHelper();
    static TimeHelper critTimer = new TimeHelper();

    static double[] y1 = {0.104080378093037, 0.105454222033912, 0.102888018147468, 0.099634532004642};

    private int attacks = 0;


    public Criticals() {
        super("Criticals", Category.COMBAT);
    }


    public static void doCrit() {
        boolean toggled = ModManager.getModule("Criticals").isEnabled();
        boolean notoggled = !ModManager.getModule("Speed").isEnabled() && !ModManager.getModule("Fly").isEnabled() && !ModManager.getModule("Scaffold").isEnabled();

        double[] packet = new double[]{
                0.051 * y1[new Random().nextInt(y1.length)] + ThreadLocalRandom.current().nextDouble(0.005),
                (!MoveUtils.isMoving() ? 0.001 : ThreadLocalRandom.current().nextDouble(0.0003, 0.0005)) + ThreadLocalRandom.current().nextDouble(0.0001),
                0.031 * y1[new Random().nextInt(y1.length)] + ThreadLocalRandom.current().nextDouble(0.001),
                (!MoveUtils.isMoving() ? 0.008 : ThreadLocalRandom.current().nextDouble(0.0001, 0.0007)) + ThreadLocalRandom.current().nextDouble(0.0001)};

        double[] hover = new double[]{
                -0.0091165721 * y1[new Random().nextInt(y1.length)] * 10,
                0.0679999 + mc.thePlayer.ticksExisted % 0.0215,
                0.0176063469198817 * y1[new Random().nextInt(y1.length)] * 10};

        double[] edit = new double[]{0,
                0.075 + ThreadLocalRandom.current().nextDouble(0.008) * (new Random().nextBoolean() ? 0.98 : 0.99) + mc.thePlayer.ticksExisted % 0.0215 * 0.94,
                (new Random().nextBoolean() ? 0.01063469198817 : 0.013999999) * (new Random().nextBoolean() ? 0.98 : 0.99) * y1[new Random().nextInt(y1.length)] * 10};

        double[] test = new double[]{0.06 + ThreadLocalRandom.current().nextDouble(0.0001),
                (0.06 + ThreadLocalRandom.current().nextDouble(0.0001)) / 2,
                (0.06 + ThreadLocalRandom.current().nextDouble(0.0001)) / 4,
                ThreadLocalRandom.current().nextDouble(1.5E-4, 1.63166800276E-4)};

        double[] old = new double[]{0,
                -0.0075,
                (MoveUtils.isMoving() ? 0.00045 : 0.0055) + ThreadLocalRandom.current().nextDouble(0.0001)};

        double[] offest = new double[]{
                -ThreadLocalRandom.current().nextDouble(1.5E-4, 1.63166800276E-4),
                0.011 * y1[new Random().nextInt(y1.length)] + ThreadLocalRandom.current().nextDouble(0.005),
                0.001 + ThreadLocalRandom.current().nextDouble(0.0001),
                1.0E-4,
                (!MoveUtils.isMoving() ? 2.0E-3 : ThreadLocalRandom.current().nextDouble(5.0E-4, 8.0E-4)) + ThreadLocalRandom.current().nextDouble(1.0E-5)};

        double[] morgan = new double[]{
                0.00124 + ThreadLocalRandom.current().nextDouble(1.0E-4, 9.0E-4),
                MoveUtils.isMoving() ? 8.5E-4 : 0.005 + ThreadLocalRandom.current().nextDouble(0.0001)};

        double[] morganfork = new double[]{
                0.012 + ThreadLocalRandom.current().nextDouble(1.0E-4, 9.0E-4),
                (MoveUtils.isMoving() ? 8.5E-4 : 0.005) + ThreadLocalRandom.current().nextDouble(0.001)};



        if (keep.getValue()) {
            double i1 = 0.0319 * y1[new Random().nextInt(y1.length)] + ThreadLocalRandom.current().nextDouble(0.005);
            double i2 = (!MoveUtils.isMoving() ? 0.008 : ThreadLocalRandom.current().nextDouble(0.0001, 0.0007)) + ThreadLocalRandom.current().nextDouble(0.0001);
            //packet
            packet = new double[]{
                    i1,
                    i2,
                    i1,
                    i2};
        }

        if (toggled) {
            //Check If you can take motion
            isReadyToCritical = !isReadyToCritical && (KillAura.target.hurtResistantTime <= Criticals.hurttime.getValue()) && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround && !BlockUtils.isInLiquid() && notoggled;


            //Check the Criticals Timing
            if (!critTimer.isDelayComplete(delay.getValueState()) || !stepTimer.isDelayComplete(steptick.getValue()) && isReadyToCritical) {
                isReadyToCritical = false;
            }

            if (isReadyToCritical) {
                EntityPlayerSP p = mc.thePlayer;
                double[] i = null;
                if (modes.isCurrentMode("Packet")) {
                    switch (pmode.getModeAt(pmode.getCurrentMode())) {
                        case "Minus":
                            i = packet;
                            break;
                        case "Hover":
                            i = hover;
                            break;
                        case "Rise":
                            i = edit;
                            break;
                        case "Drop":
                            i = test;
                            break;
                        case "Old":
                            i = old;
                            break;
                        case "Offest":
                            i = offest;
                            break;
                        case "Abuse1":
                            i = morgan;
                            break;
                        case "Abuse2":
                            i = morganfork;
                    }
                }

                if (i != null) {
                    if (MoveUtils.isOnGround(-1)) {
                        mc.thePlayer.jump();
                    } else {
                        if (prefall.getValue())
                            (mc.thePlayer.sendQueue.getNetworkManager()).sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(p.posX, p.posY + editv.getValue(), p.posZ, true));

                        for (double offset : i) {
                            (mc.thePlayer.sendQueue.getNetworkManager()).sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(p.posX, p.posY + offset, p.posZ, false));
                        }
                    }
                }

                if (noti.getValue())
                    PlayerUtil.tellPlayer("Crit: " + randomNumber(-9999, 9999));

                // Reset Criticals Timer
                critTimer.reset();
            }
        }
    }

    private static int randomNumber(int max, int min) {
        return (int) (Math.random() * (double) (max - min)) + min;
    }

    @EventTarget
    public void onStep(EventStep e) {
        isReadyToCritical = false;
        if (e.getEventType() == EventType.POST) {
            stepTimer.reset();
        }
    }

    @EventTarget
    public void onChangeWorld(EventWorldChange e) {
        stepTimer.reset();
    }


    @EventTarget
    public void onPre(EventPreMotion e) {
        if (modes.isCurrentMode("Packet")) {
            if (BlockUtils.isReallyOnGround() && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock().isFullBlock() && !BlockUtils.isInLiquid() && ModManager.getModule("Speed").isEnabled()) {
                if (ModManager.getModule("KillAura").isEnabled()) {
                    EntityLivingBase entity = KillAura.target;
                    int ht = entity.hurtResistantTime;
                    switch (ht) {
                        case 20: {
                            e.setOnGround(false);
                            e.setY(mc.thePlayer.posY + ThreadLocalRandom.current().nextDouble(0.0099, 0.011921599284565));
                            break;
                        }
                        case 17:
                        case 19: {
                            e.setOnGround(false);
                            e.setY(mc.thePlayer.posY + ThreadLocalRandom.current().nextDouble(1.5E-4, 1.63166800276E-4));
                            break;
                        }
                        case 18: {
                            e.setOnGround(false);
                            e.setY(mc.thePlayer.posY + ThreadLocalRandom.current().nextDouble(0.0019, 0.0091921599284565));
                            break;
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onEnable() {
        attacks = 0;
    }

    @EventTarget
    public void onUpdate(EventUpdate e) {
        this.setDisplayName(modes.getModeAt(modes.getCurrentMode()));
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        Packet packet = event.getPacket();
        if (packet instanceof C03PacketPlayer) {
            IC03PacketPlayer packet1 = (IC03PacketPlayer) packet;
            if (modes.isCurrentMode("NoGround")) {
                packet1.setOnGround(false);
            }
        }
    }

    @EventTarget
    public void onAttack(EventAttack event) {
        if (!mc.thePlayer.onGround || mc.thePlayer.isOnLadder() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava()
                || !(event.entity instanceof EntityLivingBase) || mc.thePlayer.ridingEntity != null)
            return;

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        if (modes.isCurrentMode("Semi")) {
            switch (smode.getModeAt(smode.getCurrentMode())) {
                case "VulcanSemi":
                    attacks++;
                    if (attacks > 6) {
                        sendCriticalPacket(x, 0.2, z, false);
                        sendCriticalPacket(x, 0.1216, z, false);
                        attacks = 0;
                    }
                    break;
                case "MatrixSemi":
                    attacks++;
                    if (attacks > 3) {
                        sendCriticalPacket(x, 0.0825080378093, z, false);
                        sendCriticalPacket(x, 0.023243243674, z, false);
                        sendCriticalPacket(x, 0.0215634532004, z, false);
                        sendCriticalPacket(x, 0.00150000001304, z, false);
                        attacks = 0;
                    }
                    break;
            }
        }

        if (modes.isCurrentMode("Jump"))
            mc.thePlayer.jump();

        if (modes.isCurrentMode("AACv4")) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0000000000000036, z, false));
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
        }
    }

    public void sendCriticalPacket(Double xOffset,Double yOffset,Double zOffset,Boolean ground) {
        double x = mc.thePlayer.posX + xOffset;
        double y = mc.thePlayer.posY + yOffset;
        double z = mc.thePlayer.posZ + zOffset;

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
    }
}
