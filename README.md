# BlueMap PneumaticCraft: Repressurized Add-on

A Java 21 BlueMap add-on for the exact `pneumaticcraft-8.2.23-mc1.21.1` profile in All the Mons
`1.2.0` / Minecraft `1.21.1`.

Status: owner-accepted `0.1.0-alpha.1` release candidate. After exact artifact
admission, it rebuilds all three pressure-tube families from PneumaticCraft's
installed models and textures, including isolated tubes and horizontal,
vertical, and mixed-tier tube connections.

## Build

```bash
gradle --no-daemon -PbluemapSourcePath=../bluemap-backport clean check build
```

`check` is the quick Java/checkstyle/archive gate. `prototypeCheck` additionally
requires every exact candidate JAR property and validates the generated
gallery. See `provenance/upstreams.json` for immutable artifact identities and
the [execution guide](docs/EXECUTION.md) for the prototype-to-release loop.

## Install

Place the production JAR in BlueMap's add-on pack directory and restart the
BlueMap JVM. Removal plus one restart restores stock behavior; the add-on
creates no custom world state.

Set `-Dbluemap.pneumaticcraft.disabled=true` to leave the exact profile inactive.

## Scope boundary

The implemented pass covers `pressure_tube`, `reinforced_pressure_tube`, and
`advanced_pressure_tube`. It reconstructs installed disconnected and connected
pieces for tube-to-tube neighbors across all three tiers. Closed sides, tube
modules, camouflage, machine-port capabilities, pressure tint, particles, and
animation remain outside this pass. Malformed resources or unsupported
artifact profiles leave the add-on inactive.

No PneumaticCraft: Repressurized binary, source, class, asset, captured mesh, or gallery is
bundled in the add-on.
