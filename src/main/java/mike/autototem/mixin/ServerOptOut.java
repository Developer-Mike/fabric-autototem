package mike.autototem.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ServerOptOut {
    @Inject(at = @At("TAIL"), method = "onGameJoin")
    private void sendInfoPackage(GameJoinS2CPacket packet, CallbackInfo ci) {
        ClientPlayNetworkHandler networkHandler = (ClientPlayNetworkHandler) ((Object) this);

        networkHandler.sendPacket(new CustomPayloadC2SPacket(
                // Use a custom channel for compatibility with Sonar
                // https://github.com/Developer-Mike/Autototem-Fabric/pull/15
                new UnknownCustomPayload(Identifier.of("autototem-fabric"))));
    }
}
