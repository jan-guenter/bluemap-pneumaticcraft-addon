#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Bounded PneumaticCraft pressure-tube comparison gallery."""

from __future__ import annotations

from dataclasses import dataclass


NAMESPACE = "pneumaticcraft_gallery"
ENVELOPE = (172, 99, 172, 206, 104, 206)


@dataclass(frozen=True)
class Placement:
    case_id: str
    label: str
    x: int
    y: int
    z: int
    block_state: str
    expected: str


TUBE_IDS = (
    "pressure_tube",
    "reinforced_pressure_tube",
    "advanced_pressure_tube",
)


def build_placements() -> tuple[Placement, ...]:
    placements: list[Placement] = []
    for index, block_id in enumerate(TUBE_IDS):
        center_x = 177 + index * 11
        center_z = 183
        for suffix, dx, dz in (
            ("center", 0, 0),
            ("west", -1, 0),
            ("east", 1, 0),
            ("north", 0, -1),
            ("south", 0, 1),
        ):
            placements.append(
                Placement(
                    f"cross-{block_id}-{suffix}",
                    f"{block_id} horizontal cross",
                    center_x + dx,
                    100,
                    center_z + dz,
                    f"pneumaticcraft:{block_id}",
                    "tube-topology-visible",
                )
            )
        placements.append(
            Placement(
                f"isolated-{block_id}",
                f"{block_id} isolated",
                center_x,
                100,
                190,
                f"pneumaticcraft:{block_id}",
                "tube-core-visible",
            )
        )

    for index, block_id in enumerate(
        ("small_tank", "medium_tank", "large_tank", "huge_tank")
    ):
        placements.append(
            Placement(
                f"tank-{block_id}",
                f"{block_id} empty structural control",
                175 + index * 4,
                100,
                200,
                f"pneumaticcraft:{block_id}",
                "stock-resource-visible",
            )
        )

    placements.extend(
        (
            Placement(
                "heat-pipe-control",
                "heat pipe stock multipart control",
                194,
                100,
                200,
                "pneumaticcraft:heat_pipe[east=true,west=true]",
                "stock-resource-visible",
            ),
            Placement(
                "stock-control",
                "stone stock rendering control",
                202,
                100,
                200,
                "minecraft:stone",
                "stock-visible",
            ),
        )
    )
    return tuple(placements)


PLACEMENTS = build_placements()


SPECIAL_BUILD_COMMANDS = (
    "# vertical-basic: basic tube vertical connection",
    "setblock 176 100 195 pneumaticcraft:pressure_tube",
    "setblock 176 101 195 pneumaticcraft:pressure_tube",
    "setblock 176 102 195 pneumaticcraft:pressure_tube",
    "# vertical-reinforced: reinforced tube vertical connection",
    "setblock 184 100 195 pneumaticcraft:reinforced_pressure_tube",
    "setblock 184 101 195 pneumaticcraft:reinforced_pressure_tube",
    "setblock 184 102 195 pneumaticcraft:reinforced_pressure_tube",
    "# vertical-advanced: advanced tube vertical connection",
    "setblock 192 100 195 pneumaticcraft:advanced_pressure_tube",
    "setblock 192 101 195 pneumaticcraft:advanced_pressure_tube",
    "setblock 192 102 195 pneumaticcraft:advanced_pressure_tube",
    "# mixed-tier-line: every exact tube tier connects",
    "setblock 198 100 195 pneumaticcraft:pressure_tube",
    "setblock 199 100 195 pneumaticcraft:reinforced_pressure_tube",
    "setblock 200 100 195 pneumaticcraft:advanced_pressure_tube",
)

SPECIAL_VERIFY_COMMANDS = tuple(
    f"execute unless block {x} {y} {z} pneumaticcraft:{block_id} run tellraw @a "
    f'{{"text":"gallery mismatch: {case_id}","color":"red"}}'
    for case_id, x, y, z, block_id in (
        ("vertical-basic-bottom", 176, 100, 195, "pressure_tube"),
        ("vertical-basic-top", 176, 102, 195, "pressure_tube"),
        ("vertical-reinforced-bottom", 184, 100, 195, "reinforced_pressure_tube"),
        ("vertical-reinforced-top", 184, 102, 195, "reinforced_pressure_tube"),
        ("vertical-advanced-bottom", 192, 100, 195, "advanced_pressure_tube"),
        ("vertical-advanced-top", 192, 102, 195, "advanced_pressure_tube"),
        ("mixed-basic", 198, 100, 195, "pressure_tube"),
        ("mixed-reinforced", 199, 100, 195, "reinforced_pressure_tube"),
        ("mixed-advanced", 200, 100, 195, "advanced_pressure_tube"),
    )
)
