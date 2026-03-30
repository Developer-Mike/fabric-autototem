package mike.autototem.packets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OptOutPacket() implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath("autototem", "opt_out");
    public static final CustomPacketPayload.Type<OptOutPacket> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, OptOutPacket> CODEC = StreamCodec.unit(new OptOutPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
