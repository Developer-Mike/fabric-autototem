package mike.autototem.client;

import mike.autototem.packets.OptOutPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

@Environment(EnvType.CLIENT)
public class AutototemClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playC2S().register(OptOutPacket.ID, OptOutPacket.CODEC);
    }
}
