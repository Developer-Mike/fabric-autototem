package mike.autototem.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;

@Mixin(GameRenderer.class)
public class OnTotemPopMixin {
    private ArrayList<Packet<?>> packetsToSend = new ArrayList<>();

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo ci) {
        if (packetsToSend.isEmpty())
            return;

        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null)
            return;

        networkHandler.sendPacket(packetsToSend.get(0));
        packetsToSend.remove(0);
    }

    @Inject(at = @At("TAIL"), method = "showFloatingItem")
    private void onTotemUse(ItemStack floatingItem, CallbackInfo ci) {
        if (!floatingItem.isOf(Items.TOTEM_OF_UNDYING))
            return;

        GameRenderer gameRenderer = (GameRenderer) ((Object) this);
        MinecraftClient client = gameRenderer.getClient();
        
        PlayerEntity player = client.player;
        if (player == null)
            return;

        if (!player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))
            return;
        if (!player.hasStatusEffect(StatusEffects.REGENERATION))
            return;

        int spareTotemSlot = getSlotWithSpareTotem(player.getInventory());
        if (spareTotemSlot == -1) {
            System.out.println("No Spare Totem Found");
            return;
        }

        System.out.println("Restocking Totem");
        restockSlot(player, spareTotemSlot);
    }

    private int getSlotWithSpareTotem(PlayerInventory inventory) {
        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack stack = inventory.main.get(i);

            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                return i;
            }
        }

        return -1;
    }

    private void restockSlot(PlayerEntity player, int totemSlot) {
        PlayerInventory playerInventory = player.getInventory();
        packetsToSend = new ArrayList<>();

        if (totemSlot < 9) {
            // Select Totem Slot
            packetsToSend.add(new UpdateSelectedSlotC2SPacket(totemSlot));

            // Move Totem To Offhand
            packetsToSend.add(new PlayerActionC2SPacket(
              PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
              BlockPos.ORIGIN, 
              Direction.DOWN
            ));

            // Restore Old Hotbar Slot
            packetsToSend.add(new UpdateSelectedSlotC2SPacket(playerInventory.selectedSlot));
        } else {
            // Move Current Hotbar Slot To Offhand
            packetsToSend.add(new PlayerActionC2SPacket(
              PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
              BlockPos.ORIGIN, 
              Direction.DOWN
            ));

            // Move Totem To Selected Hotbar Slot
            ScreenHandler screenHandler = player.currentScreenHandler;
            packetsToSend.add(new ClickSlotC2SPacket(
              screenHandler.syncId,
              screenHandler.getRevision(),
              totemSlot,
              0,
              SlotActionType.QUICK_MOVE,
              playerInventory.getStack(playerInventory.selectedSlot).copy(),
              new Int2ObjectOpenHashMap<ItemStack>()
            ));

            // The Hotbar Slot contains the Totem - Move it to the Offhand and restore the old item
            packetsToSend.add(new PlayerActionC2SPacket(
              PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
              BlockPos.ORIGIN, Direction.DOWN
            ));
        }
    }
}
