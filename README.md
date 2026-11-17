# 🗑️ Quick Trash

**Keep your inventory clean in seconds!** **Quick Trash** is a lightweight, vanilla-friendly Quality of Life (QoL) mod that lets you instantly delete and restore items directly from your inventory screen without dragging anything into a trash can.

## ⭐ Features

- ⌨️ **Fully Customizable Keybinds**: Easily change both the Delete and Undo keys anytime in your game controls.
- ⚡ **No Dragging Required!** Simply hover your cursor over the item and tap **`D`** (saving you ~0.5s per item…).
- 📦 **Bulk Delete (Shift + D)**: Clear ALL matching items of the same type across your container in a single press.
- 🧹 **Swipe-to-Delete**: Hold down **`D`** and smoothly glide your mouse across slots to wipe out rows of junk instantly.
- ↩️ **Full Undo Support**: Restores the exact quantity from your last deletion (even multi-stack batches!), with up to 64 undo steps.
- 🔊 **Satisfying Sound Effects**: Crisp extinguish and pickup audio cues give you instant, tactile feedback.
- 🪶 **Lightweight & Desync-Free**: Minimal code, zero lag, fully server-synchronized with no ghost items.

## 📷 Showcase

**Hover over an item and press `D` to delete it instantly.**

![Hover over item](https://raw.githubusercontent.com/1hidomi/quick_trash/master/quicktrash_%28neo%29forge_1.20.1-1.0.0/docs/01_hover_before_delete.jpg)

**The item is immediately removed with a crisp sound.**

![Item deleted](https://raw.githubusercontent.com/1hidomi/quick_trash/master/quicktrash_%28neo%29forge_1.20.1-1.0.0/docs/02_press_D_deleted.jpg)

**Hold `D` and drag across slots to delete multiple items in one swipe.**

![Hold D and swipe](https://raw.githubusercontent.com/1hidomi/quick_trash/master/quicktrash_%28neo%29forge_1.20.1-1.0.0/docs/03_swipe_delete_multiple.jpg)

**Press `Z` to undo — the entire deleted batch is restored at once.**

![Press Z to undo](https://raw.githubusercontent.com/1hidomi/quick_trash/master/quicktrash_%28neo%29forge_1.20.1-1.0.0/docs/04_undo_restored_hotbar.jpg)

---

## Features

1. **Swipe-to-Delete (Hold & Drag)**
   - **Default Key**: `D` (customizable in controls).
   - Works only while inside container screens (inventory, chest, shulker box, crafting table, etc.).
   - **Single Delete**: Hover over an item and press `D` to delete it instantly.
   - **Continuous Swipe**: **Hold down `D` and drag your cursor** across slots to continuously delete items in a fast, fluid motion.

2. **Delete All Matching Items (Shift + D)**
   - Hover over any item in your inventory or container and press **`Shift + D`**.
   - Instantly deletes **all items of the same type** in the current container (e.g., clear all cobblestone stacks at once).

3. **Undo Deletions (Recover Items)**
   - **Default Key**: `Z` (customizable in controls).
   - Each press of `Z` restores the **entire batch** of the last deletion:
     - If you deleted a stack of 64 eggs with `D`, pressing `Z` restores all 64 eggs.
     - If you deleted 5 stacks of cobblestone with `Shift + D`, pressing `Z` restores **all 5 stacks at once**!
   - Preserves enchantments, custom names, and NBT data.
   - Supports up to 64 consecutive undo steps.
   - Safely returns items to the player's inventory, or drops them at the player's feet if the inventory is full (never voids items).

4. **Smart Search Protection**
   - Automatically disables delete and undo shortcuts while typing in search boxes (e.g., recipe book search bar, JEI/REI search bars) to prevent accidental deletions.

5. **Full Survival & Creative Support**
   - Works consistently in both Survival and Creative modes.
   - Uses server-validated C2S network packets (`ClearSlotC2SPacket`, `ClearSameItemsC2SPacket`, and `UndoDeleteC2SPacket`) ensuring secure multiplayer synchronization (requires the mod on both client and server for multiplayer).

6. **Satisfying Audio Feedback**
   - Plays a crisp extinguish sound upon deleting items.
   - Plays an item pickup chime and displays an action-bar notification showing the restored item and quantity when undoing.

## Key Bindings

You can configure the key bindings anytime via:
**Options → Controls → Key Binds → Quick Trash**

| Action | Default Key |
|--------|-------------|
| Hold & Swipe to Delete | `D` |
| Delete All Matching Items | `Shift + D` |
| Undo Last Deletion | `Z` |

## 📦 Supported Versions

| Minecraft Version | Mod Loader | Status |
|---|---|---|
| 1.20.1 | Forge | Available |
| 1.20.1 | NeoForge | Available |
| 26.3 | Forge | Available |
| 26.3 | NeoForge | Available |

## 📥 Downloads

Download the version matching your Minecraft version and mod loader.

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/quick-trash)
- [Modrinth](https://modrinth.com/mod/quicktrash)

## Installation

Drop the `.jar` file into your `.minecraft/mods` folder.

> **Multiplayer Note**: Both the client and the server must have this mod installed for it to work in multiplayer.

## Building from Source

1. Open the project root folder in your IDE (Cursor, IntelliJ IDEA, VS Code, etc.).
2. Open the terminal and run:
   ```cmd
   .\gradlew.bat build
   ```
3. The compiled jar will be at `build/libs/quicktrash-1.0.0-1.20.1.jar`.

## License

Copyright (c) 2026 1hidomi. **All Rights Reserved**.

Redistribution, modification, and re-uploading of this mod or its source code are strictly prohibited without explicit written permission from the author. Modpack inclusion on CurseForge and Modrinth is permitted.
