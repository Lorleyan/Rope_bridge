# Rope Bridge

A Fabric mod for Minecraft 26.3. The first implementation milestone adds craftable Anchor Shot arrow ammunition and server-side placement of a sagging, walkable bridge made from oak slabs.

## Target versions

- Minecraft 26.3
- Fabric Loader 0.20.1
- Fabric API 0.165.0+26.3
- Java 25

## Current first-pass behavior

- Anchor Shots are ArrowItem ammunition and can be fired from a normal bow.
- At impact, the mod searches up to three blocks forward from the shooter for the farthest suitable starting support.
- The impact must be on a horizontal side of a solid end support with open space on the span-facing side.
- Bridges are limited to a 16-block horizontal span and 3 blocks of anchor height difference.
- A sampled sagging curve is checked for obstructions before oak slabs are placed. Water and collision-free space are allowed.
- Invalid attempts display a brief action-bar message.

## Build

Use Gradle 9.6+ and JDK 25. Run `gradle build` from the repository root. The first build may reveal version-specific mapping or API adjustments; the Minecraft 26.3 Yarn mapping coordinate is pinned in `gradle.properties`.

## Planned follow-ups

The first pass intentionally uses stable slab collision rather than moving collision. Animated ropes and weather-sensitive sway, bridge persistence/removal, configurable settings, additional arrow/bridge variants, and more exact end-anchor geometry are follow-up work. The sampling and limits are kept centralized so those features can be added without baking settings into item behavior.
