package gunn.modcurrency.common.core.handler;

import gunn.modcurrency.ModConfig;
import gunn.modcurrency.api.TileBuy;
import gunn.modcurrency.common.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-12-24
 */
public class EventHandlerCommon {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void protectedBreak(PlayerInteractEvent.LeftClickBlock e) {
        Block brokeBlock = e.getWorld().getBlockState(e.getPos()).getBlock();

        //Invincible Machines
        if (ModConfig.invincibleVendSell) {
            cancelEventChange:
            if (brokeBlock == ModBlocks.blockSeller || brokeBlock == ModBlocks.blockVendor || brokeBlock == ModBlocks.blockTop) {
                TileBuy tile = (TileBuy) e.getWorld().getTileEntity(e.getPos());
                if ((brokeBlock == ModBlocks.blockTop && (e.getWorld().getBlockState(e.getPos()).getBlock() != ModBlocks.blockVendor ||
                        e.getWorld().getBlockState(e.getPos()).getBlock() != ModBlocks.blockSeller)))
                    break cancelEventChange;
                if (brokeBlock == ModBlocks.blockTop) tile = (TileBuy) e.getWorld().getTileEntity(e.getPos().down());
                if ((!e.getEntityPlayer().getUniqueID().toString().equals(tile.getOwner())) && !e.getEntityPlayer().isCreative()) {     //If not Owner (and not in creative) Can't Break
                    e.setCanceled(true);
                }
            }
        }
    }
}
