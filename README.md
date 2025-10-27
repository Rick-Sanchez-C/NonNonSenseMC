# NonNonSenseMC — A Mod to Clean Minecraft Nonsense (Fabric 1.21.10)

NonNonSenseMC is a personal mod designed to clean up what I consider "nonsense" in Minecraft. It introduces two key features:

1. **Villager Inspector**: A lightweight HUD to inspect villagers in real time.
2. **Unlimited Trades**: Removes trade limits for villagers, allowing infinite trading.

> Baseline versions: Minecraft 1.21.10 • Fabric Loader 0.17.3 • Fabric API 0.136.0+1.21.10

## Features
### Villager Inspector
- Contextual villager HUD when aiming at a villager.
- Displays:
  - Profession (color-coded) and level.
  - Panic state based on current AI task.
  - Ready to breed status.
  - “Golem detected recently” (value and TTL as exposed by internal memory).
  - “Last slept” memory.
  - “Home” BlockPos.
- Efficient client–server communication: the client requests info only when needed and uses throttling to avoid spamming the server.

### Unlimited Trades
- Removes the maximum usage limit for villager trades.
- Allows infinite trading with villagers without resetting their trades.
- Implemented via a mixin that modifies the `maxUses` and `uses` fields of `TradeOffer`.

## How to use
1. Start the game with the mod installed (client and server if you’re on a modded dedicated/LAN server).
2. Aim your crosshair at a villager to see the HUD.
3. Trade with villagers without worrying about trade limits.

No keybinds required—the features work automatically.

## How it works (high level)
### Villager Inspector
- Client: when you aim at a villager, it sends a C2S request with the entity id, limited by a 150 ms throttle for the same target.
- Server: upon receiving the request, it reads the villager’s brain (memories and running tasks) and responds with an S2C payload, which the client renders in the HUD.

### Unlimited Trades
- A mixin hooks into the `fillRecipes` method of `VillagerEntity`.
- It modifies the `maxUses` and `uses` fields of `TradeOffer` via reflection, setting them to `Integer.MAX_VALUE`.

Advanced networking (Villager Inspector):
- C2S: `nonsensenmc:villager_info_request` — contents: `VAR_INT entityId`.
- S2C: `nonsensenmc:villager_info` — contents: `BOOLEAN scared`, `BOOLEAN readyToBreed`, `STRING home`, `STRING lastSlept`, `STRING golemDetected`.

## Architecture (Clean Architecture)
- Domain
  - `VillagerInfo` (immutable record/DTO): raw villager data without UI formatting.
- Application
  - `VillagerInfoService`: use case that builds `VillagerInfo` from a `VillagerEntity` (reads tasks/memories). No networking/UI dependencies.
- Infrastructure (server)
  - `ServerNetworking`: registers codecs and the C2S handler; uses the service and replies with the S2C packet.
- Client
  - `NonNonSenseMCClient`: registers codecs and the S2C receiver.
  - `ClientVillagerHudState`: HUD state and request throttling.
  - `HudRenderingEntrypoint`: renders the overlay, reading from the state and the pointed `VillagerEntity`.
- Mixins
  - `VillagerEntityMixin`: hooks into villager `fillRecipes` to enable unlimited trades.

## Requirements
- Minecraft: `1.21.10`
- Fabric Loader: `0.17.3`
- Fabric API: `0.136.0+1.21.10`

## Installation (player)
1. Install Fabric Loader compatible with `1.21.10`.
2. Drop this mod’s JAR into your instance’s `mods/` folder.
3. Also install the compatible Fabric API JAR into `mods/`.
4. Launch the game.

For multiplayer/servers: also install the mod and Fabric API server-side if you want everyone to see server-backed HUD data and enjoy unlimited trades.

## Build and run (development, Windows)
In a Command Prompt (cmd):

```cmd
cd /d D:\ideaprojects\aivillageinspector
gradlew.bat --no-daemon clean build -x test
```

To start a development client:

```cmd
cd /d D:\ideaprojects\aivillageinspector
gradlew.bat runClient
```

Artifacts are generated under `build/libs/`.

## Configuration
- Request throttling: currently set to 150 ms per same-target villager (`ClientVillagerHudState.beginRequestIfAllowed`). You can tune it in code.
- Logging: the service emits `debug`-level log lines with raw memory contents.

## Notes and limitations
- Some AI/Brain APIs used come from deprecated methods in this mapping version; they’re preserved for compatibility with current builds.
- The mixin that alters trade offers is experimental and may affect villager trading behavior or balance.

## Troubleshooting
- HUD doesn’t show up:
  - Make sure you’re actually aiming at a villager.
  - Confirm Fabric API is installed and client/server mod versions match.
- Payload/type mismatch:
  - Ensure both client and server register codecs (this mod does in its entrypoints) and that mod versions match on both sides.
- Performance:
  - Throttling limits requests; if you customize the interval, avoid extremely low values to prevent network spam.

## Contributing
PRs are welcome for: migrating to non-deprecated Brain APIs, configuration options, and automated tests for the service.

## License
See the included `LICENSE.txt` file.

## Changelog
A short summary of the latest release is available in `CHANGELOG.md`.

- Current version: `1.0.2` — see `CHANGELOG.md` for full history and notable changes.

For details, open `CHANGELOG.md` in the project root.
