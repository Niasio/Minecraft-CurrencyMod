package gunn.modcurrency.mod.client.container;

import gunn.modcurrency.mod.client.container.slots.SlotCustomizable;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.tile.TileATM;
import gunn.modcurrency.mod.client.util.INBTInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-16
 */
public class ContainerATM extends Container implements INBTInventory{
    //Slot Index's
    //0-35 = Player Inventory's
    //36 = Te Slot

    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INV_ROW_COUNT = 3;
    private final int PLAYER_INV_COLUMN_COUNT = 9;
    private final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT * PLAYER_INV_ROW_COUNT;
    private final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;
    private final int TE_SLOT = PLAYER_TOTAL_COUNT;

    private final int PLAYER_FIRST_SLOT_INDEX = 0;

    private TileATM tile;

    public ContainerATM(EntityPlayer player, TileATM te){
        tile = te;

        setupPlayerInv(player.inventory);
        setupTeInv();
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        tile.voidPlayerUsing();
        super.onContainerClosed(playerIn);
    }

    private void setupPlayerInv(InventoryPlayer invPlayer){
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 168;

        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++)
            addSlotToContainer(new Slot(invPlayer, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));

        final int PLAYER_INV_XPOS = 8;
        final int PLAYER_INV_YPOS = 110;

        for (int y = 0; y < PLAYER_INV_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INV_COLUMN_COUNT; x++) {
                int slotNum = HOTBAR_SLOT_COUNT + y * PLAYER_INV_COLUMN_COUNT + x;
                int xpos = PLAYER_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(invPlayer, slotNum, xpos, ypos));
            }
        }
    }

    private void setupTeInv() {
        IItemHandler itemHandler = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        addSlotToContainer(new SlotCustomizable(itemHandler, 0, 80, 54, ModItems.itemBanknote));


    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack sourceStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            if (slot.getStack().getItem() == ModItems.itemBanknote) {
                ItemStack copyStack = slot.getStack();
                sourceStack = copyStack.copy();

                if (index < PLAYER_TOTAL_COUNT) {
                    if (!this.mergeItemStack(copyStack, TE_SLOT, TE_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index == TE_SLOT) {
                    if (!this.mergeItemStack(copyStack, PLAYER_FIRST_SLOT_INDEX, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else return ItemStack.EMPTY;

                if (copyStack.getCount() == 0) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }
            }
        }
        return sourceStack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

}
