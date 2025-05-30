package cn.hanabi.injection.mixins;

import cn.hanabi.injection.interfaces.IEntityLivingBase;
import cn.hanabi.modules.ModManager;
import cn.hanabi.modules.modules.movement.NoJumpDelay;
import cn.hanabi.modules.modules.render.HUD;
import cn.hanabi.modules.modules.render.HitAnimation;
import cn.hanabi.utils.client.SoundFxPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase implements IEntityLivingBase {
    @Shadow
    private int jumpTicks;

    @Shadow
    public abstract boolean isPotionActive(Potion potionIn);

    @Shadow
    public abstract PotionEffect getActivePotionEffect(Potion potionIn);

    /**
     * @author
     */
    @Overwrite
    private int getArmSwingAnimationEnd() {
        int speed = this.isPotionActive(Potion.digSpeed) ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
        if ((this.equals(Minecraft.getMinecraft().thePlayer)) && ModManager.getModule("HitAnimation").getState()) {
            speed = (int) (speed * (ModManager.getModule(HitAnimation.class).swingSpeed.getValue()));
        }
        return speed;
    }


    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void headLiving(CallbackInfo callbackInfo) {
        if (ModManager.getModule(NoJumpDelay.class).isEnabled())
            jumpTicks = 0;
    }


      @Inject(method = "handleStatusUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;playSound(Ljava/lang/String;FF)V"), cancellable = true)
       private void handleSound(CallbackInfo callbackInfo) {
          if (!((HUD)ModManager.getModule("HUD")).hitsound.isCurrentMode("Minecraft")) {
              if (((HUD)ModManager.getModule("HUD")).hitsound.isCurrentMode("Ding"))
                  new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Ding, -7);
              else
                  new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Crack, -7);
              callbackInfo.cancel();
          }
       }


    @Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;playSound(Ljava/lang/String;FF)V"), cancellable = true)
    private void handleSound(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!((HUD)ModManager.getModule("HUD")).hitsound.isCurrentMode("Minecraft")) {
            if (((HUD)ModManager.getModule("HUD")).hitsound.isCurrentMode("Ding"))
                new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Ding, -7);
            else
                new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.Crack, -7);
            cir.cancel();
        }
    }


    @Override
    public int runGetArmSwingAnimationEnd() {
        return this.getArmSwingAnimationEnd();
    }

    @Override
    public int getJumpTicks() {
        return jumpTicks;
    }

    @Override
    public void setJumpTicks(int a) {
        jumpTicks = a;
    }

}
