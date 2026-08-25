#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Lint the generated PneumaticCraft pressure-tube gallery."""

from __future__ import annotations

import json
from pathlib import Path
import re
import sys

sys.dont_write_bytecode = True
import cases
import generate


ROOT = Path(__file__).resolve().parent


def main() -> int:
    for relative, payload in generate.generated_files().items():
        path = ROOT / relative
        if not path.is_file() or path.read_bytes() != payload:
            raise ValueError(f"generated file differs: {relative}")

    json.loads((ROOT / "datapack/pack.mcmeta").read_text(encoding="utf-8"))
    load_tag = json.loads(
        (ROOT / "datapack/data/minecraft/tags/function/load.json").read_text(
            encoding="utf-8"
        )
    )
    if load_tag != {"values": [f"{cases.NAMESPACE}:load"]}:
        raise ValueError("load tag differs from the exact namespace")

    coordinates = {(row.x, row.y, row.z) for row in cases.PLACEMENTS}
    if len(coordinates) != len(cases.PLACEMENTS):
        raise ValueError("gallery placement coordinates overlap")
    minimum_x, minimum_y, minimum_z, maximum_x, maximum_y, maximum_z = (
        cases.ENVELOPE
    )
    if any(
        not (
            minimum_x <= row.x <= maximum_x
            and minimum_y <= row.y <= maximum_y
            and minimum_z <= row.z <= maximum_z
        )
        for row in cases.PLACEMENTS
    ):
        raise ValueError("gallery placement escaped its bounded envelope")

    for block_id in cases.TUBE_IDS:
        matching = [
            row
            for row in cases.PLACEMENTS
            if row.block_state == f"pneumaticcraft:{block_id}"
        ]
        if len(matching) != 6:
            raise ValueError(
                f"tube cross and isolated coverage differs for {block_id}"
            )
    stock = [row for row in cases.PLACEMENTS if row.case_id == "stock-control"]
    if len(stock) != 1 or stock[0].block_state != "minecraft:stone":
        raise ValueError("gallery needs one honest stone stock control")

    function_root = ROOT / f"datapack/data/{cases.NAMESPACE}/function"
    functions = "\n".join(
        path.read_text(encoding="utf-8")
        for path in sorted(function_root.glob("*.mcfunction"))
    )
    expected_setblocks = len(cases.PLACEMENTS) * 2 + sum(
        command.startswith("setblock ")
        for command in cases.SPECIAL_BUILD_COMMANDS
    )
    if len(re.findall(r"^setblock ", functions, re.MULTILINE)) != expected_setblocks:
        raise ValueError("build function setblock count differs")
    for required in (
        "vertical-basic",
        "vertical-reinforced",
        "vertical-advanced",
        "mixed-tier-line",
    ):
        if required not in functions:
            raise ValueError(f"special topology case missing: {required}")
    lowered = functions.lower()
    for forbidden in ("summon ", "data merge", "op ", "deop ", "stop "):
        if forbidden in lowered:
            raise ValueError(f"forbidden gallery command: {forbidden}")
    print(
        f"PneumaticCraft gallery lint passed: {len(cases.PLACEMENTS)} anchors, "
        "3 tube crosses, 3 vertical lines, 1 mixed-tier line"
    )
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as error:
        print(f"gallery lint failed: {error}", file=sys.stderr)
        raise SystemExit(1)
