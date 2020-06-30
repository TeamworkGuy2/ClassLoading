package twg2.jbcm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public enum Opcodes {
	/*  0  0x0 */NOP             (0, 0, none(Type.class), null), // Do nothing, stack: [ "No change" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.nop
	/*  1  0x1 */ACONST_NULL     (1, 0, enums(Type.PUSH1), null), // Push null, stack: [ "...", "..., null" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aconst_null
	/*  2  0x2 */ICONST_M1       (2, 0, enums(Type.PUSH1), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  3  0x3 */ICONST_0        (3, 0, enums(Type.PUSH1), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  4  0x4 */ICONST_1        (4, 0, enums(Type.PUSH1), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  5  0x5 */ICONST_2        (5, 0, enums(Type.PUSH1), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  6  0x6 */ICONST_3        (6, 0, enums(Type.PUSH1), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  7  0x7 */ICONST_4        (7, 0, enums(Type.PUSH1), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  8  0x8 */ICONST_5        (8, 0, enums(Type.PUSH1), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  9  0x9 */LCONST_0        (9, 0, enums(Type.PUSH1), null), // Push long constant, stack: [ "...", "..., <l>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lconst_l
	/* 10  0xA */LCONST_1        (10, 0, enums(Type.PUSH1), null), // Push long constant, stack: [ "...", "..., <l>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lconst_l
	/* 11  0xB */FCONST_0        (11, 0, enums(Type.PUSH1), null), // Push float, stack: [ "...", "..., <f>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fconst_f
	/* 12  0xC */FCONST_1        (12, 0, enums(Type.PUSH1), null), // Push float, stack: [ "...", "..., <f>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fconst_f
	/* 13  0xD */FCONST_2        (13, 0, enums(Type.PUSH1), null), // Push float, stack: [ "...", "..., <f>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fconst_f
	/* 14  0xE */DCONST_0        (14, 0, enums(Type.PUSH1), null), // Push double, stack: [ "...", "..., <d>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dconst_d
	/* 15  0xF */DCONST_1        (15, 0, enums(Type.PUSH1), null), // Push double, stack: [ "...", "..., <d>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dconst_d
	/* 16 0x10 */BIPUSH          (16, 1, enums(Type.PUSH1), null), // Push byte, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.bipush
	/* 17 0x11 */SIPUSH          (17, 2, enums(Type.PUSH1), null), // Push short, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.sipush
	/* 18 0x12 */LDC             (18, 1, enums(Type.PUSH1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Push item from run-time constant pool, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ldc
	/* 19 0x13 */LDC_W           (19, 2, enums(Type.PUSH1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 4))), // Push item from run-time constant pool (wide index), stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ldc_w
	/* 20 0x14 */LDC2_W          (20, 2, enums(Type.PUSH1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 4))), // Push long or double from run-time constant pool (wide index), stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ldc2_w
	/* 21 0x15 */ILOAD           (21, 1, enums(Type.PUSH1), null), // Load int from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iload
	/* 22 0x16 */LLOAD           (22, 1, enums(Type.PUSH1), null), // Load long from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lload
	/* 23 0x17 */FLOAD           (23, 1, enums(Type.PUSH1), null), // Load float from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fload
	/* 24 0x18 */DLOAD           (24, 1, enums(Type.PUSH1), null), // Load double from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dload
	/* 25 0x19 */ALOAD           (25, 1, enums(Type.PUSH1, Type.ARRAY), null), // Load reference from local variable, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aload
	/* 26 0x1A */ILOAD_0         (26, 0, enums(Type.PUSH1), null), // Load int from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iload_n
	/* 27 0x1B */ILOAD_1         (27, 0, enums(Type.PUSH1), null), // Load int from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iload_n
	/* 28 0x1C */ILOAD_2         (28, 0, enums(Type.PUSH1), null), // Load int from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iload_n
	/* 29 0x1D */ILOAD_3         (29, 0, enums(Type.PUSH1), null), // Load int from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iload_n
	/* 30 0x1E */LLOAD_0         (30, 0, enums(Type.PUSH1), null), // Load long from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lload_n
	/* 31 0x1F */LLOAD_1         (31, 0, enums(Type.PUSH1), null), // Load long from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lload_n
	/* 32 0x20 */LLOAD_2         (32, 0, enums(Type.PUSH1), null), // Load long from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lload_n
	/* 33 0x21 */LLOAD_3         (33, 0, enums(Type.PUSH1), null), // Load long from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lload_n
	/* 34 0x22 */FLOAD_0         (34, 0, enums(Type.PUSH1), null), // Load float from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fload_n
	/* 35 0x23 */FLOAD_1         (35, 0, enums(Type.PUSH1), null), // Load float from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fload_n
	/* 36 0x24 */FLOAD_2         (36, 0, enums(Type.PUSH1), null), // Load float from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fload_n
	/* 37 0x25 */FLOAD_3         (37, 0, enums(Type.PUSH1), null), // Load float from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fload_n
	/* 38 0x26 */DLOAD_0         (38, 0, enums(Type.PUSH1), null), // Load double from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dload_n
	/* 39 0x27 */DLOAD_1         (39, 0, enums(Type.PUSH1), null), // Load double from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dload_n
	/* 40 0x28 */DLOAD_2         (40, 0, enums(Type.PUSH1), null), // Load double from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dload_n
	/* 41 0x29 */DLOAD_3         (41, 0, enums(Type.PUSH1), null), // Load double from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dload_n
	/* 42 0x2A */ALOAD_0         (42, 0, enums(Type.PUSH1), null), // Load reference from local variable, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aload_n
	/* 43 0x2B */ALOAD_1         (43, 0, enums(Type.PUSH1), null), // Load reference from local variable, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aload_n
	/* 44 0x2C */ALOAD_2         (44, 0, enums(Type.PUSH1), null), // Load reference from local variable, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aload_n
	/* 45 0x2D */ALOAD_3         (45, 0, enums(Type.PUSH1), null), // Load reference from local variable, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aload_n
	/* 46 0x2E */IALOAD          (46, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY), null), // Load int from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iaload
	/* 47 0x2F */LALOAD          (47, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY), null), // Load long from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.laload
	/* 48 0x30 */FALOAD          (48, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY), null), // Load float from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.faload
	/* 49 0x31 */DALOAD          (49, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY), null), // Load double from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.daload
	/* 50 0x32 */AALOAD          (50, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY), null), // Load reference from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aaload
	/* 51 0x33 */BALOAD          (51, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY), null), // Load byte or boolean from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.baload
	/* 52 0x34 */CALOAD          (52, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY), null), // Load char from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.caload
	/* 53 0x35 */SALOAD          (53, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY), null), // Load short from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.saload
	/* 54 0x36 */ISTORE          (54, 1, enums(Type.POP1), null), // Store int into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.istore
	/* 55 0x37 */LSTORE          (55, 1, enums(Type.POP1), null), // Store long into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lstore
	/* 56 0x38 */FSTORE          (56, 1, enums(Type.POP1), null), // Store float into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fstore
	/* 57 0x39 */DSTORE          (57, 1, enums(Type.POP1), null), // Store double into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dstore
	/* 58 0x3A */ASTORE          (58, 1, enums(Type.POP1, Type.ARRAY), null), // Store reference into local variable, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.astore
	/* 59 0x3B */ISTORE_0        (59, 0, enums(Type.POP1), null), // Store int into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.istore_n
	/* 60 0x3C */ISTORE_1        (60, 0, enums(Type.POP1), null), // Store int into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.istore_n
	/* 61 0x3D */ISTORE_2        (61, 0, enums(Type.POP1), null), // Store int into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.istore_n
	/* 62 0x3E */ISTORE_3        (62, 0, enums(Type.POP1), null), // Store int into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.istore_n
	/* 63 0x3F */LSTORE_0        (63, 0, enums(Type.POP1), null), // Store long into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lstore_n
	/* 64 0x40 */LSTORE_1        (64, 0, enums(Type.POP1), null), // Store long into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lstore_n
	/* 65 0x41 */LSTORE_2        (65, 0, enums(Type.POP1), null), // Store long into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lstore_n
	/* 66 0x42 */LSTORE_3        (66, 0, enums(Type.POP1), null), // Store long into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lstore_n
	/* 67 0x43 */FSTORE_0        (67, 0, enums(Type.POP1), null), // Store float into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fstore_n
	/* 68 0x44 */FSTORE_1        (68, 0, enums(Type.POP1), null), // Store float into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fstore_n
	/* 69 0x45 */FSTORE_2        (69, 0, enums(Type.POP1), null), // Store float into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fstore_n
	/* 70 0x46 */FSTORE_3        (70, 0, enums(Type.POP1), null), // Store float into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fstore_n
	/* 71 0x47 */DSTORE_0        (71, 0, enums(Type.POP1), null), // Store double into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dstore_n
	/* 72 0x48 */DSTORE_1        (72, 0, enums(Type.POP1), null), // Store double into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dstore_n
	/* 73 0x49 */DSTORE_2        (73, 0, enums(Type.POP1), null), // Store double into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dstore_n
	/* 74 0x4A */DSTORE_3        (74, 0, enums(Type.POP1), null), // Store double into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dstore_n
	/* 75 0x4B */ASTORE_0        (75, 0, enums(Type.POP1), null), // Store reference into local variable, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.astore_n
	/* 76 0x4C */ASTORE_1        (76, 0, enums(Type.POP1), null), // Store reference into local variable, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.astore_n
	/* 77 0x4D */ASTORE_2        (77, 0, enums(Type.POP1), null), // Store reference into local variable, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.astore_n
	/* 78 0x4E */ASTORE_3        (78, 0, enums(Type.POP1), null), // Store reference into local variable, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.astore_n
	/* 79 0x4F */IASTORE         (79, 0, enums(Type.POP3, Type.ARRAY), null), // Store into int array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iastore
	/* 80 0x50 */LASTORE         (80, 0, enums(Type.POP3, Type.ARRAY), null), // Store into long array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lastore
	/* 81 0x51 */FASTORE         (81, 0, enums(Type.POP3, Type.ARRAY), null), // Store into float array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fastore
	/* 82 0x52 */DASTORE         (82, 0, enums(Type.POP3, Type.ARRAY), null), // Store into double array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dastore
	/* 83 0x53 */AASTORE         (83, 0, enums(Type.POP3, Type.ARRAY), null), // Store into reference array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aastore
	/* 84 0x54 */BASTORE         (84, 0, enums(Type.POP3, Type.ARRAY), null), // Store into byte or boolean array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.bastore
	/* 85 0x55 */CASTORE         (85, 0, enums(Type.POP3, Type.ARRAY), null), // Store into char array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.castore
	/* 86 0x56 */SASTORE         (86, 0, enums(Type.POP3, Type.ARRAY), null), // Store into short array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.sastore
	/* 87 0x57 */POP             (87, 0, enums(Type.POP1), null), // Pop the top operand stack value, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.pop
	/* 88 0x58 */POP2            (88, 0, enums(Type.POP2), null), // Pop the top one or two operand stack values, stack: [ "Form 1:", "..., value2, value1", "...", "Form 2:", "..., value", "...", "(§2.11.1)." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.pop2
	/* 89 0x59 */DUP             (89, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top operand stack value, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup
	/* 90 0x5A */DUP_X1          (90, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top operand stack value and insert two values down, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup_x1
	/* 91 0x5B */DUP_X2          (91, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top operand stack value and insert two or three values down, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup_x2
	/* 92 0x5C */DUP2            (92, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top one or two operand stack values, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup2
	/* 93 0x5D */DUP2_X1         (93, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top one or two operand stack values and insert two or three values down, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup2_x1
	/* 94 0x5E */DUP2_X2         (94, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top one or two operand stack values and insert two, three, or four values down, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup2_x2
	/* 95 0x5F */SWAP            (95, 0, enums(Type.STACK_MANIPULATE), null), // Swap the top two operand stack values, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.swap
	/* 96 0x60 */IADD            (96, 0, enums(Type.POP2, Type.PUSH1), null), // Add int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iadd
	/* 97 0x61 */LADD            (97, 0, enums(Type.POP2, Type.PUSH1), null), // Add long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ladd
	/* 98 0x62 */FADD            (98, 0, enums(Type.POP2, Type.PUSH1), null), // Add float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fadd
	/* 99 0x63 */DADD            (99, 0, enums(Type.POP2, Type.PUSH1), null), // Add double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dadd
	/* 100 0x64 */ISUB            (100, 0, enums(Type.POP2, Type.PUSH1), null), // Subtract int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.isub
	/* 101 0x65 */LSUB            (101, 0, enums(Type.POP2, Type.PUSH1), null), // Subtract long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lsub
	/* 102 0x66 */FSUB            (102, 0, enums(Type.POP2, Type.PUSH1), null), // Subtract float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fsub
	/* 103 0x67 */DSUB            (103, 0, enums(Type.POP2, Type.PUSH1), null), // Subtract double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dsub
	/* 104 0x68 */IMUL            (104, 0, enums(Type.POP2, Type.PUSH1), null), // Multiply int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.imul
	/* 105 0x69 */LMUL            (105, 0, enums(Type.POP2, Type.PUSH1), null), // Multiply long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lmul
	/* 106 0x6A */FMUL            (106, 0, enums(Type.POP2, Type.PUSH1), null), // Multiply float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fmul
	/* 107 0x6B */DMUL            (107, 0, enums(Type.POP2, Type.PUSH1), null), // Multiply double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dmul
	/* 108 0x6C */IDIV            (108, 0, enums(Type.POP2, Type.PUSH1), null), // Divide int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.idiv
	/* 109 0x6D */LDIV            (109, 0, enums(Type.POP2, Type.PUSH1), null), // Divide long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ldiv
	/* 110 0x6E */FDIV            (110, 0, enums(Type.POP2, Type.PUSH1), null), // Divide float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fdiv
	/* 111 0x6F */DDIV            (111, 0, enums(Type.POP2, Type.PUSH1), null), // Divide double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ddiv
	/* 112 0x70 */IREM            (112, 0, enums(Type.POP2, Type.PUSH1), null), // Remainder int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.irem
	/* 113 0x71 */LREM            (113, 0, enums(Type.POP2, Type.PUSH1), null), // Remainder long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lrem
	/* 114 0x72 */FREM            (114, 0, enums(Type.POP2, Type.PUSH1), null), // Remainder float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.frem
	/* 115 0x73 */DREM            (115, 0, enums(Type.POP2, Type.PUSH1), null), // Remainder double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.drem
	/* 116 0x74 */INEG            (116, 0, enums(Type.POP1, Type.PUSH1), null), // Negate int, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ineg
	/* 117 0x75 */LNEG            (117, 0, enums(Type.POP1, Type.PUSH1), null), // Negate long, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lneg
	/* 118 0x76 */FNEG            (118, 0, enums(Type.POP1, Type.PUSH1), null), // Negate float, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fneg
	/* 119 0x77 */DNEG            (119, 0, enums(Type.POP1, Type.PUSH1), null), // Negate double, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dneg
	/* 120 0x78 */ISHL            (120, 0, enums(Type.POP2, Type.PUSH1), null), // Shift left int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ishl
	/* 121 0x79 */LSHL            (121, 0, enums(Type.POP2, Type.PUSH1), null), // Shift left long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lshl
	/* 122 0x7A */ISHR            (122, 0, enums(Type.POP2, Type.PUSH1), null), // Arithmetic shift right int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ishr
	/* 123 0x7B */LSHR            (123, 0, enums(Type.POP2, Type.PUSH1), null), // Arithmetic shift right long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lshr
	/* 124 0x7C */IUSHR           (124, 0, enums(Type.POP2, Type.PUSH1), null), // Logical shift right int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iushr
	/* 125 0x7D */LUSHR           (125, 0, enums(Type.POP2, Type.PUSH1), null), // Logical shift right long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lushr
	/* 126 0x7E */IAND            (126, 0, enums(Type.POP2, Type.PUSH1), null), // Boolean AND int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iand
	/* 127 0x7F */LAND            (127, 0, enums(Type.POP2, Type.PUSH1), null), // Boolean AND long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.land
	/* 128 0x80 */IOR             (128, 0, enums(Type.POP2, Type.PUSH1), null), // Boolean OR int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ior
	/* 129 0x81 */LOR             (129, 0, enums(Type.POP2, Type.PUSH1), null), // Boolean OR long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lor
	/* 130 0x82 */IXOR            (130, 0, enums(Type.POP2, Type.PUSH1), null), // Boolean XOR int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ixor
	/* 131 0x83 */LXOR            (131, 0, enums(Type.POP2, Type.PUSH1), null), // Boolean XOR long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lxor
	/* 132 0x84 */IINC            (132, 2, none(Type.class), null), // Increment local variable by constant, stack: [ "No change" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iinc
	/* 133 0x85 */I2L             (133, 0, enums(Type.POP1, Type.PUSH1), null), // Convert int to long, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2l
	/* 134 0x86 */I2F             (134, 0, enums(Type.POP1, Type.PUSH1), null), // Convert int to float, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2f
	/* 135 0x87 */I2D             (135, 0, enums(Type.POP1, Type.PUSH1), null), // Convert int to double, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2d
	/* 136 0x88 */L2I             (136, 0, enums(Type.POP1, Type.PUSH1), null), // Convert long to int, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.l2i
	/* 137 0x89 */L2F             (137, 0, enums(Type.POP1, Type.PUSH1), null), // Convert long to float, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.l2f
	/* 138 0x8A */L2D             (138, 0, enums(Type.POP1, Type.PUSH1), null), // Convert long to double, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.l2d
	/* 139 0x8B */F2I             (139, 0, enums(Type.POP1, Type.PUSH1), null), // Convert float to int, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.f2i
	/* 140 0x8C */F2L             (140, 0, enums(Type.POP1, Type.PUSH1), null), // Convert float to long, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.f2l
	/* 141 0x8D */F2D             (141, 0, enums(Type.POP1, Type.PUSH1), null), // Convert float to double, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.f2d
	/* 142 0x8E */D2I             (142, 0, enums(Type.POP1, Type.PUSH1), null), // Convert double to int, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.d2i
	/* 143 0x8F */D2L             (143, 0, enums(Type.POP1, Type.PUSH1), null), // Convert double to long, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.d2l
	/* 144 0x90 */D2F             (144, 0, enums(Type.POP1, Type.PUSH1), null), // Convert double to float, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.d2f
	/* 145 0x91 */I2B             (145, 0, enums(Type.POP1, Type.PUSH1), null), // Convert int to byte, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2b
	/* 146 0x92 */I2C             (146, 0, enums(Type.POP1, Type.PUSH1), null), // Convert int to char, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2c
	/* 147 0x93 */I2S             (147, 0, enums(Type.POP1, Type.PUSH1), null), // Convert int to short, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2s
	/* 148 0x94 */LCMP            (148, 0, enums(Type.POP2, Type.PUSH1), null), // Compare long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lcmp
	/* 149 0x95 */FCMPL           (149, 0, enums(Type.POP2, Type.PUSH1), null), // Compare float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fcmp_op
	/* 150 0x96 */FCMPG           (150, 0, enums(Type.POP2, Type.PUSH1), null), // Compare float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fcmp_op
	/* 151 0x97 */DCMPL           (151, 0, enums(Type.POP2, Type.PUSH1), null), // Compare double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dcmp_op
	/* 152 0x98 */DCMPG           (152, 0, enums(Type.POP2, Type.PUSH1), null), // Compare double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dcmp_op
	/* 153 0x99 */IFEQ            (153, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 154 0x9A */IFNE            (154, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 155 0x9B */IFLT            (155, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 156 0x9C */IFGE            (156, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 157 0x9D */IFGT            (157, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 158 0x9E */IFLE            (158, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 159 0x9F */IF_ICMPEQ       (159, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 160 0xA0 */IF_ICMPNE       (160, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 161 0xA1 */IF_ICMPLT       (161, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 162 0xA2 */IF_ICMPGE       (162, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 163 0xA3 */IF_ICMPGT       (163, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 164 0xA4 */IF_ICMPLE       (164, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 165 0xA5 */IF_ACMPEQ       (165, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if reference comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_acmp_cond
	/* 166 0xA6 */IF_ACMPNE       (166, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if reference comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_acmp_cond
	/* 167 0xA7 */GOTO            (167, 2, enums(Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch always, stack: [ "No change" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.goto
	/* 168 0xA8 */JSR             (168, 2, enums(Type.PUSH1, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Jump subroutine, stack: [ "...", "..., address" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.jsr
	/* 169 0xA9 */RET             (169, 1, none(Type.class), null), // Return from subroutine, stack: [ "No change" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ret
	/* 170 0xAA */TABLESWITCH     (170, Const.UNPREDICTABLE, enums(Type.POP1), Op.of(IoUtility.TableswitchOffsetModifier)), // Access jump table by index and jump, stack: [ "..., index", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.tableswitch
	/* 171 0xAB */LOOKUPSWITCH    (171, Const.UNPREDICTABLE, enums(Type.POP1), Op.of(IoUtility.LookupswitchOffsetModifier)), // Access jump table by key match and jump, stack: [ "..., key", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lookupswitch
	/* 172 0xAC */IRETURN         (172, 0, enums(Type.POP1, Type.RETURN), null), // Return int from method, stack: [ "..., value", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ireturn
	/* 173 0xAD */LRETURN         (173, 0, enums(Type.POP1, Type.RETURN), null), // Return long from method, stack: [ "..., value", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lreturn
	/* 174 0xAE */FRETURN         (174, 0, enums(Type.POP1, Type.RETURN), null), // Return float from method, stack: [ "..., value", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.freturn
	/* 175 0xAF */DRETURN         (175, 0, enums(Type.POP1, Type.RETURN), null), // Return double from method, stack: [ "..., value", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dreturn
	/* 176 0xB0 */ARETURN         (176, 0, enums(Type.POP1, Type.RETURN), null), // Return reference from method, stack: [ "..., objectref", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.areturn
	/* 177 0xB1 */RETURN          (177, 0, enums(Type.RETURN), null), // Return void from method, stack: [ "...", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.return
	/* 178 0xB2 */GETSTATIC       (178, 2, enums(Type.PUSH1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Get static field from class, stack: [ "...,", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.getstatic
	/* 179 0xB3 */PUTSTATIC       (179, 2, enums(Type.POP1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Set static field in class, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.putstatic
	/* 180 0xB4 */GETFIELD        (180, 2, enums(Type.POP1, Type.PUSH1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Fetch field from object, stack: [ "..., objectref", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.getfield
	/* 181 0xB5 */PUTFIELD        (181, 2, enums(Type.POP2, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Set field in object, stack: [ "..., objectref, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.putfield
	/* 182 0xB6 */INVOKEVIRTUAL   (182, 2, enums(Type.POP_UNPREDICTABLE, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Invoke instance method; dispatch based on class, stack: [ "..., objectref, [arg1, [arg2 ...]]", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.invokevirtual
	/* 183 0xB7 */INVOKESPECIAL   (183, 2, enums(Type.POP_UNPREDICTABLE, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Invoke instance method; special handling for superclass, private, and instance initialization method invocations, stack: [ "..., objectref, [arg1, [arg2 ...]]", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.invokespecial
	/* 184 0xB8 */INVOKESTATIC    (184, 2, enums(Type.POP_UNPREDICTABLE, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Invoke a class (static) method, stack: [ "..., [arg1, [arg2 ...]]", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.invokestatic
	/* 185 0xB9 */INVOKEINTERFACE (185, 4, enums(Type.POP_UNPREDICTABLE, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Invoke interface method, stack: [ "..., objectref, [arg1, [arg2 ...]]", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.invokeinterface
	/* 186 0xBA */INVOKEDYNAMIC   (186, 4, enums(Type.POP_UNPREDICTABLE, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Invoke dynamic method, stack: [ "..., [arg1, [arg2 ...]]", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.invokedynamic
	/* 187 0xBB */NEW             (187, 2, enums(Type.PUSH1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Create new object, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.new
	/* 188 0xBC */NEWARRAY        (188, 1, enums(Type.POP1, Type.PUSH1), null), // Create new array, stack: [ "..., count", "..., arrayref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.newarray
	/* 189 0xBD */ANEWARRAY       (189, 2, enums(Type.POP1, Type.PUSH1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Create new array of reference, stack: [ "..., count", "..., arrayref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.anewarray
	/* 190 0xBE */ARRAYLENGTH     (190, 0, enums(Type.POP1, Type.PUSH1), null), // Get length of array, stack: [ "..., arrayref", "..., length" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.arraylength
	/* 191 0xBF */ATHROW          (191, 0, enums(Type.POP1), null), // Throw exception or error, stack: [ "..., objectref", "objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.athrow
	/* 192 0xC0 */CHECKCAST       (192, 2, enums(Type.POP1, Type.PUSH1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Check whether object is of given type, stack: [ "..., objectref", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.checkcast
	/* 193 0xC1 */INSTANCEOF      (193, 2, enums(Type.POP1, Type.PUSH1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Determine if object is of given type, stack: [ "..., objectref", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.instanceof
	/* 194 0xC2 */MONITORENTER    (194, 0, enums(Type.POP1), null), // Enter monitor for object, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.monitorenter
	/* 195 0xC3 */MONITOREXIT     (195, 0, enums(Type.POP1), null), // Exit monitor for object, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.monitorexit
	/* 196 0xC4 */WIDE            (196, Const.UNPREDICTABLE, none(Type.class), null), // Extend local variable index by additional bytes, stack: [ "Same as modified instruction" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.wide
	/* 197 0xC5 */MULTIANEWARRAY  (197, 3, enums(Type.POP3, Type.PUSH1, Type.CP_INDEX), Op.of(IoUtility.cpIndex(1, 2))), // Create new multidimensional array, stack: [ "..., count1, [count2, ...]", "..., arrayref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.multianewarray
	/* 198 0xC6 */IFNULL          (198, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if reference is null, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ifnull
	/* 199 0xC7 */IFNONNULL       (199, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 2))), // Branch if reference not null, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ifnonnull
	/* 200 0xC8 */GOTO_W          (200, 4, enums(Type.JUMP), Op.of(IoUtility.offsetModifier(1, 4))), // Branch always (wide index), stack: [ "No change" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.goto_w
	/* 201 0xC9 */JSR_W           (201, 4, enums(Type.PUSH1, Type.JUMP), Op.of(IoUtility.offsetModifier(1, 4))), // Jump subroutine (wide index), stack: [ "...", "..., address" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.jsr_w
	/* 202 0xCA */BREAKPOINT      (202, Const.RESERVED, none(Type.class), null), // reserved for debuggers, stack: [ "No change" ],
	/* 254 0xFE */IMPDEP1         (254, Const.RESERVED, none(Type.class), null), // reserved opcode, stack: [ "" ],
	/* 255 0xFF */IMPDEP2         (255, Const.RESERVED, none(Type.class), null), // reserved opcode, stack: [ "" ],
	/* -1 0x-1 */UNDEFINED       (-1, -1, none(Type.class), null); // , stack: [ "" ],

	@SafeVarargs
	private static <E extends Enum<E>> EnumSet<E> enums(E... es) {
		return EnumSet.copyOf(Arrays.asList(es));
	}


	private static <E extends Enum<E>> EnumSet<E> none(Class<E> cls) {
		return EnumSet.noneOf(cls);
	}


	/**
	 * @author TeamworkGuy2
	 * @since 2014-4-19
	 */
	public enum Type {
		ARRAY,
		CP_INDEX,
		CONDITION,
		JUMP,
		STACK_MANIPULATE,
		PUSH1,
		@SuppressWarnings("hiding")
		POP1,
		@SuppressWarnings("hiding")
		POP2,
		POP3,
		POP_UNPREDICTABLE,
		@SuppressWarnings("hiding")
		RETURN;
	}


	public static final class Const {
		/** Unpredictable */
		public static final int UNPREDICTABLE = -1;
		/** Restricted */
		public static final int RESERVED = -2;
	}


	/** An inner class with a map to handle the map of opcode types to opcodes that are a member of those types.
	 * Inner class necessary because static blocks in enums are called after enums are initialized I think...?
	 * @author TeamworkGuy2
	 * @since 2014-4-19
	 */
	private enum OpcodeMap {
		;

		/** A map of all OpcodeTypes to a mutable (key) and immutable (value) list of Opcodes that are of that type */
		private static final EnumMap<Type, Entry<List<Opcodes>, List<Opcodes>>> typeMap;
		private static final Opcodes[] opcodes = new Opcodes[256];

		static {
			typeMap = new EnumMap<>(Type.class);
			for(Type type : Type.values()) {
				List<Opcodes> typeList = new ArrayList<>();
				List<Opcodes> typeListIm = Collections.unmodifiableList(typeList);
				Entry<List<Opcodes>, List<Opcodes>> typeSet = new AbstractMap.SimpleImmutableEntry<>(typeList, typeListIm);
				typeMap.put(type, typeSet);
			}
		}
	}


	private final int opcode;
	private final String displayName;
	private final int operandCount;
	private final EnumSet<Type> types;
	private final OpcodeOperations operations;

	/** Create an opcode enum with an opcode value and related fields.
	 * @param opcode the opcode's value
	 * @param clas the class that manages operations related to this opcode
	 * @param types the opcode categories that this opcode falls under
	 * @param ops a set of operations that this opcode supports
	 */
	Opcodes(int opcode, int operandCount, EnumSet<Type> types, Op ops) {
		this.opcode = opcode;
		this.displayName = this.name().toLowerCase();
		this.operandCount = operandCount;
		this.types = types;
		this.operations = ops != null ? ops.create() : OpcodeOperations.EMPTY;
		if(opcode >= 0) {
			setupTypes(); // don't setup UNDEFINED
		}
	}


	/** Add the list of {@link Type OpcodeTypes} associated with this Opcode into the {@link #typeMap}
	 * containing all of the opcodes associated with a specific type.
	 */
	private final void setupTypes() {
		if(types != null) {
			for(Type type : types) {
				Entry<List<Opcodes>, List<Opcodes>> typeSet = OpcodeMap.typeMap.get(type);
				typeSet.getKey().add(this);
			}
		}
		OpcodeMap.opcodes[this.opcode] = this;
	}


	public String displayName() {
		return displayName;
	}


	/**
	 * @return the set of operations that this opcode supports
	 */
	public OpcodeOperations getOperations() {
		return operations;
	}


	/**
	 * @param opcode the opcode to compare to this opcode
	 * @return true if this opcode equals the specified opcode value
	 */
	public boolean is(int opcode) {
		return this.opcode == opcode;
	}


	public boolean hasBehavior(Type type) {
		return this.types.contains(type);
	}


	/**
	 * @return the number of opcodes this instruction uses (i.e. the number of instruction bytes
	 * in a code array after this opcode that belong to this opcode). Note: negative values represent
	 * unknown or illegal operand counts, see {@link Const#UNDEFINED},
	 * {@link Const#UNPREDICTABLE}, {@link Const#RESERVED}.
	 */
	public int getOperandCount() {
		return operandCount;
	}


	/** Return the list of opcodes that are of a specific type
	 * @param type the opcode type to filter by
	 * @return an immutable list of opcodes associated with the specific opcode type
	 */
	public static List<Opcodes> getOpcodeSet(Type type) {
		return OpcodeMap.typeMap.get(type).getValue();
	}


	/** Return the opcode with the specified opcode value
	 * @param opcode the opcode value to lookup
	 * @return the instruction corresponding to the specified opcode
	 */
	public static Opcodes get(int opcode) {
		Opcodes op = OpcodeMap.opcodes[opcode];
		return op != null ? op : Opcodes.UNDEFINED;
	}

}
