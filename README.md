# nerdsuite
Nerdsuite is an IDE (based on eclipse 4.25.0/2022-09) to develop software for old 8Bit machines (C64, Atari 800XL etc.)  
[![Linux](https://svgshare.com/i/Zhy.svg)](https://svgshare.com/i/Zhy.svg)[![macOS](https://svgshare.com/i/ZjP.svg)](https://svgshare.com/i/ZjP.svg)[![Windows](https://svgshare.com/i/ZhY.svg)](https://svgshare.com/i/ZhY.svg)

Download prebuilt version for...
[Windows](https://drazil.de/nerdsuite/Nerdsuite-win32.win32.x86_64.zip)
[Linux](https://drazil.de/nerdsuite/Nerdsuite-linux.gtk.x86_64.tar.gz)
[Mac](http://drazil.de/nerdsuite/Nerdsuite-macosx.cocoa.x86_64.tar.gz)

Current state: Still buggy and incomplete :-)

## Build Prerequisites
1. Download latest java (for example amazon corretto)
	```https://aws.amazon.com/de/corretto/```
2. Download latest eclipse RCP IDE
	```https://www.eclipse.org/downloads/packages/```
3. Download latest lombok and install it on your eclipse IDE
	```https://projectlombok.org/```
4. Download latest Maven
	```https://maven.apache.org/download.cgi```
5. Clone tycho-lombokizer and follow the instructions
	```https://github.com/poul-m/tycho-lombokizer```
6. Clone nerdsuite repo
	```https://github.com/guidobonerz/nerdsuite.git```
7. Build 
	- Via Maven
	  goto folder ```de.drazil.nerdsuite.parent``` and execute ```mvn clean install```
	- via eclipse RCP IDE
	  goto folder ```de.drazil.nerdsuite``` and open the ```de.drazil.nerdsuite.product``` file
	  press the ```Export``` button in the upper right corner of the product view to export the product
	  for current target platform.
		


## Screenshots
### Basic Code Editor
![screenshot1](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/basic_editor1.png)
![screenshot1](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/basic_editor2.png)
### Disassembler Z80 Screen (in Progress)
![screenshot1](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/disasmz80.png)
### Disassembler 6502/10 Screen (in Progress)
![screenshot2](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/disasm6502.png)
### Big PETSCII Screen
![screenshot3](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/WideScreen.png)
### Ultimate64 App Streaming Client
![screenshot4](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ultimate64-streaming-windows.png)
### Ultimate64 Debug Streaming Client
![screenshot5](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/debugstream_view.png)
### New Graphic Project Dialog
![screenshot6](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ns_screen1.png)
### Sprite Editor
![screenshot7](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ns_screen2.png)
### CharSet Double Height
![screenshot8](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ns_screen4.png)
### ScreenSet 3x3 PETSCII Font
![screenshot8](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/3x3ScreenSetFont.png)
### Popup menu
![screenshot9](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ns_screen3.png)
### Colorchooser
![screenshot10](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/ColorChooser.png)
### Sprite Animation Example
![screenshot11](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/drops_animation.gif)
### Sprite Animation Example - GI-Joe Firefly Walking left / Imported Hires
![screenshot12](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/gi-joe-firefly-walk-left.gif)
### Reordering Tile Example
![screenshot13](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/tile_reordering.gif)
### Tile Bulk Modification Example
![screenshot14](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/tile_bulk_modification.gif)
### Tile Range Modification on 2x2 Char Example
![screenshot15](https://github.com/guidobonerz/nerdsuite/blob/develop/docs/tile_range_modification.gif)


