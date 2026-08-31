/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.pneumaticcraft.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.ResourcePath;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.util.Direction;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.util.math.Axis;
import de.bluecolored.bluemap.core.util.math.VectorM3f;

/** Rotates the installed upward-facing tube parts onto each cube side. */
final class TubeRotation {

    private static final int[] QUARTER_TURNS = {0, 90, 180, 270};
    private static final ResourcePath<Model> PROBE_MODEL =
            new ResourcePath<>("bluemap_pneumaticcraft:block/rotation_probe");

    private TubeRotation() {
    }

    static Variant toward(Key model, Direction side) {
        Direction secondary = side.getAxis() == Axis.Y
                ? Direction.NORTH : Direction.UP;
        for (int x : QUARTER_TURNS) {
            for (int y : QUARTER_TURNS) {
                for (int z : QUARTER_TURNS) {
                    Variant candidate = new Variant(
                            new ResourcePath<Model>(model), x, y, z
                    );
                    if (maps(candidate, Direction.UP, side)
                            && maps(candidate, Direction.NORTH, secondary)) {
                        return candidate;
                    }
                }
            }
        }
        throw new IllegalArgumentException("no cube rotation for " + side);
    }

    private static boolean maps(Variant variant, Direction source, Direction target) {
        VectorM3f transformed = new VectorM3f(0F, 0F, 0F)
                .set(source.toVector())
                .rotateAndScale(variant.getTransformMatrix());
        return Math.abs(transformed.x - target.toVector().getX()) < 0.01F
                && Math.abs(transformed.y - target.toVector().getY()) < 0.01F
                && Math.abs(transformed.z - target.toVector().getZ()) < 0.01F;
    }
}
