# Beddium <img src=".idea/icon.png" align="right" width=150>

<sup>**[CurseForge](https://www.curseforge.com/minecraft/mc-mods/beddium) | [Modrinth](https://modrinth.com/mod/beddium)**</sup>

An open-source terrain renderer using the Celeritas Core API

Works best with [FalseTweaks](https://github.com/FalsePattern/FalseTweaks)

A long time coming, a faster, better, more compatible than ever way of quickly drawing chunks on your screen!
Enjoy smoother gameplay with less stutter, see further with longer render distances and more stability than the alternatives.

Replaces the archaic call-list based chunk rendered (circa 93' technology) with a more 'modern' approach, using the power of OpenGL 3.3 and above.

## Compatibility

Compatible with most other popular client-side optimization and graphics mods, including but not limited to:

- FalseTweaks
- SwanSong
- Right Proper Lighting Engine
- Right Proper MCPatcher
- HodgePodge
- FastCraft
- CoreTweaks
- ArchaicFix
- BugTorch
- NotFine

With expected incompatibilities with:

- OptiFine (any)
- Original ShadersMod
- MCPatcher: Forge
- Neodymium (does the same thing anyway)
- Angelica (May work with certain modules)
- Flying Cullers (Beddium does its own efficient entity culling)

## Technical

This mod is based on the reference implementation of Celeritas for 1.7.10, focused on the terrain renderer.

Other features such as general improvements to entity rendering or ambient occlusion tweaks are not planned.

**FalseTweaks is required for threaded rendering support.**

## License

The mod was developed by `Ven` and `FalsePattern`, Copyright 2025, licensed under the GNU Lesser General Public License, Version 3.0 only.

For more information, see the LICENSE file. This readme section is not a replacement for the full license text.

## Credits

Designed by Ven, written by FalsePattern

Initial testing done by, (alphabetic order):

- Bot
- Brandyn
- DarkShadow44
- Houstonruss
- Jss
- matt-159
- Rally
- UnicornBlood

Based on the work of Embeddedt and his multiplatform framework [Celeritas](https://git.taumc.org/embeddedt/celeritas)
With credits to the CaffeineMC team, and other contributors to forks of the Sodium project.