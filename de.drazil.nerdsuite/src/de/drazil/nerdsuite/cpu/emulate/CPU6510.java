package de.drazil.nerdsuite.cpu.emulate;

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
	public int execute(int pc, int[] ram, int[] rom) {
		int instruction = (int) ram[pc];
		switch (instruction) {
		case 0x0: {// BRK
			pc++;
			pc++;
			setFlag(FLAG_BREAK, true);
			setFlag(FLAG_INTERRUPT, true);
			stack.push(pc);
			stack.push(registers[REG_FLAGS]);
			pc = (ram[0xfffe] & 0xff) + (ram[0xffff] << 8) & 0xff00;
			break;
		}
		case 0x1: {// ORA ($ll,X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] |= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x2: {
			break;
		}
		case 0x3: {
			break;
		}
		case 0x4: {
			break;
		}
		case 0x5: {// ORA $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_A] |= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x6: {// ASL $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			setFlag(FLAG_CARRY, (ram[adr] & 0x80) == 0x80);
			ram[adr] <<= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 2;
			break;
		}
		case 0x7: {
			break;
		}
		case 0x8: {// PHP
			stack.push(registers[REG_FLAGS]);
			pc++;
			break;
		}
		case 0x9: {// ORA #$nn
			registers[REG_A] |= (byte) getValue(pc + 1, ram, rom);
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xa: {// ASL
			setFlag(FLAG_CARRY, (registers[REG_A] & 0x80) == 0x80);
			registers[REG_A] <<= 1;
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			pc += 1;
			break;
		}
		case 0xb: {
			break;
		}
		case 0xc: {
			break;
		}
		case 0xd: {// ORA $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_A] |= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0xe: {// ASl $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			setFlag(FLAG_CARRY, (ram[adr] & 0x80) == 0x80);
			ram[adr] <<= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 3;
			break;
		}
		case 0xf: {
		}
		case 0x10: {// BPL $hhll
			if (!hasFlag(FLAG_NEGATIVE)) {
				pc += ((ram[pc + 1] & 0x80) == 0x80 ? -(ram[pc + 1] & 0x7f) : (ram[pc + 1] & 0x7f));
			}
			break;
		}
		case 0x11: {// ORA ($ll),Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0) + registers[REG_Y];
			adr = getAdress(adr, ram, rom, (byte) 0);
			registers[REG_A] |= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x12: {
			break;
		}
		case 0x13: {
			break;
		}
		case 0x14: {
			break;
		}
		case 0x15: {// ORA $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] |= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x16: {// ASL $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			setFlag(FLAG_CARRY, (ram[adr] & 0x80) == 0x80);
			ram[adr] <<= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 2;
			break;
		}
		case 0x17: {
			break;
		}
		case 0x18: {// CLC
			registers[REG_FLAGS] &= ~FLAG_CARRY;
			pc++;
			break;
		}
		case 0x19: {// ORA $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			registers[REG_A] |= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x1a: {
			break;
		}
		case 0x1b: {
			break;
		}
		case 0x1c: {
			break;
		}
		case 0x1d: {// ORA $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] |= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x1e: {// ASL $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			setFlag(FLAG_CARRY, (ram[adr] & 0x80) == 0x80);
			ram[adr] <<= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 3;
			break;
		}
		case 0x1f: {
			break;
		}
		case 0x20: {// JSR $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			pc += 3;
			stack.push(pc);
			break;
		}
		case 0x21: {// AND ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] &= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x22: {
			break;
		}
		case 0x23: {
			break;
		}
		case 0x24: {// BIT $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			break;
		}
		case 0x25: {// AND $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_A] &= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x26: {// ROL $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			setFlag(FLAG_CARRY, (ram[adr] & 0x80) == 0x80);
			ram[adr] <<= 1;
			if (hasFlag(FLAG_CARRY)) {
				ram[adr] &= 1;
			}
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 2;
			break;
		}
		case 0x27: {
			break;
		}
		case 0x28: {// PLP
			registers[REG_FLAGS] = stack.pop();
			pc++;
			break;
		}
		case 0x29: {// AND #$nn
			int v = getValue(pc + 1, ram, rom);
			registers[REG_A] &= v;
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x2a: {// ROL
			setFlag(FLAG_CARRY, (registers[REG_A] & 0x80) == 0x80);
			registers[REG_A] <<= 1;
			if (hasFlag(FLAG_CARRY)) {
				registers[REG_A] &= 1;
			}
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			pc += 1;
			break;
		}
		case 0x2b: {
			break;
		}
		case 0x2c: {// BIT $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			break;
		}
		case 0x2d: {// AND $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_A] &= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x2e: {// ROL $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			setFlag(FLAG_CARRY, (ram[adr] & 0x80) == 0x80);
			ram[adr] <<= 1;
			if (hasFlag(FLAG_CARRY)) {
				ram[adr] &= 1;
			}
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 3;
			break;
		}
		case 0x2f: {
			break;
		}
		case 0x30: {// BMI $hhll
			if (hasFlag(FLAG_NEGATIVE)) {
				pc += ((ram[pc + 1] & 0x80) == 0x80 ? -(ram[pc + 1] & 0x7f) : (ram[pc + 1] & 0x7f));
			}
			break;
		}
		case 0x31: {// AND ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0) + registers[REG_Y];
			adr = getAdress(adr, ram, rom, (byte) 0);
			registers[REG_A] &= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x32: {
			break;
		}
		case 0x33: {
			break;
		}
		case 0x34: {
			break;
		}
		case 0x35: {// AND $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] &= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x36: {// ROL $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			setFlag(FLAG_CARRY, (ram[adr] & 0x80) == 0x80);
			ram[adr] <<= 1;
			if (hasFlag(FLAG_CARRY)) {
				ram[adr] &= 1;
			}
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 2;
			break;
		}
		case 0x37: {
			break;
		}
		case 0x38: {// SEC
			registers[REG_FLAGS] |= FLAG_CARRY;
			pc++;
			break;
		}
		case 0x39: {// AND $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			registers[REG_A] &= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x3a: {
			break;
		}
		case 0x3b: {
			break;
		}
		case 0x3c: {
			break;
		}
		case 0x3d: {// AND $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] &= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x3e: {// ROL $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			setFlag(FLAG_CARRY, (ram[adr] & 0x80) == 0x80);
			ram[adr] <<= 1;
			if (hasFlag(FLAG_CARRY)) {
				ram[adr] &= 1;
			}
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 2;
			break;
		}
		case 0x3f: {
			break;
		}
		case 0x40: {// RTI
			break;
		}
		case 0x41: {// EOR ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] ^= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x42: {
			break;
		}
		case 0x43: {
			break;
		}
		case 0x44: {
			break;
		}
		case 0x45: {// EOR $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_A] ^= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x46: {// LSR $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			setFlag(FLAG_CARRY, (ram[adr] & 1) == 1);
			ram[adr] >>= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 2;
			break;
		}
		case 0x47: {
			break;
		}
		case 0x48: {// PHA
			stack.push(registers[REG_A]);
			pc++;
			break;
		}
		case 0x49: {// EOR #$nn
			int v = getValue(pc + 1, ram, rom);
			registers[REG_A] ^= v;
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x4a: {// LSR
			setFlag(FLAG_CARRY, (registers[REG_A] & 1) == 1);
			registers[REG_A] >>= 1;
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			pc += 1;
			break;
		}
		case 0x4b: {
			break;
		}
		case 0x4c: {// JMP $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			break;
		}
		case 0x4d: {// EOR $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_A] ^= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x4e: {// LSR $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			setFlag(FLAG_CARRY, (ram[adr] & 1) == 1);
			ram[adr] >>= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 3;
			break;
		}
		case 0x4f: {
			break;
		}
		case 0x50: {// BVC $hhll
			if (!hasFlag(FLAG_OVERFLOW)) {

			}
			pc += 2;
			break;
		}
		case 0x51: {// EOR ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0) + registers[REG_Y];
			adr = getAdress(adr, ram, rom, (byte) 0);
			registers[REG_A] &= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x52: {
			break;
		}
		case 0x53: {
			break;
		}
		case 0x54: {
			break;
		}
		case 0x55: {// EOR $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] ^= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x56: {// LSR $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			setFlag(FLAG_CARRY, (ram[adr] & 1) == 1);
			ram[adr] >>= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 2;
			break;
		}
		case 0x57: {
			break;
		}
		case 0x58: {// CLI
			registers[REG_FLAGS] &= ~FLAG_INTERRUPT;
			pc++;
			break;
		}
		case 0x59: {// EOR $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			registers[REG_A] ^= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x5a: {
			break;
		}
		case 0x5b: {
			break;
		}
		case 0x5c: {
			break;
		}
		case 0x5d: {// EOR $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] ^= ram[adr];
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x5e: {// LSR $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			setFlag(FLAG_CARRY, (ram[adr] & 1) == 1);
			ram[adr] >>= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 3;
			break;
		}
		case 0x5f: {
			break;
		}
		case 0x60: {// RTS
			pc = stack.pop();
			break;
		}
		case 0x61: {// ADC ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_m + lo_a + (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0xff) > 9)
					lo += 6;
				int hi = hi_m + hi_a + (lo > 15 ? 1 : 0);
				if ((hi & 0xff) > 9)
					hi += 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi > 15));

			} else {
				registers[REG_A] += ram[adr] + (hasFlag(FLAG_CARRY) ? 1 : 0);
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x62: {
			break;
		}
		case 0x63: {
			break;
		}
		case 0x64: {
			break;
		}
		case 0x65: {// ADC $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_m + lo_a + (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0xff) > 9)
					lo += 6;
				int hi = hi_m + hi_a + (lo > 15 ? 1 : 0);
				if ((hi & 0xff) > 9)
					hi += 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi > 15));

			} else {
				registers[REG_A] += ram[adr] + (hasFlag(FLAG_CARRY) ? 1 : 0);
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x66: {// ROR $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			setFlag(FLAG_CARRY, (ram[adr] & 1) == 1);
			ram[adr] >>= 1;
			if (hasFlag(FLAG_CARRY)) {
				ram[adr] &= 0x80;
			}
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 2;
			break;
		}
		case 0x67: {
			break;
		}
		case 0x68: {// PLA
			registers[REG_A] = stack.pop();
			pc++;
			break;
		}
		case 0x69: {// ADC #$nn
			int v = getValue(pc + 1, ram, rom);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = v & 0b00001111;
				int hi_m = (v >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_m + lo_a + (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0xff) > 9)
					lo += 6;
				int hi = hi_m + hi_a + (lo > 15 ? 1 : 0);
				if ((hi & 0xff) > 9)
					hi += 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi > 15));

			} else {
				registers[REG_A] += v + (hasFlag(FLAG_CARRY) ? 1 : 0);
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x6a: {// ROR
			setFlag(FLAG_CARRY, (registers[REG_A] & 1) == 1);
			registers[REG_A] >>= 1;
			if (hasFlag(FLAG_CARRY)) {
				registers[REG_A] &= 0x80;
			}
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			pc++;
			break;
		}
		case 0x6b: {
			break;
		}
		case 0x6c: {// JMP ($hhll)
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			break;
		}
		case 0x6d: {// ADC $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_m + lo_a + (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0xff) > 9)
					lo += 6;
				int hi = hi_m + hi_a + (lo > 15 ? 1 : 0);
				if ((hi & 0xff) > 9)
					hi += 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi > 15));

			} else {
				registers[REG_A] += ram[adr] + (hasFlag(FLAG_CARRY) ? 1 : 0);
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x6e: {// ROR $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			setFlag(FLAG_CARRY, (ram[adr] & 1) == 1);
			ram[adr] >>= 1;
			if (hasFlag(FLAG_CARRY)) {
				ram[adr] &= 0x80;
			}
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 2;
			break;
		}
		case 0x6f: {
			break;
		}
		case 0x70: {// BVS $hhll
			if (hasFlag(FLAG_OVERFLOW)) {

			}
			pc += 2;
			break;
		}
		case 0x71: {// ADC ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_m + lo_a + (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0xff) > 9)
					lo += 6;
				int hi = hi_m + hi_a + (lo > 15 ? 1 : 0);
				if ((hi & 0xff) > 9)
					hi += 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi > 15));

			} else {
				registers[REG_A] += ram[adr] + (hasFlag(FLAG_CARRY) ? 1 : 0);
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x72: {
			break;
		}
		case 0x73: {
			break;
		}
		case 0x74: {
			break;
		}
		case 0x75: {// ADC $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_m + lo_a + (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0xff) > 9)
					lo += 6;
				int hi = hi_m + hi_a + (lo > 15 ? 1 : 0);
				if ((hi & 0xff) > 9)
					hi += 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi > 15));

			} else {
				registers[REG_A] += ram[adr] + (hasFlag(FLAG_CARRY) ? 1 : 0);
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0x76: {// ROR $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			setFlag(FLAG_CARRY, (ram[adr] & 1) == 1);
			ram[adr] >>= 1;
			if (hasFlag(FLAG_CARRY)) {
				ram[adr] &= 0x80;
			}
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 2;
			break;
		}
		case 0x77: {
			break;
		}
		case 0x78: {// SEI
			registers[REG_FLAGS] |= FLAG_INTERRUPT;
			break;
		}
		case 0x79: {// ADC $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_m + lo_a + (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0xff) > 9)
					lo += 6;
				int hi = hi_m + hi_a + (lo > 15 ? 1 : 0);
				if ((hi & 0xff) > 9)
					hi += 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi > 15));

			} else {
				registers[REG_A] += ram[adr] + (hasFlag(FLAG_CARRY) ? 1 : 0);
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x7a: {
			break;
		}
		case 0x7b: {
			break;
		}
		case 0x7c: {
			break;
		}
		case 0x7d: {// ADC $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_m + lo_a + (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0xff) > 9)
					lo += 6;
				int hi = hi_m + hi_a + (lo > 15 ? 1 : 0);
				if ((hi & 0xff) > 9)
					hi += 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi > 15));

			} else {
				registers[REG_A] += ram[adr] + (hasFlag(FLAG_CARRY) ? 1 : 0);
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0x7e: {// ROR $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			setFlag(FLAG_CARRY, (ram[adr] & 1) == 1);
			ram[adr] >>= 1;
			if (hasFlag(FLAG_CARRY)) {
				ram[adr] &= 0x80;
			}
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			pc += 3;
			break;
		}
		case 0x7f: {
			break;
		}
		case 0x80: {
			break;
		}
		case 0x81: {// STA ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			ram[adr] = registers[REG_A] & 0xff;
			pc += 2;
			break;
		}
		case 0x82: {
			break;
		}
		case 0x83: {
			break;
		}
		case 0x84: {// STY $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] = registers[REG_Y] & 0xff;
			pc += 2;
			break;
		}
		case 0x85: {// STA $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] = registers[REG_A] & 0xff;
			pc += 2;
			break;
		}
		case 0x86: {// STX $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] = registers[REG_X] & 0xff;
			pc += 2;
			break;
		}
		case 0x87: {
			break;
		}
		case 0x88: {// DEY
			registers[REG_Y] += 1;
			setNegativeFlag(registers[REG_Y]);
			setZeroFlag(registers[REG_Y]);
			registers[REG_Y] &= 0xff;
			pc += 1;
			break;
		}
		case 0x89: {
			break;
		}
		case 0x8a: {// TXA
			registers[REG_A] = registers[REG_X];
			pc++;
			break;
		}
		case 0x8b: {
			break;
		}
		case 0x8c: {// STY $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] = registers[REG_Y] & 0xff;
			pc += 2;
			break;
		}
		case 0x8d: {// STA $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] = registers[REG_A] & 0xff;
			pc += 2;
			break;
		}
		case 0x8e: {// STX $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] = registers[REG_X] & 0xff;
			pc += 2;
			break;
		}
		case 0x8f: {
			break;
		}
		case 0x90: {// BCC $hhll
			if (!hasFlag(FLAG_CARRY)) {
				pc += ((ram[pc + 1] & 0x80) == 0x80 ? -(ram[pc + 1] & 0x7f) : (ram[pc + 1] & 0x7f));
			}
			break;
		}
		case 0x91: {// STA ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] = registers[REG_A] & 0xff;
			pc += 2;
			break;
		}
		case 0x92: {
			break;
		}
		case 0x93: {
			break;
		}
		case 0x94: {// STY $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			ram[adr] = registers[REG_Y] & 0xff;
			pc += 2;
			break;
		}
		case 0x95: {// STA $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			ram[adr] = registers[REG_A] & 0xff;
			pc += 2;
			break;
		}
		case 0x96: {// STX $ll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			ram[adr] = registers[REG_X] & 0xff;
			pc += 2;
			break;
		}
		case 0x97: {
			break;
		}
		case 0x98: {// TYA
			registers[REG_A] = registers[REG_Y];
			pc++;
			break;
		}
		case 0x99: {// STA $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			break;
		}
		case 0x9a: {// TXS
			registers[REG_SP] = registers[REG_X];
			pc++;
			break;
		}
		case 0x9b: {
			break;
		}
		case 0x9c: {
			break;
		}
		case 0x9d: {// STA $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			ram[adr] = registers[REG_A] & 0xff;
			pc += 2;
			break;
		}
		case 0x9e: {
			break;
		}
		case 0x9f: {
			break;
		}
		case 0xa0: {// LDY #$nn
			int v = getValue(pc + 1, ram, rom);
			registers[REG_Y] = v & 0xff;
			setNegativeFlag(registers[REG_Y]);
			setZeroFlag(registers[REG_Y]);
			registers[REG_Y] &= 0xff;
			pc += 2;
			break;
		}
		case 0xa1: {// LDA ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xa2: {// LDX #$nn
			int v = getValue(pc + 1, ram, rom);
			registers[REG_X] = v & 0xff;
			setNegativeFlag(registers[REG_X]);
			setZeroFlag(registers[REG_X]);
			registers[REG_X] &= 0xff;
			pc += 2;
			break;
		}
		case 0xa3: {
			break;
		}
		case 0xa4: {// LDY $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_Y] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_Y]);
			setZeroFlag(registers[REG_Y]);
			registers[REG_Y] &= 0xff;
			pc += 2;
			break;
		}
		case 0xa5: {// LDA $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_A] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xa6: {// LDX $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_X] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_X]);
			setZeroFlag(registers[REG_X]);
			registers[REG_X] &= 0xff;
			pc += 2;
			break;
		}
		case 0xa7: {
			break;
		}
		case 0xa8: {// TAY
			registers[REG_Y] = registers[REG_A];
			pc++;
			break;
		}
		case 0xa9: {// LDA #$nn
			int v = getValue(pc + 1, ram, rom);
			registers[REG_A] = v & 0xff;
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xaa: {// TAX
			registers[REG_X] = registers[REG_A];
			pc++;
			break;
		}
		case 0xab: {
			break;
		}
		case 0xac: {// LDY $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_Y] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_Y]);
			setZeroFlag(registers[REG_Y]);
			registers[REG_Y] &= 0xff;
			pc += 3;
			break;
		}
		case 0xad: {// LDA $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_A] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0xae: {// LDX $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_X] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_X]);
			setZeroFlag(registers[REG_X]);
			registers[REG_X] &= 0xff;
			pc += 3;
			break;
		}
		case 0xaf: {
			break;
		}
		case 0xb0: {// BCS $hhll
			if (hasFlag(FLAG_CARRY)) {
				pc += ((ram[pc + 1] & 0x80) == 0x80 ? -(ram[pc + 1] & 0x7f) : (ram[pc + 1] & 0x7f));
			}
			break;
		}
		case 0xb1: {// LDA ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			registers[REG_A] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xb2: {
			break;
		}
		case 0xb3: {
			break;
		}
		case 0xb4: {// LDY $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_Y] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_Y]);
			setZeroFlag(registers[REG_Y]);
			registers[REG_Y] &= 0xff;
			pc += 2;
			break;
		}
		case 0xb5: {// LDA $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xb6: {// LDX $ll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			registers[REG_X] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_X]);
			setZeroFlag(registers[REG_X]);
			registers[REG_X] &= 0xff;
			pc += 2;
			break;
		}
		case 0xb7: {
			break;
		}
		case 0xb8: {// CLV
			registers[REG_FLAGS] &= ~FLAG_OVERFLOW;
			pc++;
			break;
		}
		case 0xb9: {// LDA $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			registers[REG_A] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0xba: {// TSX
			registers[REG_X] = registers[REG_SP];
			pc++;
			break;
		}
		case 0xbb: {
			break;
		}
		case 0xbc: {// LDY $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_Y] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_Y]);
			setZeroFlag(registers[REG_Y]);
			registers[REG_Y] &= 0xff;
			pc += 3;
			break;
		}
		case 0xbd: {// LDA $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			registers[REG_A] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_A]);
			setZeroFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0xbe: {// LDX $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			registers[REG_X] = ram[adr] & 0xff;
			setNegativeFlag(registers[REG_X]);
			setZeroFlag(registers[REG_X]);
			registers[REG_X] &= 0xff;
			pc += 3;
			break;
		}
		case 0xbf: {
			break;
		}
		case 0xc0: {// CPY #$nn
			int v = getValue(pc + 1, ram, rom);
			int res = registers[REG_Y] - v;
			setCompareStatus(res);
			pc += 2;
			break;
		}
		case 0xc1: {// CMP ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			int res = registers[REG_A] - ram[adr];
			setCompareStatus(res);
			pc += 2;
			break;
		}
		case 0xc2: {
			break;
		}
		case 0xc3: {
			break;
		}
		case 0xc4: {// CPY $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			int res = registers[REG_Y] - ram[adr];
			setCompareStatus(res);
			pc += 2;
			break;
		}
		case 0xc5: {// CMP $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			int res = registers[REG_A] - ram[adr];
			setCompareStatus(res);
			pc += 2;
			break;
		}
		case 0xc6: {// DEC $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] -= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			ram[adr] &= 0xff;
			pc += 2;
			break;
		}
		case 0xc7: {
			break;
		}
		case 0xc8: {// INY
			registers[REG_Y] += 1;
			setNegativeFlag(registers[REG_Y]);
			setZeroFlag(registers[REG_Y]);
			registers[REG_Y] &= 0xff;
			pc += 1;
			break;
		}
		case 0xc9: {// CMP #$nn
			int v = getValue(pc + 1, ram, rom);
			int res = registers[REG_A] - v;
			setCompareStatus(res);
			pc += 2;
			break;
		}
		case 0xca: {// DEX
			registers[REG_X] -= 1;
			setNegativeFlag(registers[REG_X]);
			setZeroFlag(registers[REG_X]);
			registers[REG_X] &= 0xff;
			pc += 1;
			break;
		}
		case 0xcb: {
			break;
		}
		case 0xcc: {// CPY $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			int res = registers[REG_Y] - ram[adr];
			setCompareStatus(res);
			pc += 3;
			break;
		}
		case 0xcd: {// CMP $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			int res = registers[REG_A] - ram[adr];
			setCompareStatus(res);
			pc += 3;
			break;
		}
		case 0xce: {// DEC $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] -= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			ram[adr] &= 0xff;
			pc += 3;
			break;
		}
		case 0xcf: {
			break;
		}
		case 0xd0: {// BNE $hhll

			if (!hasFlag(FLAG_ZERO)) {

			}
			pc += 2;
			break;
		}
		case 0xd1: {// CMP ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			int res = registers[REG_Y] - ram[adr];
			setCompareStatus(res);
			pc += 2;
			break;
		}
		case 0xd2: {
			break;
		}
		case 0xd3: {
			break;
		}
		case 0xd4: {
			break;
		}
		case 0xd5: {// CMP $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			int res = registers[REG_A] - ram[adr];
			setCompareStatus(res);
			pc += 2;
			break;
		}
		case 0xd6: {// DEC $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			ram[adr] -= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			ram[adr] &= 0xff;
			pc += 2;
			break;
		}
		case 0xd7: {
			break;
		}
		case 0xd8: {// CLD
			registers[REG_FLAGS] &= ~FLAG_DECIMAL;
			pc++;
			break;
		}
		case 0xd9: {// CMP $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			int res = registers[REG_A] - ram[adr];
			setCompareStatus(res);
			pc += 3;
			break;
		}
		case 0xda: {
			break;
		}
		case 0xdb: {
			break;
		}
		case 0xdc: {
			break;
		}
		case 0xdd: {// CMP $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			int res = registers[REG_A] - ram[adr];
			setCompareStatus(res);
			pc += 3;
			break;
		}
		case 0xde: {// DEC $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			ram[adr] -= 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			ram[adr] &= 0xff;
			pc += 2;
			break;
		}
		case 0xdf: {
			break;
		}
		case 0xe0: {// CPX #$nn
			int v = getValue(pc + 1, ram, rom);
			int res = registers[REG_X] - v;
			setCompareStatus(res);
			pc += 2;
			break;
		}
		case 0xe1: {// SBC ($ll, X)
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_a - lo_m - (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0x10) != 0)
					lo -= 6;
				int hi = hi_a - hi_m - ((lo & 0x10) != 0 ? 1 : 0);
				if ((hi & 0x10) != 0)
					hi -= 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi < 15));
			} else {
				registers[REG_A] -= ram[adr];
				if (hasFlag(FLAG_CARRY)) {
					registers[REG_A] += 1;
				}
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xe2: {
			break;
		}
		case 0xe3: {
			break;
		}
		case 0xe4: {// CPX $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			int res = registers[REG_X] - ram[adr];
			setCompareStatus(res);
			pc += 2;
			break;
		}
		case 0xe5: {// SBC $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_a - lo_m - (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0x10) != 0)
					lo -= 6;
				int hi = hi_a - hi_m - ((lo & 0x10) != 0 ? 1 : 0);
				if ((hi & 0x10) != 0)
					hi -= 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi < 15));
			} else {
				registers[REG_A] -= ram[adr];
				if (hasFlag(FLAG_CARRY)) {
					registers[REG_A] += 1;
				}
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xe6: {// INC $ll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] += 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			ram[adr] &= 0xff;
			pc += 2;
			break;
		}
		case 0xe7: {
			break;
		}
		case 0xe8: {// INX
			registers[REG_X] += 1;
			setNegativeFlag(registers[REG_X]);
			setZeroFlag(registers[REG_X]);
			registers[REG_X] &= 0xff;
			pc += 1;
			break;
		}
		case 0xe9: {// SBC #$nn
			int v = getValue(pc + 1, ram, rom);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = v & 0b00001111;
				int hi_m = (v >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_a - lo_m - (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0x10) != 0)
					lo -= 6;
				int hi = hi_a - hi_m - ((lo & 0x10) != 0 ? 1 : 0);
				if ((hi & 0x10) != 0)
					hi -= 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi < 15));
			} else {
				registers[REG_A] -= v;
				if (hasFlag(FLAG_CARRY)) {
					registers[REG_A] += 1;
				}
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xea: {// NOP
			break;
		}
		case 0xeb: {
			break;
		}
		case 0xec: {// CPX $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			int res = registers[REG_X] - ram[adr];
			setCompareStatus(res);
			pc += 3;
			break;
		}
		case 0xed: {// SBC $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_a - lo_m - (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0x10) != 0)
					lo -= 6;
				int hi = hi_a - hi_m - ((lo & 0x10) != 0 ? 1 : 0);
				if ((hi & 0x10) != 0)
					hi -= 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi < 15));
			} else {
				registers[REG_A] -= ram[adr];
				if (hasFlag(FLAG_CARRY)) {
					registers[REG_A] += 1;
				}
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0xee: {// INC $hhll
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			ram[adr] += 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			ram[adr] &= 0xff;
			pc += 3;
			break;
		}
		case 0xef: {
			break;
		}
		case 0xf0: {// BEQ $hhll
			if (hasFlag(FLAG_ZERO)) {
				pc += ((ram[pc + 1] & 0x80) == 0x80 ? -(ram[pc + 1] & 0x7f) : (ram[pc + 1] & 0x7f));
			}
			break;
		}
		case 0xf1: {// SBC ($ll), Y
			int adr = getAdress(pc + 1, ram, rom, (byte) 0);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_a - lo_m - (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0x10) != 0)
					lo -= 6;
				int hi = hi_a - hi_m - ((lo & 0x10) != 0 ? 1 : 0);
				if ((hi & 0x10) != 0)
					hi -= 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi < 15));
			} else {
				registers[REG_A] -= ram[adr];
				if (hasFlag(FLAG_CARRY)) {
					registers[REG_A] += 1;
				}
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xf2: {
			break;
		}
		case 0xf3: {
			break;
		}
		case 0xf4: {
			break;
		}
		case 0xf5: {// SBC $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_a - lo_m - (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0x10) != 0)
					lo -= 6;
				int hi = hi_a - hi_m - ((lo & 0x10) != 0 ? 1 : 0);
				if ((hi & 0x10) != 0)
					hi -= 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi < 15));
			} else {
				registers[REG_A] -= ram[adr];
				if (hasFlag(FLAG_CARRY)) {
					registers[REG_A] += 1;
				}
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 2;
			break;
		}
		case 0xf6: {// INC $ll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			ram[adr] += 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			ram[adr] &= 0xff;
			pc += 2;
			break;
		}
		case 0xf7: {
			break;
		}
		case 0xf8: {// SED
			registers[REG_FLAGS] |= FLAG_DECIMAL;
			pc++;
			break;
		}
		case 0xf9: {// SBC $hhll, Y
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_Y]);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_a - lo_m - (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0x10) != 0)
					lo -= 6;
				int hi = hi_a - hi_m - ((lo & 0x10) != 0 ? 1 : 0);
				if ((hi & 0x10) != 0)
					hi -= 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi < 15));
			} else {
				registers[REG_A] -= ram[adr];
				if (hasFlag(FLAG_CARRY)) {
					registers[REG_A] += 1;
				}
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0xfa: {
			break;
		}
		case 0xfb: {
			break;
		}
		case 0xfc: {
			break;
		}
		case 0xfd: {// SBC $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			if (hasFlag(FLAG_DECIMAL)) {
				int lo_m = ram[adr] & 0b00001111;
				int hi_m = (ram[adr] >> 4) & 0b00001111;
				int lo_a = registers[REG_A] & 0b00001111;
				int hi_a = (registers[REG_A] >> 4) & 0b00001111;

				int lo = lo_a - lo_m - (hasFlag(FLAG_CARRY) ? 1 : 0);
				if ((lo & 0x10) != 0)
					lo -= 6;
				int hi = hi_a - hi_m - ((lo & 0x10) != 0 ? 1 : 0);
				if ((hi & 0x10) != 0)
					hi -= 6;
				int result = (lo & 0x0f) | (hi << 4);
				result &= 0xff;
				registers[REG_A] = result;
				setFlag(FLAG_CARRY, (hi < 15));
			} else {
				registers[REG_A] -= ram[adr];
				if (hasFlag(FLAG_CARRY)) {
					registers[REG_A] += 1;
				}
			}
			setZeroFlag(registers[REG_A]);
			setNegativeFlag(registers[REG_A]);
			setCarryFlag(registers[REG_A]);
			setOverflowFlag(registers[REG_A]);
			registers[REG_A] &= 0xff;
			pc += 3;
			break;
		}
		case 0xfe: {// INC $hhll, X
			int adr = getAdress(pc + 1, ram, rom, (byte) registers[REG_X]);
			ram[adr] += 1;
			setNegativeFlag(ram[adr]);
			setZeroFlag(ram[adr]);
			ram[adr] &= 0xff;
			pc += 3;
			break;
		}
		case 0xff: {
			break;
		}

		}
		return pc;
	}

	private int getAdress(int pc, int[] ram, int[] rom, int offset) {
		int l = ram[pc] & 0xff;
		int h = ram[pc + 1] & 0xff;
		return (((h << 8) + l) + offset) & 0xffff;
	}

	private int getValue(int pc, int[] ram, int[] rom) {
		return ram[pc] & 0xff;
	}

	private void setZeroFlag(int value) {
		setFlag(FLAG_ZERO, (value & 0xff) == 0);
	}

	private void setNegativeFlag(int value) {
		setFlag(FLAG_NEGATIVE, (value & 0x80) == 0x80);
	}

	private void setCarryFlag(int value) {
		setFlag(FLAG_CARRY, (value & 0x100) == 0x100);
	}

	private void setOverflowFlag(int value) {
		setFlag(FLAG_OVERFLOW, (value & 0x100) == 0x100);
	}

	private void setCompareStatus(int value) {
		if (value > 0) {
			setFlag(FLAG_CARRY, true);
		} else if (value < 0) {
			setFlag(FLAG_NEGATIVE, true);
		} else {
			setFlag(FLAG_CARRY, true);
			setFlag(FLAG_ZERO, true);
		}
	}
}
