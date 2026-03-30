package mike.autototem.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.network.HashedStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(GameRenderer.class)
public class OnTotemPopMixin {
    private ArrayList<Object> packetsToSend = new ArrayList<>();

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo ci) {
        if (packetsToSend.isEmpty())
            return;

        ClientPacketListener packetListener = Minecraft.getInstance().getConnection();
        if (packetListener == null)
            return;

        packetListener.getConnection().send((net.minecraft.network.protocol.Packet<?>) packetsToSend.get(0));
        packetsToSend.remove(0);
    }

    @Inject(at = @At("TAIL"), method = "displayItemActivation")
    private void onTotemUse(ItemStack floatingItem, CallbackInfo ci) {
        if (!floatingItem.is(Items.TOTEM_OF_UNDYING))
            return;

        GameRenderer gameRenderer = (GameRenderer) ((Object) this);
        Minecraft client = gameRenderer.getMinecraft();
        
        Player player = client.player;
        if (player == null)
            return;

        if (!player.hasEffect(MobEffects.FIRE_RESISTANCE))
            return;
        if (!player.hasEffect(MobEffects.REGENERATION))
            return;

        int spareTotemSlot = getSlotWithSpareTotem(player.getInventory());
        if (spareTotemSlot == -1) {
            System.out.println("No Spare Totem Found");
            return;
        }

        System.out.println("Restocking Totem");
        restockSlot(client, player, spareTotemSlot);
    }

    private int getSlotWithSpareTotem(Inventory inventory) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);

            if (!stack.isEmpty() && stack.is(Items.TOTEM_OF_UNDYING)) {
                return i;
            }
        }

        return -1;
    }

    private void restockSlot(Minecraft client, Player player, int totemSlot) {
        Inventory inventory = player.getInventory();
        int currentHotbarSlot = inventory.getSelectedSlot();
        packetsToSend = new ArrayList<>();

        if (totemSlot < 9) {
            if (currentHotbarSlot != totemSlot) {
                packetsToSend.add(new ServerboundSetCarriedItemPacket(totemSlot));
            }

            packetsToSend.add(new ServerboundPlayerActionPacket(
              ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND,
              BlockPos.ZERO, 
              Direction.DOWN
            ));

            if (currentHotbarSlot != totemSlot) {
                packetsToSend.add(new ServerboundSetCarriedItemPacket(currentHotbarSlot));
            }
        } else {
            int availableHotbarSlot = inventory.getFreeSlot();
            if (availableHotbarSlot < 9) {
                packetsToSend.add(new ServerboundSetCarriedItemPacket(availableHotbarSlot));
            } else {
                packetsToSend.add(new ServerboundPlayerActionPacket(
                  ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                  BlockPos.ZERO, 
                  Direction.DOWN
                ));
            }

            AbstractContainerMenu menu = player.containerMenu;
            packetsToSend.add(new ServerboundContainerClickPacket(
              menu.containerId,
              menu.getStateId(),
              (short) totemSlot,
              (byte) 0,
              ContainerInput.QUICK_MOVE,
              it.unimi.dsi.fastutil.ints.Int2ObjectMaps.emptyMap(),
              HashedStack.create(inventory.getSelectedItem(), null)
            ));

            packetsToSend.add(new ServerboundPlayerActionPacket(
              ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND,
              BlockPos.ZERO, 
              Direction.DOWN
            ));

            if (availableHotbarSlot < 9) {
                packetsToSend.add(new ServerboundSetCarriedItemPacket(currentHotbarSlot));
            }
        }
    }
}
