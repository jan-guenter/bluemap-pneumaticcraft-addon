# PneumaticCraft pressure-tube visual gallery

This bounded gallery covers isolated, horizontal-cross, vertical-line, and
mixed-tier topology for all three exact pressure-tube blocks. Four empty tank
shells, one heat-pipe multipart state, and one stone block are stock-rendering
controls. Tube modules, camouflage, pressure, fluids, and animation are not
represented in this first static pass.

Keep the stable commands:

```bash
python gallery/generate.py
python gallery/generate.py --check
python gallery/lint.py
bash gallery/package.sh /tmp/pneumaticcraft-gallery.zip
```

Keep gallery generation deterministic, bounded, synthetic where practical, and
free of candidate assets or captured meshes.
