# Void Visuals

Client-seitiger Fabric-Mod fuer Minecraft 1.21.11, Autor: akamevoid.
Menue: Right Shift. Eigener Code, keine Uebernahme aus fremden Mods.

## Bauen mit GitHub
1. Projektordner in ein GitHub-Repo pushen.
2. Tab "Actions" -> Workflow "build" laeuft automatisch.
3. Fertige Jar: Run oeffnen -> Artifacts -> "voidvisuals".
Bei roten Fehlern die Log-Zeile mit "error:" kopieren und weitergeben.
Versionen in gradle.properties ggf. auf https://fabricmc.net/develop abgleichen.

## Module
| Modul | Wirkung |
|---|---|
| crosshair | eigenes Fadenkreuz (Farbe, Groesse) |
| hud | FPS, Koordinaten, Keystrokes |
| hit_color | Farbe der Treffer-Faerbung auf Mobs/Spielern |
| block_outline | Farbe des Blockumrisses |
| glint | blendet den Verzauberungs-Glint aus |
| hide_armor | blendet Ruestung auf Entities aus |
| kill_effect | Partikel-Burst beim Kill (Totem, Flame, Hearts, Cloud, Soul) |
| sky_time | feste Tageszeit (nur visuell, Client) |
| mute_sounds | Sounds stumm schalten, per Textfeld (z.B. step,rain) |
| Presets | Share-Code kopieren / einfuegen (Format VV1:...) |

Vorinstalliert: Resource-Pack "Void Red Glint" (rote Glint-Texturen, eigene Inhalte).
Aktivieren unter Optionen -> Ressourcenpakete.

## Mixins sind fehlertolerant
Jedes Mixin-Modul hat eine eigene Config (voidvisuals.*.mixins.json, required=false).
Passt ein Minecraft-Name nicht, faellt nur dieses Modul aus; im Log steht eine Warnung
mit "voidvisuals". Bitte die Zeile weitergeben, dann wird der Name korrigiert.

## Noch nicht gebaut
Capes, Texture-Mixing mit 3D-Vorschau, Sky-Farben (Skybox), eigene Kill-Animationen.

## Icon
Eigenes Icon als 128x128 PNG nach
src/main/resources/assets/voidvisuals/icon.png legen.
