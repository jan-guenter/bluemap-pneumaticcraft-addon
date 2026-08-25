/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.pneumaticcraft.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.pneumaticcraft.activation.AddonRuntime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Emits installed tube pieces with stable tube-to-tube neighbor topology. */
final class PressureTubeRenderer implements BlockRenderer {

    private final AddonRuntime runtime;
    private final ResourceModelRenderer resources;
    private final Map<PartKey, Variant> parts = new ConcurrentHashMap<>();
    private final Color partColor = new Color();

    PressureTubeRenderer(
            ResourcePack pack,
            TextureGallery textures,
            RenderSettings settings,
            AddonRuntime runtime
    ) {
        this.runtime = runtime;
        this.resources = new ResourceModelRenderer(pack, textures, settings);
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant fallback,
            TileModelView target,
            Color mapColor
    ) {
        if (!runtime.active()) {
            resources.render(block, fallback, target, mapColor);
            return;
        }

        int start = target.getStart();
        Color initialMapColor = new Color().set(mapColor);
        try {
            BlockState state = block.getBlockState();
            String blockId = state.getId().getFormatted();
            if (!PressureTubeCatalog.owns(blockId) || !isLoaderVariant(blockId, fallback)) {
                resources.render(block, fallback, target, mapColor);
                return;
            }

            mapColor.set(0F, 0F, 0F, 0F, true);
            float opacity = 0F;
            for (Direction direction : Direction.values()) {
                String kind = connects(block, direction) ? "connected" : "disconnected";
                partColor.set(0F, 0F, 0F, 0F, true);
                resources.render(
                        block,
                        part(blockId, kind, direction),
                        target.initialize(),
                        partColor
                );
                opacity = Math.max(opacity, partColor.a);
                mapColor.add(partColor.premultiplied());
            }
            if (mapColor.a > 0F) {
                mapColor.flatten().straight();
                mapColor.a = opacity;
            }
        } catch (MaxCapacityReachedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            target.getTileModel().reset(start);
            target.initialize(start);
            mapColor.set(initialMapColor);
            runtime.inactive("pressure-tube-renderer-"
                    + exception.getClass().getSimpleName());
            resources.render(block, fallback, target, mapColor);
        }
    }

    private Variant part(String blockId, String kind, Direction direction) {
        return parts.computeIfAbsent(
                new PartKey(blockId, kind, direction),
                key -> TubeRotation.toward(
                        PressureTubeCatalog.partModelKey(blockId, kind), direction
                )
        );
    }

    private static boolean connects(BlockNeighborhood block, Direction direction) {
        BlockState neighbor = block.getNeighborBlock(
                direction.toVector().getX(),
                direction.toVector().getY(),
                direction.toVector().getZ()
        ).getBlockState();
        return PressureTubeCatalog.owns(neighbor.getId().getFormatted());
    }

    private static boolean isLoaderVariant(String blockId, Variant variant) {
        return variant.getRenderer() == BlueMap522Adapter.renderer()
                && PressureTubeCatalog.loaderModelKey(blockId).equals(variant.getModel())
                && !variant.isTransformed() && !variant.isUvlock();
    }

    private record PartKey(String blockId, String kind, Direction direction) {
    }
}
