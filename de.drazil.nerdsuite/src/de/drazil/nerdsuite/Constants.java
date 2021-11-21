package de.drazil.nerdsuite;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import de.drazil.nerdsuite.util.SwtUtil;

public class Constants {
	public final static String APP_ID = "de.drazil.nerdsuite";
	public final static String PLUGIN_BASE_PATH = String.format("platform:/plugin/%s/", APP_ID);
	public final static String OWNER = "OWNER";

	public final static String PREFERENCES_TEXTCOLOR_OPCODE = "preferences.textcolor.instruction";
	public final static String PREFERENCES_TEXTCOLOR_ILLEGAL_OPCODE = "preferences.textcolor.illegal_instruction";
	public final static String PREFERENCES_TEXTCOLOR_UNSTABLE_ILLEGAL_OPCODE = "preferences.textcolor.unstable_illegal_instruction";
	public final static String PREFERENCES_TEXTCOLOR_DIRECTIVE = "preferences.textcolor.directive";
	public final static String PREFERENCES_TEXTCOLOR_COMMENT = "preferences.textcolor.comment";
	public final static String PREFERENCES_TEXTCOLOR_STRING = "preferences.textcolor.string";
	public final static String PREFERENCES_TEXTCOLOR_HEXADECIMAL = "preferences.textcolor.hexadecimal";
	public final static String PREFERENCES_TEXTCOLOR_DECIMAL = "preferences.textcolor.decimal";
	public final static String PREFERENCES_TEXTCOLOR_BINARY = "preferences.textcolor.binary";
	public final static String PREFERENCES_TEXTCOLOR_LABEL = "preferences.textcolor.label";

	public final static int DIRECTIVE = 0;
	public final static int OPCODE = 1;
	public final static int ILLEGAL_OPCODE = 2;
	public final static int UNSTABLE_ILLEGAL_OPCODE = 3;

	public final static String DEFAULT_WORKSPACE_NAME = ".nerdsuiteWorkspace";

	public final static String USER_HOME = System.getProperty("user.home");
	public final static String FILE_SEPARATOR = System.getProperty("file.separator");

	public final static String DEFAULT_SOURCE_PATH = "src";
	public final static String DEFAULT_BINARY_PATH = "bin";
	public final static String DEFAULT_INCLUDE_PATH = "include";
	public final static String DEFAULT_SYMBOL_PATH = "symbol";

	public final static String ID = "id";
	public final static String NAME = "name";

	public final static String SOURCE_FOLDER = "SOURCE_FOLDER";
	public final static String BINARY_FOLDER = "BINARY_FOLDER";
	public final static String INCLUDE_FOLDER = "INCLUDE_FOLDER";
	public final static String SYMBOL_FOLDER = "SYMBOL_FOLDER";

	public final static Color SWAP_TILE_MARKER_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#0000C0"));
	public final static Color SELECTION_TILE_MARKER_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#C00000"));
	public final static Color TEMPORARY_SELECTION_TILE_MARKER_COLOR = new Color(Display.getCurrent(),
			SwtUtil.toRGB("#F4D03F"));

	public final static Color SOURCE_EDITOR_FOREGROUND_COLOR = new Color(Display.getCurrent(),
			SwtUtil.toRGB("#C0C0C0"));
	public final static Color SOURCE_EDITOR_BACKGROUND_COLOR = new Color(Display.getCurrent(),
			SwtUtil.toRGB("#000000"));
	public final static Color SOURCE_EDITOR_HIGHLIGHTED_BACKGROUND_COLOR = new Color(Display.getCurrent(),
			SwtUtil.toRGB("#404040"));
	public final static Color SOURCE_EDITOR_HIGHLIGHTED_FOREGROUND_COLOR = new Color(Display.getCurrent(),
			SwtUtil.toRGB("#a00000"));

	public final static Color GREY3 = new Color(Display.getCurrent(), SwtUtil.toRGB("#D9C69A"));
	public final static Color PIXEL_GRID_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#686868"));
	public final static Color LINE_GRID_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#383838"));
	public final static Color TILE_SUB_GRID_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#FFC30D"));
	public final static Color TILE_GRID_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#808080"));
	public final static Color BYTE_SEPARATOR_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#9AFF9C"));
	public final static Color BITMAP_BACKGROUND_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#000000"));
	public final static Color CURRENT_ROW_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#F0F0FF"));
	public final static Color LINENUMBER_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#A0A0A0"));
	public final static Color DEFAULT_COMMENT_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#20CB6E"));
	public final static Color CBM_FG_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#786abd"));
	public final static Color CBM_BG_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#403285"));
	public final static Color CODE_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#a4957d"));
	public final static Color BINARY_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#00747d"));

	// public final static Color DEFAULT_STRING_COLOR = new
	// Color(Display.getCurrent(), SwtUtil.toRGB("#485CCB"));

