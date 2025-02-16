package mike.autototem.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class OptOutPacket implements CustomPayload {
    public static final CustomPayload.Id<OptOutPacket> ID = new CustomPayload.Id<>(Identifier.tryParse("autototem-fabric"));

    public static final PacketCodec<RegistryByteBuf, OptOutPacket> CODEC = new PacketCodec<>() {
        @Override
        public void encode(RegistryByteBuf buffer, OptOutPacket optOutPacket) {
            Unpooled.buffer();
        }

        @Override
        public @NotNull OptOutPacket decode(RegistryByteBuf buffer) {
            return new OptOutPacket();
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
