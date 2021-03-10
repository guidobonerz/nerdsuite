# nerdsuite
Nerdsuite is an IDE (based on eclipse 4.18.0/2020-12) to develop for old 8Bit machines (C64, Atari 800XL etc.)  
Once finished, it will contain a SourceCode Editor (Assembler/Basic), Visual Disassembler, GfxEditor (for CharSets, SpriteSets, TileSets, TileMaps, Screens and Bitmaps)  
Currently I work mostly on the graphics part.  

| System | OS | Works | Performance | Java Version
| -- | -- | -- | -- | -- |
| Windows (4 GHz i7 / 32GB) | Windows 10 | yes | excellent| 1.8.0_191
| Windows (4 GHz i7 / 32GB) | Windows 10 | yes | excellent| openjdk version "11.0.3" 2019-04-16 LTS
| Windows (4 GHz i7 / 32GB) | Windows 8.1 | yes | excellent| 1.8.0_191
| Windows (2.4 GHz i5 / 16GB) | Windows 7 | yes | good| 1.8.0_191
| Linux (2.5 GHz i5 / 6GB) | Linux Mint 18.2 Cinnamon | yes | ok | 1.8.0_191
| Mac (2 GHz Core2Duo / 8GB) | MacOS 10.11.6 El Capitan | yes | very slow | 1.8.0_25

Current state: Still buggy and incomplete :-)

Last Changes 07-03-2021:
- added some direct commands to the Ultimate64 Virtual Keyboard (RUN/LIST/DIR)
- Set Frame and Backgroundcolor remoteley

Last Changes 07-03-2021:
- added Ultimate64 Virtual Keyboard
- added run programs and disk images directly from U64 internal usb devices via FTP access

Last Changes 29-01-2021:
- added Ultimate64 Debug Stream Viewer

Last Changes 19-01-2021:
- added z80 disassembly

Last Changes 11-01-2021:
- start to implement interactive(visual) disassembler

Last Changes 26-11-2020:
- pixel paint performance/gapless painting

Last Changes 25-11-2020:
- PETASCII Painter
- Performance tuning
- Code cleanup
- Scroll drawing area

Last Changes 04-07-2020:
- select/run programs from D64 image

Last Changes 02-07-2020:
- add start/stop/reset/remote load&start function to streaming client

Last Changes 09-06-2020:
- add ultimate64 streaming client

Last Changes:
- Range selection modification for Shift, Flip, Mirror, Rotate, Invert and Purge
- Bulk tile modifications
- Tile animation finished
- Tile drag movement/reordering
- Sprite import
- Copy/Paste
- Mount disk images to project-browser
- Bitmap Exporter for CharSets and Sprites

Gfx Module Todo:
- Proper Multicolor handling
- Undo/Redo
- Zoom option in all views
- Layer Preview/Selector
- Layer drag reordering
- TileMap reference tile selector
- TileSet reference char selector
- Binary Import
- Export to BIN/BAS/ASM/PNG/GIF/SVG/TTF
- Layer content compression
- Performance tuning
- Code cleanup


Handle DiskImages:
Export files partly works
Create/Update disk images

## Disassembler Z80 Screen (in Progress)
![screenshot1](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/disasmz80.png)
## Disassembler 6502/10 Screen (in Progress)
![screenshot2](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/disasm6502.png)
## Big PETSCII Screen
![screenshot3](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/BigScreen2.png)
## Ultimate64 App Streaming Client
![screenshot4](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ultimate64-streaming-windows.png)
## Ultimate64 Debug Streaming Client
![screenshot5](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/debugstream_view.png)
## New Graphic Project Dialog
![screenshot6](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ns_screen1.png)
## Sprite Editor
![screenshot7](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ns_screen2.png)
## CharSet Double Height
![screenshot8](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ns_screen4.png)
## Popup menu
![screenshot9](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ns_screen3.png)
## Colorchooser
![screenshot10](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ColorChooser.png)
## Sprite Animation Example
![screenshot11](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/drops_animation.gif)
## Sprite Animation Example - GI-Joe Firefly Walking left / Imported Hires
![screenshot12](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/gi-joe-firefly-walk-left.gif)
## Reordering Tile Example
![screenshot13](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/tile_reordering.gif)
## Tile Bulk Modification Example
![screenshot14](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/tile_bulk_modification.gif)
## Tile Range Modification on 2x2 Char Example
![screenshot15](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/tile_range_modification.gif)
## Simple BitmapViewer
Image by Almighty God
![screenshot16](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/koala.png)