	public final static Color FUNCTION_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#00a0d0"));
	public final static Color COMMAND_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#30b090"));
	public final static Color OPERATOR_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#20ff90"));
	public final static Color TRANSPARENT_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#010101"));
	public final static Color DEFAULT_STRING_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#48FCCB"));
	public final static Color DEFAULT_DECIMAL_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#6476CB"));
	public final static Color DEFAULT_HEXADECIMAL_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#94769B"));
	public final static Color DEFAULT_BINARY_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#A4765B"));
	public final static Color DEFAULT_LABEL_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#7164CB"));
	public final static Color DEFAULT_DIRCETIVE_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#809E35"));
	public final static Color DEFAULT_OPCODE_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#9090FF"));
	public final static Color DEFAULT_ILLEGAL_OPCODE_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#A6924B"));
	public final static Color DEFAULT_UNSTABLE_ILLEGAL_OPCODE_COLOR = new Color(Display.getCurrent(),
			SwtUtil.toRGB("#A64425"));
	public final static Color DEFAULT_CURLY_BRACES_COLOR = new Color(Display.getCurrent(), SwtUtil.toRGB("#A3A3A3"));
	public final static Color WHITE = new Color(Display.getCurrent(), SwtUtil.toRGB("#FFFFFF"));
	public final static Color BLACK = new Color(Display.getCurrent(), SwtUtil.toRGB("#000000"));
	public final static Color LIGHT_BLUE = new Color(Display.getCurrent(), SwtUtil.toRGB("#EFEFFF"));
	public final static Color LIGHT_GREEN = new Color(Display.getCurrent(), SwtUtil.toRGB("#EFFFEF"));
	public final static Color LIGHT_GREEN2 = new Color(Display.getCurrent(), SwtUtil.toRGB("#D0FFD0"));
	public final static Color DARK_GREY = new Color(Display.getCurrent(), SwtUtil.toRGB("#3f474f"));
	public final static Color BRIGHT_ORANGE = new Color(Display.getCurrent(), SwtUtil.toRGB("#FF5722"));

	public final static Color RED = new Color(Display.getCurrent(), SwtUtil.toRGB("#FF0000"));
	public final static Color GREEN = new Color(Display.getCurrent(), SwtUtil.toRGB("#00FF00"));
	public final static Color LIGHT_RED = new Color(Display.getCurrent(), SwtUtil.toRGB("#FF9090"));

	public final static Font DEFAULT_FONT = Display.getCurrent().getSystemFont();
	public final static Font EDITOR_FONT = JFaceResources.getFont(JFaceResources.TEXT_FONT);

	public final static Font GoogleMaterials = new Font(Display.getCurrent(), "Material Icons", 16, SWT.NORMAL);
	public final static Font ICOMOON = new Font(Display.getCurrent(), "icomoon", 16, SWT.NORMAL);
	public final static Font FontAwesome5ProSolid = new Font(Display.getCurrent(), "Font Awesome 5 Pro Solid", 20,
			SWT.NORMAL);
	public final static Font FontAwesome5ProSolid_12 = new Font(Display.getCurrent(), "Font Awesome 5 Pro Solid", 16,
			SWT.NORMAL);
	public final static Font Ubuntu_Mono = new Font(Display.getCurrent(), "Ubuntu Mono", 20, SWT.NORMAL);
	public final static Font DroidSans_Mono = new Font(Display.getCurrent(), "Droid Sans Mono", 20, SWT.NORMAL);
	public final static Font PT_Mono = new Font(Display.getCurrent(), "PT Mono", 10, SWT.NORMAL);
	public final static Font SourceCodePro_Mono = new Font(Display.getCurrent(), "Source Code Pro", 10, SWT.NORMAL);
	public final static Font C64_Pro_Mono_FONT = new Font(Display.getCurrent(), "C64 Pro Mono", 6, SWT.NORMAL);
	public final static Font C64_Pro_Mono_FONT_10 = new Font(Display.getCurrent(), "C64 Pro Mono", 10, SWT.NORMAL);
	public final static Font C64_Pro_Mono_FONT_8 = new Font(Display.getCurrent(), "C64 Pro Mono", 8, SWT.NORMAL);
	public final static Font C64_Pro_Mono_FONT_12 = new Font(Display.getCurrent(), "C64 Pro Mono", 12, SWT.NORMAL);
	public final static Font Atari_Classic_FONT = new Font(Display.getCurrent(), "Atari Classic", 10, SWT.NORMAL);
	public final static Font CPC_FONT = new Font(Display.getCurrent(), "Amstrad CPC correcy", 11, SWT.NORMAL);

