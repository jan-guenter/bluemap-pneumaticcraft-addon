/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.pneumaticcraft.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;

import java.util.ArrayList;
import java.util.List;

/** Admits only the exact installed pressure-tube model schema. */
final class InstalledPressureTubeResources {

    private InstalledPressureTubeResources() {
    }

    static Admission inspect(ResourcePack pack) {
        List<Variant> routed = new ArrayList<>();
        for (String blockId : PressureTubeCatalog.BLOCK_IDS) {
            de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state =
                    pack.getBlockStates().get(PressureTubeCatalog.blockKey(blockId));
            if (state == null || state.getVariants() == null || state.getMultipart() != null) {
                return null;
            }
            List<Variant> variants = new ArrayList<>();
            state.forEach(variants::add);
            if (variants.size() != 1) {
                return null;
            }
            Variant variant = variants.getFirst();
            if (variant.getRenderer() != BlockRendererType.DEFAULT
                    || !PressureTubeCatalog.loaderModelKey(blockId).equals(variant.getModel())
                    || variant.isTransformed() || variant.isUvlock()) {
                return null;
            }
            for (String part : PressureTubeCatalog.PARTS) {
                if (pack.getModels().get(
                        PressureTubeCatalog.partModelKey(blockId, part)
                ) == null) {
                    return null;
                }
            }
            routed.add(variant);
        }
        return new Admission(List.copyOf(routed));
    }

    static boolean bakedModelsValid(ResourcePack pack) {
        for (String blockId : PressureTubeCatalog.BLOCK_IDS) {
            for (String part : PressureTubeCatalog.PARTS) {
                Model model = pack.getModels().get(
                        PressureTubeCatalog.partModelKey(blockId, part)
                );
                if (model == null || model.getElements() == null
                        || model.getElements().length == 0) {
                    return false;
                }
            }
        }
        return PressureTubeCatalog.textureKeys().stream()
                .allMatch(key -> pack.getTextures().get(key) != null);
    }

    record Admission(List<Variant> variants) {

        Admission {
            variants = List.copyOf(variants);
        }

        boolean route(BlockRendererType renderer) {
            if (variants.stream().anyMatch(
                    variant -> variant.getRenderer() != BlockRendererType.DEFAULT
            )) {
                return false;
            }
            int routed = 0;
            try {
                for (Variant variant : variants) {
                    variant.setRenderer(renderer);
                    routed++;
                }
                return true;
            } catch (RuntimeException exception) {
                for (int index = 0; index < routed; index++) {
                    variants.get(index).setRenderer(BlockRendererType.DEFAULT);
                }
                return false;
            }
        }
    }
}
