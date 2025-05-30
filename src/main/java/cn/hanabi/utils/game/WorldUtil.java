package cn.hanabi.utils.game;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

import java.util.Arrays;
import java.util.List;

/**
 * Created by John on 2017/04/30.
 */
public class WorldUtil {
    public static List<EntityLivingBase> getLivingEntities() {
        return Arrays.asList(
                Minecraft.getMinecraft().theWorld.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityLivingBase)
                        .filter(entity -> entity != Minecraft.getMinecraft().thePlayer)
                        .map(entity -> (EntityLivingBase) entity)
                        .toArray(EntityLivingBase[]::new)
        );
    }

    public static List<Entity> getEntities() {
        return Arrays.asList(
                Minecraft.getMinecraft().theWorld.loadedEntityList.stream()
                        .filter(entity -> entity != Minecraft.getMinecraft().thePlayer)
                        .toArray(Entity[]::new)
        );
    }

    public static List<EntityPlayer> getLivingPlayers() {
        return Arrays.asList(
                Minecraft.getMinecraft().theWorld.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityPlayer)
                        .filter(entity -> entity != Minecraft.getMinecraft().thePlayer)
                        .map(entity -> (EntityPlayer) entity)
                        .toArray(EntityPlayer[]::new)
        );
    }

    public static List<TileEntity> getTileEntities() {
        return Minecraft.getMinecraft().theWorld.loadedTileEntityList;
    }

    public static List<TileEntityChest> getChestTileEntities() {
        return Arrays.asList(
                Minecraft.getMinecraft().theWorld.loadedTileEntityList.stream()
                        .filter(entity -> entity instanceof TileEntityChest)
                        .map(entity -> (TileEntityChest) entity)
                        .toArray(TileEntityChest[]::new)
        );
    }

    public static List<TileEntityEnderChest> getEnderChestTileEntities() {
        return Arrays.asList(
                Minecraft.getMinecraft().theWorld.loadedTileEntityList.stream()
                        .filter(entity -> entity instanceof TileEntityEnderChest)
                        .map(entity -> (TileEntityEnderChest) entity)
                        .toArray(TileEntityEnderChest[]::new)
        );
    }
}
