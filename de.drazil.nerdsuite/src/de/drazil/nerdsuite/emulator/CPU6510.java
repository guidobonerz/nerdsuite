package de.drazil.nerdsuite.emulator;

public class CPU6510 extends AbstractCPU {
	public static final int FLAG_CARRY = 0;
	public static final int FLAG_ZERO = 1;
	public static final int FLAG_INTERRUPT = 2;
	public static final int FLAG_DECIMAL = 4;
	public static final int FLAG_BREAK = 8;
	public static final int FLAG_EXPANSION = 16;
	public static final int FLAG_OVERFLOW = 32;
	public static final int FLAG_NEGATIVE = 64;

	public static final int REG_SP = 1;
	public static final int REG_PC = 2;
	public static final int REG_A = 3;
	public static final int REG_X = 4;
	public static final int REG_Y = 5;
	public static final int REG_PP = 6;
	public static final int REG_DD = 7;

	@Override
	public int execute(int pc, byte[] ram, byte[] rom) {
		int instruction = (int) ram[pc];
		switch (instruction) {
		case 0x0: {// BRK
			pc++;
			pc++;
			setFlag(FLAG_BREAK);
			stack.push(Byte.valueOf((byte) ((pc >> 8) & 0xff)));
			setFlag(FLAG_INTERRUPT);
			stack.push(Byte.valueOf((byte) (pc & 0xff)));
			stack.push(Byte.valueOf((byte) (registers[REG_FLAGS] & 0xff)));
			pc = (ram[0xfffe] & 0xff) + (ram[0xffff] << 8) & 0xff00;
			break;
		}
		case 0x1: {// ORA ($ll,X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] |= ram[adr + registers[REG_X]];
			pc += 2;
			break;
		}
		case 0x2: {// KIL
			break;
		}
		case 0x3: {// SLO inx
			break;
		}
		case 0x4: {// NOP $ll
			break;
		}
		case 0x5: {// ORA $ll
			int adr = ram[pc + 1] & 0xff;
			registers[REG_A] |= ram[adr];
			pc++;
			break;
		}
		case 0x6: {// ASL $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			int value = ram[pc + 1] & 0xff;
			value <<= 1;
			ram[pc + 1] = (byte) (value & 0xff);
			if (value == 0) {
				registers[REG_FLAGS] |= FLAG_ZERO;
			} else if ((value & 0x80) == 0x80) {
				registers[REG_FLAGS] |= FLAG_NEGATIVE;
			} else if ((value & 0x100) == 0x100) {
				registers[REG_FLAGS] |= FLAG_CARRY;
			}
			pc += 2;
			break;
		}
		case 0x7: {// SLO $ll
			break;
		}
		case 0x8: {// PHP
			stack.push(Byte.valueOf((byte) (registers[REG_FLAGS] & 0xff)));
			pc++;
			break;
		}
		case 0x9: {// ORA #$nn
			int v = getValue(pc + 1, ram, rom);
			break;
		}
		case 0xa: {// ASL
			registers[REG_A] <<= 1;
			if (registers[REG_A] == 0) {
				registers[REG_FLAGS] |= FLAG_ZERO;
			} else if ((registers[REG_A] & 0x80) == 0x80) {
				registers[REG_FLAGS] |= FLAG_NEGATIVE;
			} else if ((registers[REG_A] & 0x100) == 0x100) {
				registers[REG_FLAGS] |= FLAG_CARRY;
			}
			pc += 1;
			break;
		}
		case 0xb: {// ANC imm
			break;
		}
		case 0xc: {// NOP abs
			break;
		}
		case 0xd: {// ORA $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			break;
		}
		case 0xe: {// ASl $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			break;
		}
		case 0xf: {// SLO abs
		}
		case 0x10: {// BPL $hhll

			if ((registers[REG_FLAGS] & FLAG_ZERO) == (byte) 0) {
				pc += ((ram[pc + 1] & 0x80) == 0x80 ? -(ram[pc + 1] & 0x7f) : (ram[pc + 1] & 0x7f));
			}
			break;
		}
		case 0x11: {// ORA ($ll),Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			break;
		}
		case 0x12: {// KIL
		}
		case 0x13: {// SLO iny
		}
		case 0x14: {// NOP zpx
		}
		case 0x15: {// ORA $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x16: {// ASL $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x17: {//
		}
		case 0x18: {// CLC
		}
		case 0x19: {// ORA $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0x1a: {//
		}
		case 0x1b: {//
		}
		case 0x1c: {//
		}
		case 0x1d: {// ORA $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x1e: {// ASL $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x1f: {//
		}
		case 0x20: {// JSR $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x21: {// AND ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x22: {//
		}
		case 0x23: {//
		}
		case 0x24: {// BIT $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x25: {// AND $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x26: {// ROL $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x27: {
		}
		case 0x28: {// PLP
		}
		case 0x29: {// AND #$nn
			int v = getValue(pc + 1, ram, rom);
		}
		case 0x2a: {// ROl
		}
		case 0x2b: {
		}
		case 0x2c: {// BIT $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x2d: {// AND $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x2e: {// ROL $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x2f: {
		}
		case 0x30: {// BMI $hhll
		}
		case 0x31: {// AND ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x32: {
		}
		case 0x33: {
		}
		case 0x34: {
		}
		case 0x35: {// AND $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x36: {// ROL $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x37: {
		}
		case 0x38: {// SEC
		}
		case 0x39: {// AND $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0x3a: {
		}
		case 0x3b: {
		}
		case 0x3c: {
		}
		case 0x3d: {// AND $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x3e: {// ROL $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x3f: {
		}
		case 0x40: {// RTI
		}
		case 0x41: {// EOR ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x42: {
		}
		case 0x43: {
		}
		case 0x44: {
		}
		case 0x45: {// EOR $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x46: {// LSR $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x47: {
		}
		case 0x48: {// PHA
		}
		case 0x49: {// EOR #$nn
			int v = getValue(pc + 1, ram, rom);
		}
		case 0x4a: {// LSR
		}
		case 0x4b: {
		}
		case 0x4c: {// JMP $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x4d: {// EOR $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x4e: {// LSR $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x4f: {
		}
		case 0x50: {// BVC $hhll
		}
		case 0x51: {// EOR ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x52: {
		}
		case 0x53: {
		}
		case 0x54: {
		}
		case 0x55: {// EOR $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x56: {// LSR $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x57: {
		}
		case 0x58: {// CLI
		}
		case 0x59: {// EOR $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0x5a: {
		}
		case 0x5b: {
		}
		case 0x5c: {
		}
		case 0x5d: {// EOR $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x5e: {// LSR $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x5f: {
		}
		case 0x60: {// RTS
		}
		case 0x61: {// ADC ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x62: {
		}
		case 0x63: {
		}
		case 0x64: {
		}
		case 0x65: {// ADC $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x66: {// ROR $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x67: {
		}
		case 0x68: {// PLA
		}
		case 0x69: {// ADC #$nn
			int v = getValue(pc + 1, ram, rom);
		}
		case 0x6a: {// ROR
		}
		case 0x6b: {
		}
		case 0x6c: {// JMP ($hhll)
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x6d: {// ADC $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x6e: {// ROR $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x6f: {
		}
		case 0x70: {// BVS $hhll
		}
		case 0x71: {// ADC ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x72: {
		}
		case 0x73: {
		}
		case 0x74: {
		}
		case 0x75: {// ADC $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x76: {// ROR $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x77: {
		}
		case 0x78: {// SEI
		}
		case 0x79: {// ADC $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0x7a: {
		}
		case 0x7b: {
		}
		case 0x7c: {
		}
		case 0x7d: {// ADC $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x7e: {// ROR $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x7f: {
		}
		case 0x80: {
		}
		case 0x81: {// STA ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x82: {
		}
		case 0x83: {
		}
		case 0x84: {// STY $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x85: {// STA $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x86: {// STX $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x87: {
		}
		case 0x88: {// DEY
		}
		case 0x89: {
		}
		case 0x8a: {// TXA
		}
		case 0x8b: {
		}
		case 0x8c: {// STY $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x8d: {// STA $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x8e: {// STX $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x8f: {
		}
		case 0x90: {// BCC $hhll
		}
		case 0x91: {// STA ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0x92: {
		}
		case 0x93: {
		}
		case 0x94: {// STY $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x95: {// STA $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x96: {// STX $ll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0x97: {
		}
		case 0x98: {// TYA
		}
		case 0x99: {// STA $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0x9a: {// TXS
		}
		case 0x9b: {
		}
		case 0x9c: {
		}
		case 0x9d: {// STA $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0x9e: {
		}
		case 0x9f: {
		}
		case 0xa0: {// LDY #$nn
			int v = getValue(pc + 1, ram, rom);
		}
		case 0xa1: {// LDA ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xa2: {// LDX #$nn
			int v = getValue(pc + 1, ram, rom);
		}
		case 0xa3: {
		}
		case 0xa4: {// LDY $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xa5: {// LDA $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xa6: {// LDX $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xa7: {
		}
		case 0xa8: {// TAY
		}
		case 0xa9: {// LDA #$nn
			int v = getValue(pc + 1, ram, rom);
		}
		case 0xaa: {// TAX
		}
		case 0xab: {
		}
		case 0xac: {// LDY $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xad: {// LDA $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xae: {// LDX $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xaf: {
		}
		case 0xb0: {// BCS $hhll

		}
		case 0xb1: {// LDA ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xb2: {
		}
		case 0xb3: {
		}
		case 0xb4: {// LDY $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xb5: {// LDA $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xb6: {// LDX $ll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0xb7: {
		}
		case 0xb8: {// CLV
		}
		case 0xb9: {// LDA $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0xba: {// TSX
		}
		case 0xbb: {
		}
		case 0xbc: {// LDY $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xbd: {// LDA $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xbe: {// LDX $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0xbf: {
		}
		case 0xc0: {// CPY #$nn
			int v = getValue(pc + 1, ram, rom);
		}
		case 0xc1: {// CMP ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xc2: {
		}
		case 0xc3: {
		}
		case 0xc4: {// CPY $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xc5: {// CMP $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xc6: {// DEC $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xc7: {
		}
		case 0xc8: {// INY
		}
		case 0xc9: {// CMP #$nn
			int v = getValue(pc + 1, ram, rom);
		}
		case 0xca: {// DEX
		}
		case 0xcb: {
		}
		case 0xcc: {// CPY $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xcd: {// CMP $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xce: {// DEC $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xcf: {
		}
		case 0xd0: {// BNE $hhll
		}
		case 0xd1: {// CMP ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xd2: {
		}
		case 0xd3: {
		}
		case 0xd4: {
		}
		case 0xd5: {// CMP $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xd6: {// DEC $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xd7: {
		}
		case 0xd8: {// CLD
		}
		case 0xd9: {// CMP $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0xda: {
		}
		case 0xdb: {
		}
		case 0xdc: {
		}
		case 0xdd: {// CMP $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xde: {// DEC $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xdf: {
		}
		case 0xe0: {// CPX #$nn
			int v = getValue(pc + 1, ram, rom);
		}
		case 0xe1: {// SBC ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xe2: {
		}
		case 0xe3: {
		}
		case 0xe4: {// CPX $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xe5: {// SBC $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xe6: {// INC $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xe7: {
		}
		case 0xe8: {// INX
		}
		case 0xe9: {// SBC #$nn
			int v = getValue(pc + 1, ram, rom);
		}
		case 0xea: {// NOP
		}
		case 0xeb: {
		}
		case 0xec: {// CPX $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xed: {// SBC $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xee: {// INC $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xef: {
		}
		case 0xf0: {// BEQ $hhll
		}
		case 0xf1: {// SBC ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
		}
		case 0xf2: {
		}
		case 0xf3: {
		}
		case 0xf4: {
		}
		case 0xf5: {// SBC $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xf6: {// INC $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xf7: {
		}
		case 0xf8: {// SED
		}
		case 0xf9: {// SBC $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
		}
		case 0xfa: {
		}
		case 0xfb: {
		}
		case 0xfc: {
		}
		case 0xfd: {// SBC $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xfe: {// INC $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
		}
		case 0xff: {
		}

		}
		return 0;
	}

	private int getAdress(int pc, byte[] ram, byte[] rom, byte offset) {
		int l = ram[pc] & 0xff;
		int h = ram[pc + 1] & 0xff;
		return (((h << 8) + l) + offset) & 0xffff;
	}

	private int getValue(int pc, byte[] ram, byte[] rom) {
		return ram[pc] & 0xff;
	}

	private byte getZeroPage(int pc, byte[] ram, byte[] rom) {
		return (byte) (ram[pc] & 0xff);
	}

	private byte getAbsolute(int pc, byte[] ram, byte[] rom) {
		int vl = ram[pc] & 0xff;
		int vh = ram[pc + 1] & 0xff;
		int adr = ((vh << 8) + vl) & 0xffff;
		return ram[adr];
	}
}
