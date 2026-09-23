# Evoker Raids

Drinking an Evoker's Omen applies the **Evoker's Omen** effect. When the player enters a village under valid raid conditions, it converts into **Evoker Raid Omen** and starts a custom raid.

## What changes

During an Evoker's Omen raid:

- Raid-spawned raiders are replaced with Evokers.
- Higher Omen levels add extra legitimate raid Evokers.
- Evoker stacking/riding behavior is prevented for the custom raid.
- The custom Omen level is stored with the raid so it can survive saving and reloading.

## What stays vanilla

Evoker's Omen deliberately avoids changing unrelated gameplay:

- Vanilla Ominous Bottles still create vanilla raids.
- Normal world mob spawning is unchanged.
- `/summon` is unchanged.
- Evokers outside custom Evoker's Omen raids are not converted into special raid Evokers.
- Vanilla raid flow remains responsible for waves, raid bars, victory, and other normal raid behavior.

## Difficulty scaling

Level I uses the baseline converted raid. Levels II–V progressively add more Evokers, reaching approximately double the baseline Evoker count at Level V.
