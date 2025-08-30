package mike.autototem.mixin;

import mike.autototem.packets.OptOutPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ServerOptOut {
    @Inject(at = @At("TAIL"), method = "onGameJoin")
    private void sendInfoPackage(GameJoinS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworking.send(new OptOutPacket());
    }
}
