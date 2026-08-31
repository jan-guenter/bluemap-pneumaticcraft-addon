/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.pneumaticcraft.adapter.bluemap523;

import de.bluecolored.bluemap.core.util.Key;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Exact installed-resource keys owned by the pressure-tube renderer. */
final class PressureTubeCatalog {

    static final List<String> BLOCK_IDS = List.of(
            "pneumaticcraft:pressure_tube",
            "pneumaticcraft:reinforced_pressure_tube",
            "pneumaticcraft:advanced_pressure_tube"
    );
    static final List<String> PARTS = List.of("disconnected", "connected", "closed");
    private static final Set<String> BLOCK_ID_SET = Set.copyOf(BLOCK_IDS);

    private PressureTubeCatalog() {
    }

    static boolean owns(String blockId) {
        return BLOCK_ID_SET.contains(blockId);
    }

    static Key blockKey(String blockId) {
        return Key.parse(blockId);
    }

    static Key loaderModelKey(String blockId) {
        return Key.parse("pneumaticcraft:block/" + path(blockId));
    }

    static Key partModelKey(String blockId, String part) {
        return Key.parse("pneumaticcraft:block/" + path(blockId) + '_' + part);
    }

    static Set<Key> textureKeys() {
        LinkedHashSet<Key> keys = new LinkedHashSet<>();
        keys.add(Key.parse("pneumaticcraft:pnc_model/pressure_tube"));
        keys.add(Key.parse("pneumaticcraft:pnc_model/reinforced_pressure_tube"));
        keys.add(Key.parse("pneumaticcraft:pnc_model/advanced_pressure_tube"));
        keys.add(Key.parse("pneumaticcraft:pnc_model/tube_closed"));
        return Set.copyOf(keys);
    }

    private static String path(String blockId) {
        int separator = blockId.indexOf(':');
        if (separator < 0 || separator == blockId.length() - 1) {
            throw new IllegalArgumentException("block id has no path");
        }
        return blockId.substring(separator + 1);
    }
}
