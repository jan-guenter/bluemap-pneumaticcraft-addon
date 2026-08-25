/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.pneumaticcraft.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.pneumaticcraft.activation.AddonRuntime;
import io.github.janguenter.bluemap.pneumaticcraft.profile.ExactArtifactDetector;
import io.github.janguenter.bluemap.pneumaticcraft.profile.PneumaticCraft823Profile;

import java.nio.file.Path;
import java.util.Set;

/** Exact-artifact admission and atomic installed-resource routing. */
final class ProfileResourceExtension implements ResourcePackExtension {

    private final ResourcePack resourcePack;
    private final BlockRendererType renderer;
    private final AddonRuntime runtime;
    private InstalledPressureTubeResources.Admission admission;

    ProfileResourceExtension(
            ResourcePack resourcePack,
            BlockRendererType renderer,
            AddonRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.renderer = renderer;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        if (Boolean.getBoolean("bluemap.pneumaticcraft.disabled")) {
            runtime.inactive("operator-disabled");
            return;
        }
        if (!ExactArtifactDetector.matchesAll(roots, PneumaticCraft823Profile.ARTIFACTS)) {
            runtime.inactive("exact-artifact-missing-or-duplicate");
            return;
        }

        admission = InstalledPressureTubeResources.inspect(resourcePack);
        if (admission == null) {
            runtime.inactive("installed-pressure-tube-schema-invalid");
        }
    }

    @Override
    public Set<Key> collectUsedTextureKeys() {
        return admission == null ? Set.of() : PressureTubeCatalog.textureKeys();
    }

    @Override
    public void bake() {
        if (admission == null) {
            return;
        }
        if (!InstalledPressureTubeResources.bakedModelsValid(resourcePack)) {
            admission = null;
            runtime.inactive("installed-pressure-tube-model-invalid");
            return;
        }
        if (!admission.route(renderer)) {
            admission = null;
            runtime.inactive("pressure-tube-routing-collision");
            return;
        }
        runtime.activate();
        System.out.println("BlueMap PneumaticCraft add-on active: 3 pressure tubes.");
    }

    @Override
    public void getBlockProperties(BlockState blockState, BlockProperties.Builder builder) {
        if (runtime.active() && PressureTubeCatalog.owns(
                blockState.getId().getFormatted()
        )) {
            builder.culling(false).occluding(false).cullingIdentical(false);
        }
    }
}