	public final static Font PetMe2Y_FONT = new Font(Display.getCurrent(), "Pet Me 2Y", 12, SWT.NORMAL);
	public final static Font PetMe642Y_FONT = new Font(Display.getCurrent(), "Pet Me 64 2Y", 12, SWT.NORMAL);
	public final static Font PetMe64_FONT = new Font(Display.getCurrent(), "Pet Me 64", 6, SWT.NORMAL);
	public final static Font SpaceMono_FONT = new Font(Display.getCurrent(), "Space Mono", 10, SWT.NORMAL);
	public final static Font RobotoMonoBold_FONT = new Font(Display.getCurrent(), "Roboto Mono", 10, SWT.NORMAL);

	public final static TextStyle TEXTSTYLE_PetMe642Y_ASCII = new TextStyle(PetMe642Y_FONT, DEFAULT_COMMENT_COLOR,
			BLACK);
	public final static TextStyle TEXTSTYLE_PetMe64_ASCII = new TextStyle(PetMe64_FONT, DEFAULT_COMMENT_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_PetMe2Y_ASCII = new TextStyle(PetMe2Y_FONT, DEFAULT_COMMENT_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_C64_ASCII = new TextStyle(C64_Pro_Mono_FONT, DEFAULT_COMMENT_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_ATARI_ASCII = new TextStyle(Atari_Classic_FONT, DEFAULT_COMMENT_COLOR,
			BLACK);
	public final static TextStyle TEXTSTYLE_STRING = new TextStyle(EDITOR_FONT, DEFAULT_STRING_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_COMMENT = new TextStyle(RobotoMonoBold_FONT, DEFAULT_COMMENT_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_COMMENT_BLOCK = new TextStyle(RobotoMonoBold_FONT, DEFAULT_LABEL_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_DECIMAL = new TextStyle(EDITOR_FONT, DEFAULT_DECIMAL_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_HEXADECIMAL = new TextStyle(EDITOR_FONT, DEFAULT_HEXADECIMAL_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_BINARY = new TextStyle(EDITOR_FONT, DEFAULT_BINARY_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_LABEL = new TextStyle(EDITOR_FONT, DEFAULT_LABEL_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_DIRECTIVE = new TextStyle(EDITOR_FONT, DEFAULT_DIRCETIVE_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_OPCODE = new TextStyle(EDITOR_FONT, DEFAULT_OPCODE_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_ILLEGAL_OPCODE = new TextStyle(EDITOR_FONT, DEFAULT_ILLEGAL_OPCODE_COLOR,
			BLACK);
	public final static TextStyle TEXTSTYLE_UNSTABLE_ILLEGAL_OPCODE = new TextStyle(EDITOR_FONT,
			DEFAULT_UNSTABLE_ILLEGAL_OPCODE_COLOR, BLACK);
	public final static TextStyle TEXTSTYLE_CURLY_BRACES = new TextStyle(EDITOR_FONT, DEFAULT_CURLY_BRACES_COLOR,
			BLACK);
	public final static TextStyle TEXTSTYLE_COMMAND = new TextStyle(RobotoMonoBold_FONT, DEFAULT_OPCODE_COLOR, WHITE);
	public final static TextStyle TEXTSTYLE_CODE = new TextStyle(RobotoMonoBold_FONT, CODE_COLOR, WHITE);
	public final static TextStyle TEXTSTYLE_DATA = new TextStyle(EDITOR_FONT, BINARY_COLOR, WHITE);
	public final static TextStyle TEXTSTYLE_UNDEFINED = new TextStyle(EDITOR_FONT, WHITE, BLACK);

	public final static String T_C64_BASIC_STRING = "C64BASIC_STRING";
	public final static String T_Atari_BASIC_STRING = "Atari_BASIC_STRING";
	public final static String T_PETME642YASCII = "PETME642YASCII";
	public final static String T_PETME2YASCII = "PETME2YASCII";
	public final static String T_C64ASCII = "C64ASCII";
	public final static String T_COMMENT = "COMMENT";
	public final static String T_COMMENT_BLOCK = "COMMENT_BLOCK";
	public final static String T_DECIMAL = "DECIMAL";
	public final static String T_BASIC_COMMAND = "BASIC_COMMAND";
	public final static String T_BINARY = "BINARY";
	public final static String T_HEXADECIMAL = "HEXADECIMAL";
	public final static String T_LABEL = "LABEL";
	public final static String T_ADRESS = "ADRESS";
	public final static String T_STRING = "STRING";
	public final static String T_DIRECTIVE = "DIRECTIVE";
	public final static String T_OPCODE = "OPCODE";
	public final static String T_COMMAND = "COMMAND";
	public final static String T_ILLEGAL_OPCODE = "ILLEGAL_OPCODE";
	public final static String T_UNSTABLE_ILLEGAL_OPCODE = "UNSTABLE_ILLEGAL_OPCODE";

	public final static String PROJECT_FILE_INFO_HEADER = "// Nerdsuite Project by drazil 2017-2020\n"
			+ "// Projectname_____: %s\n" + "// Created on______: %s\n" + "// Changed on______: %s\n";

}
