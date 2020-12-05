package twg2.jbcm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import twg2.jbcm.modify.LookupswitchOffsetModifier;
import twg2.jbcm.modify.TableswitchOffsetModifier;

/**
 * @author TeamworkGuy2
 * @since 2014-4-19
 */
public enum Opcodes {
	/*  0x0 */NOP             (0, 0, none(Type.class), null), // Do nothing, stack: [ "No change" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.nop
	/*  0x1 */ACONST_NULL     (1, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push null, stack: [ "...", "..., null" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aconst_null
	/*  0x2 */ICONST_M1       (2, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  0x3 */ICONST_0        (3, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  0x4 */ICONST_1        (4, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  0x5 */ICONST_2        (5, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  0x6 */ICONST_3        (6, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  0x7 */ICONST_4        (7, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  0x8 */ICONST_5        (8, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push int constant, stack: [ "...", "..., <i>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iconst_i
	/*  0x9 */LCONST_0        (9, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push long constant, stack: [ "...", "..., <l>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lconst_l
	/*  0xA */LCONST_1        (10, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push long constant, stack: [ "...", "..., <l>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lconst_l
	/*  0xB */FCONST_0        (11, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push float, stack: [ "...", "..., <f>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fconst_f
	/*  0xC */FCONST_1        (12, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push float, stack: [ "...", "..., <f>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fconst_f
	/*  0xD */FCONST_2        (13, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push float, stack: [ "...", "..., <f>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fconst_f
	/*  0xE */DCONST_0        (14, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push double, stack: [ "...", "..., <d>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dconst_d
	/*  0xF */DCONST_1        (15, 0, enums(Type.PUSH1, Type.CONST_LOAD), null), // Push double, stack: [ "...", "..., <d>" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dconst_d
	/* 0x10 */BIPUSH          (16, 1, enums(Type.PUSH1), null), // Push byte, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.bipush
	/* 0x11 */SIPUSH          (17, 2, enums(Type.PUSH1), null), // Push short, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.sipush
	/* 0x12 */LDC             (18, 1, enums(Type.PUSH1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Push item from run-time constant pool, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ldc
	/* 0x13 */LDC_W           (19, 2, enums(Type.PUSH1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 4))), // Push item from run-time constant pool (wide index), stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ldc_w
	/* 0x14 */LDC2_W          (20, 2, enums(Type.PUSH1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 4))), // Push long or double from run-time constant pool (wide index), stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ldc2_w
	/* 0x15 */ILOAD           (21, 1, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load int from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iload
	/* 0x16 */LLOAD           (22, 1, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load long from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lload
	/* 0x17 */FLOAD           (23, 1, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load float from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fload
	/* 0x18 */DLOAD           (24, 1, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load double from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dload
	/* 0x19 */ALOAD           (25, 1, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load reference from local variable, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aload
	/* 0x1A */ILOAD_0         (26, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load int from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iload_n
	/* 0x1B */ILOAD_1         (27, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load int from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iload_n
	/* 0x1C */ILOAD_2         (28, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load int from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iload_n
	/* 0x1D */ILOAD_3         (29, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load int from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iload_n
	/* 0x1E */LLOAD_0         (30, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load long from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lload_n
	/* 0x1F */LLOAD_1         (31, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load long from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lload_n
	/* 0x20 */LLOAD_2         (32, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load long from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lload_n
	/* 0x21 */LLOAD_3         (33, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load long from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lload_n
	/* 0x22 */FLOAD_0         (34, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load float from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fload_n
	/* 0x23 */FLOAD_1         (35, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load float from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fload_n
	/* 0x24 */FLOAD_2         (36, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load float from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fload_n
	/* 0x25 */FLOAD_3         (37, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load float from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fload_n
	/* 0x26 */DLOAD_0         (38, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load double from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dload_n
	/* 0x27 */DLOAD_1         (39, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load double from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dload_n
	/* 0x28 */DLOAD_2         (40, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load double from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dload_n
	/* 0x29 */DLOAD_3         (41, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load double from local variable, stack: [ "...", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dload_n
	/* 0x2A */ALOAD_0         (42, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load reference from local variable, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aload_n
	/* 0x2B */ALOAD_1         (43, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load reference from local variable, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aload_n
	/* 0x2C */ALOAD_2         (44, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load reference from local variable, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aload_n
	/* 0x2D */ALOAD_3         (45, 0, enums(Type.PUSH1, Type.VAR_LOAD), null), // Load reference from local variable, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aload_n
	/* 0x2E */IALOAD          (46, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY_LOAD), null), // Load int from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iaload
	/* 0x2F */LALOAD          (47, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY_LOAD), null), // Load long from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.laload
	/* 0x30 */FALOAD          (48, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY_LOAD), null), // Load float from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.faload
	/* 0x31 */DALOAD          (49, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY_LOAD), null), // Load double from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.daload
	/* 0x32 */AALOAD          (50, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY_LOAD), null), // Load reference from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aaload
	/* 0x33 */BALOAD          (51, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY_LOAD), null), // Load byte or boolean from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.baload
	/* 0x34 */CALOAD          (52, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY_LOAD), null), // Load char from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.caload
	/* 0x35 */SALOAD          (53, 0, enums(Type.POP2, Type.PUSH1, Type.ARRAY_LOAD), null), // Load short from array, stack: [ "..., arrayref, index", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.saload
	/* 0x36 */ISTORE          (54, 1, enums(Type.POP1, Type.VAR_STORE), null), // Store int into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.istore
	/* 0x37 */LSTORE          (55, 1, enums(Type.POP1, Type.VAR_STORE), null), // Store long into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lstore
	/* 0x38 */FSTORE          (56, 1, enums(Type.POP1, Type.VAR_STORE), null), // Store float into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fstore
	/* 0x39 */DSTORE          (57, 1, enums(Type.POP1, Type.VAR_STORE), null), // Store double into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dstore
	/* 0x3A */ASTORE          (58, 1, enums(Type.POP1, Type.VAR_STORE), null), // Store reference into local variable, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.astore
	/* 0x3B */ISTORE_0        (59, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store int into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.istore_n
	/* 0x3C */ISTORE_1        (60, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store int into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.istore_n
	/* 0x3D */ISTORE_2        (61, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store int into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.istore_n
	/* 0x3E */ISTORE_3        (62, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store int into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.istore_n
	/* 0x3F */LSTORE_0        (63, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store long into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lstore_n
	/* 0x40 */LSTORE_1        (64, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store long into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lstore_n
	/* 0x41 */LSTORE_2        (65, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store long into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lstore_n
	/* 0x42 */LSTORE_3        (66, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store long into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lstore_n
	/* 0x43 */FSTORE_0        (67, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store float into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fstore_n
	/* 0x44 */FSTORE_1        (68, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store float into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fstore_n
	/* 0x45 */FSTORE_2        (69, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store float into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fstore_n
	/* 0x46 */FSTORE_3        (70, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store float into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fstore_n
	/* 0x47 */DSTORE_0        (71, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store double into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dstore_n
	/* 0x48 */DSTORE_1        (72, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store double into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dstore_n
	/* 0x49 */DSTORE_2        (73, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store double into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dstore_n
	/* 0x4A */DSTORE_3        (74, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store double into local variable, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dstore_n
	/* 0x4B */ASTORE_0        (75, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store reference into local variable, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.astore_n
	/* 0x4C */ASTORE_1        (76, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store reference into local variable, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.astore_n
	/* 0x4D */ASTORE_2        (77, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store reference into local variable, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.astore_n
	/* 0x4E */ASTORE_3        (78, 0, enums(Type.POP1, Type.VAR_STORE), null), // Store reference into local variable, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.astore_n
	/* 0x4F */IASTORE         (79, 0, enums(Type.POP3, Type.ARRAY_STORE), null), // Store into int array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iastore
	/* 0x50 */LASTORE         (80, 0, enums(Type.POP3, Type.ARRAY_STORE), null), // Store into long array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lastore
	/* 0x51 */FASTORE         (81, 0, enums(Type.POP3, Type.ARRAY_STORE), null), // Store into float array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fastore
	/* 0x52 */DASTORE         (82, 0, enums(Type.POP3, Type.ARRAY_STORE), null), // Store into double array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dastore
	/* 0x53 */AASTORE         (83, 0, enums(Type.POP3, Type.ARRAY_STORE), null), // Store into reference array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.aastore
	/* 0x54 */BASTORE         (84, 0, enums(Type.POP3, Type.ARRAY_STORE), null), // Store into byte or boolean array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.bastore
	/* 0x55 */CASTORE         (85, 0, enums(Type.POP3, Type.ARRAY_STORE), null), // Store into char array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.castore
	/* 0x56 */SASTORE         (86, 0, enums(Type.POP3, Type.ARRAY_STORE), null), // Store into short array, stack: [ "..., arrayref, index, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.sastore
	/* 0x57 */POP             (87, 0, enums(Type.POP1), null), // Pop the top operand stack value, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.pop
	/* 0x58 */POP2            (88, 0, enums(Type.POP2), null), // Pop the top one or two operand stack values, stack: [ "Form 1:", "..., value2, value1", "...", "Form 2:", "..., value", "...", "(§2.11.1)." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.pop2
	/* 0x59 */DUP             (89, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top operand stack value, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup
	/* 0x5A */DUP_X1          (90, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top operand stack value and insert two values down, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup_x1
	/* 0x5B */DUP_X2          (91, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top operand stack value and insert two or three values down, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup_x2
	/* 0x5C */DUP2            (92, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top one or two operand stack values, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup2
	/* 0x5D */DUP2_X1         (93, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top one or two operand stack values and insert two or three values down, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup2_x1
	/* 0x5E */DUP2_X2         (94, 0, enums(Type.STACK_MANIPULATE), null), // Duplicate the top one or two operand stack values and insert two, three, or four values down, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dup2_x2
	/* 0x5F */SWAP            (95, 0, enums(Type.STACK_MANIPULATE), null), // Swap the top two operand stack values, link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.swap
	/* 0x60 */IADD            (96, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Add int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iadd
	/* 0x61 */LADD            (97, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Add long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ladd
	/* 0x62 */FADD            (98, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Add float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fadd
	/* 0x63 */DADD            (99, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Add double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dadd
	/* 0x64 */ISUB            (100, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Subtract int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.isub
	/* 0x65 */LSUB            (101, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Subtract long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lsub
	/* 0x66 */FSUB            (102, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Subtract float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fsub
	/* 0x67 */DSUB            (103, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Subtract double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dsub
	/* 0x68 */IMUL            (104, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Multiply int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.imul
	/* 0x69 */LMUL            (105, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Multiply long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lmul
	/* 0x6A */FMUL            (106, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Multiply float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fmul
	/* 0x6B */DMUL            (107, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Multiply double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dmul
	/* 0x6C */IDIV            (108, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Divide int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.idiv
	/* 0x6D */LDIV            (109, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Divide long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ldiv
	/* 0x6E */FDIV            (110, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Divide float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fdiv
	/* 0x6F */DDIV            (111, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Divide double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ddiv
	/* 0x70 */IREM            (112, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Remainder int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.irem
	/* 0x71 */LREM            (113, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Remainder long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lrem
	/* 0x72 */FREM            (114, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Remainder float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.frem
	/* 0x73 */DREM            (115, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Remainder double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.drem
	/* 0x74 */INEG            (116, 0, enums(Type.POP1, Type.PUSH1, Type.MATH_OP), null), // Negate int, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ineg
	/* 0x75 */LNEG            (117, 0, enums(Type.POP1, Type.PUSH1, Type.MATH_OP), null), // Negate long, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lneg
	/* 0x76 */FNEG            (118, 0, enums(Type.POP1, Type.PUSH1, Type.MATH_OP), null), // Negate float, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fneg
	/* 0x77 */DNEG            (119, 0, enums(Type.POP1, Type.PUSH1, Type.MATH_OP), null), // Negate double, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dneg
	/* 0x78 */ISHL            (120, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Shift left int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ishl
	/* 0x79 */LSHL            (121, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Shift left long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lshl
	/* 0x7A */ISHR            (122, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Arithmetic shift right int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ishr
	/* 0x7B */LSHR            (123, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Arithmetic shift right long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lshr
	/* 0x7C */IUSHR           (124, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Logical shift right int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iushr
	/* 0x7D */LUSHR           (125, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Logical shift right long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lushr
	/* 0x7E */IAND            (126, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Boolean AND int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iand
	/* 0x7F */LAND            (127, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Boolean AND long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.land
	/* 0x80 */IOR             (128, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Boolean OR int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ior
	/* 0x81 */LOR             (129, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Boolean OR long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lor
	/* 0x82 */IXOR            (130, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Boolean XOR int, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ixor
	/* 0x83 */LXOR            (131, 0, enums(Type.POP2, Type.PUSH1, Type.MATH_OP), null), // Boolean XOR long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lxor
	/* 0x84 */IINC            (132, 2, none(Type.class), null), // Increment local variable by constant, stack: [ "No change" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.iinc
	/* 0x85 */I2L             (133, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert int to long, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2l
	/* 0x86 */I2F             (134, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert int to float, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2f
	/* 0x87 */I2D             (135, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert int to double, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2d
	/* 0x88 */L2I             (136, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert long to int, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.l2i
	/* 0x89 */L2F             (137, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert long to float, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.l2f
	/* 0x8A */L2D             (138, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert long to double, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.l2d
	/* 0x8B */F2I             (139, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert float to int, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.f2i
	/* 0x8C */F2L             (140, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert float to long, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.f2l
	/* 0x8D */F2D             (141, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert float to double, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.f2d
	/* 0x8E */D2I             (142, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert double to int, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.d2i
	/* 0x8F */D2L             (143, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert double to long, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.d2l
	/* 0x90 */D2F             (144, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert double to float, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.d2f
	/* 0x91 */I2B             (145, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert int to byte, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2b
	/* 0x92 */I2C             (146, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert int to char, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2c
	/* 0x93 */I2S             (147, 0, enums(Type.POP1, Type.PUSH1, Type.TYPE_CONVERT), null), // Convert int to short, stack: [ "..., value", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.i2s
	/* 0x94 */LCMP            (148, 0, enums(Type.POP2, Type.PUSH1, Type.COMPARE_NUMERIC), null), // Compare long, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lcmp
	/* 0x95 */FCMPL           (149, 0, enums(Type.POP2, Type.PUSH1, Type.COMPARE_NUMERIC), null), // Compare float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fcmp_op
	/* 0x96 */FCMPG           (150, 0, enums(Type.POP2, Type.PUSH1, Type.COMPARE_NUMERIC), null), // Compare float, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.fcmp_op
	/* 0x97 */DCMPL           (151, 0, enums(Type.POP2, Type.PUSH1, Type.COMPARE_NUMERIC), null), // Compare double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dcmp_op
	/* 0x98 */DCMPG           (152, 0, enums(Type.POP2, Type.PUSH1, Type.COMPARE_NUMERIC), null), // Compare double, stack: [ "..., value1, value2", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dcmp_op
	/* 0x99 */IFEQ            (153, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 0x9A */IFNE            (154, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 0x9B */IFLT            (155, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 0x9C */IFGE            (156, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 0x9D */IFGT            (157, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 0x9E */IFLE            (158, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison with zero succeeds, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_cond
	/* 0x9F */IF_ICMPEQ       (159, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 0xA0 */IF_ICMPNE       (160, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 0xA1 */IF_ICMPLT       (161, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 0xA2 */IF_ICMPGE       (162, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 0xA3 */IF_ICMPGT       (163, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 0xA4 */IF_ICMPLE       (164, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if int comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_icmp_cond
	/* 0xA5 */IF_ACMPEQ       (165, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if reference comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_acmp_cond
	/* 0xA6 */IF_ACMPNE       (166, 2, enums(Type.POP2, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if reference comparison succeeds, stack: [ "..., value1, value2", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.if_acmp_cond
	/* 0xA7 */GOTO            (167, 2, enums(Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch always, stack: [ "No change" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.goto
	/* 0xA8 */JSR             (168, 2, enums(Type.PUSH1, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Jump subroutine, stack: [ "...", "..., address" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.jsr
	/* 0xA9 */RET             (169, 1, none(Type.class), null), // Return from subroutine, stack: [ "No change" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ret
	/* 0xAA */TABLESWITCH     (170, Const.UNPREDICTABLE, enums(Type.POP1), Op.of(TableswitchOffsetModifier.defaultInst)), // Access jump table by index and jump, stack: [ "..., index", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.tableswitch
	/* 0xAB */LOOKUPSWITCH    (171, Const.UNPREDICTABLE, enums(Type.POP1), Op.of(LookupswitchOffsetModifier.defaultInst)), // Access jump table by key match and jump, stack: [ "..., key", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lookupswitch
	/* 0xAC */IRETURN         (172, 0, enums(Type.POP1, Type.RETURN), null), // Return int from method, stack: [ "..., value", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ireturn
	/* 0xAD */LRETURN         (173, 0, enums(Type.POP1, Type.RETURN), null), // Return long from method, stack: [ "..., value", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.lreturn
	/* 0xAE */FRETURN         (174, 0, enums(Type.POP1, Type.RETURN), null), // Return float from method, stack: [ "..., value", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.freturn
	/* 0xAF */DRETURN         (175, 0, enums(Type.POP1, Type.RETURN), null), // Return double from method, stack: [ "..., value", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.dreturn
	/* 0xB0 */ARETURN         (176, 0, enums(Type.POP1, Type.RETURN), null), // Return reference from method, stack: [ "..., objectref", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.areturn
	/* 0xB1 */RETURN          (177, 0, enums(Type.RETURN), null), // Return void from method, stack: [ "...", "[empty]" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.return
	/* 0xB2 */GETSTATIC       (178, 2, enums(Type.PUSH1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Get static field from class, stack: [ "...,", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.getstatic
	/* 0xB3 */PUTSTATIC       (179, 2, enums(Type.POP1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Set static field in class, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.putstatic
	/* 0xB4 */GETFIELD        (180, 2, enums(Type.POP1, Type.PUSH1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Fetch field from object, stack: [ "..., objectref", "..., value" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.getfield
	/* 0xB5 */PUTFIELD        (181, 2, enums(Type.POP2, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Set field in object, stack: [ "..., objectref, value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.putfield
	/* 0xB6 */INVOKEVIRTUAL   (182, 2, enums(Type.POP_UNPREDICTABLE, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Invoke instance method; dispatch based on class, stack: [ "..., objectref, [arg1, [arg2 ...]]", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.invokevirtual
	/* 0xB7 */INVOKESPECIAL   (183, 2, enums(Type.POP_UNPREDICTABLE, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Invoke instance method; direct invocation of instance initialization methods and methods of the current class and its supertypes, stack: [ "..., objectref, [arg1, [arg2 ...]]", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.invokespecial
	/* 0xB8 */INVOKESTATIC    (184, 2, enums(Type.POP_UNPREDICTABLE, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Invoke a class (static) method, stack: [ "..., [arg1, [arg2 ...]]", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.invokestatic
	/* 0xB9 */INVOKEINTERFACE (185, 4, enums(Type.POP_UNPREDICTABLE, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Invoke interface method, stack: [ "..., objectref, [arg1, [arg2 ...]]", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.invokeinterface
	/* 0xBA */INVOKEDYNAMIC   (186, 4, enums(Type.POP_UNPREDICTABLE, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Invoke a dynamically-computed call site, stack: [ "..., [arg1, [arg2 ...]]", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.invokedynamic
	/* 0xBB */NEW             (187, 2, enums(Type.PUSH1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Create new object, stack: [ "...", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.new
	/* 0xBC */NEWARRAY        (188, 1, enums(Type.POP1, Type.PUSH1), null), // Create new array, stack: [ "..., count", "..., arrayref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.newarray
	/* 0xBD */ANEWARRAY       (189, 2, enums(Type.POP1, Type.PUSH1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Create new array of reference, stack: [ "..., count", "..., arrayref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.anewarray
	/* 0xBE */ARRAYLENGTH     (190, 0, enums(Type.POP1, Type.PUSH1), null), // Get length of array, stack: [ "..., arrayref", "..., length" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.arraylength
	/* 0xBF */ATHROW          (191, 0, enums(Type.POP1), null), // Throw exception or error, stack: [ "..., objectref", "objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.athrow
	/* 0xC0 */CHECKCAST       (192, 2, enums(Type.POP1, Type.PUSH1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Check whether object is of given type, stack: [ "..., objectref", "..., objectref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.checkcast
	/* 0xC1 */INSTANCEOF      (193, 2, enums(Type.POP1, Type.PUSH1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Determine if object is of given type, stack: [ "..., objectref", "..., result" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.instanceof
	/* 0xC2 */MONITORENTER    (194, 0, enums(Type.POP1), null), // Enter monitor for object, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.monitorenter
	/* 0xC3 */MONITOREXIT     (195, 0, enums(Type.POP1), null), // Exit monitor for object, stack: [ "..., objectref", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.monitorexit
	/* 0xC4 */WIDE            (196, Const.UNPREDICTABLE, none(Type.class), null), // Extend local variable index by additional bytes, stack: [ "Same as modified instruction" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.wide
	/* 0xC5 */MULTIANEWARRAY  (197, 3, enums(Type.POP3, Type.PUSH1, Type.CP_INDEX), Op.of(CodeUtility.cpIndex(1, 2))), // Create new multidimensional array, stack: [ "..., count1, [count2, ...]", "..., arrayref" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.multianewarray
	/* 0xC6 */IFNULL          (198, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if reference is null, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ifnull
	/* 0xC7 */IFNONNULL       (199, 2, enums(Type.POP1, Type.CONDITION, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 2))), // Branch if reference not null, stack: [ "..., value", "..." ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.ifnonnull
	/* 0xC8 */GOTO_W          (200, 4, enums(Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 4))), // Branch always (wide index), stack: [ "No change" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.goto_w
	/* 0xC9 */JSR_W           (201, 4, enums(Type.PUSH1, Type.JUMP), Op.of(CodeUtility.offsetModifier(1, 4))), // Jump subroutine (wide index), stack: [ "...", "..., address" ], link: https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5.jsr_w
	/* 0xCA */BREAKPOINT      (202, Const.RESERVED, none(Type.class), null), // reserved for debuggers, stack: [ "No change" ],
	/* 0xFE */IMPDEP1         (254, Const.RESERVED, none(Type.class), null), // reserved opcode, stack: [ "" ],
	/* 0xFF */IMPDEP2         (255, Const.RESERVED, none(Type.class), null), // reserved opcode, stack: [ "" ],
	/* 0x-1 */UNDEFINED       (-1, -1, none(Type.class), null); // , stack: [ "" ],

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
		ARRAY_LOAD,
		ARRAY_STORE,
		COMPARE_NUMERIC,
		CONST_LOAD,
		CP_INDEX,
		CONDITION,
		JUMP,
		MATH_OP,
		STACK_MANIPULATE,
		PUSH1,
		POP1,
		@SuppressWarnings("hiding")
		POP2,
		POP3,
		POP_UNPREDICTABLE,
		@SuppressWarnings("hiding")
		RETURN,
		TYPE_CONVERT,
		VAR_LOAD,
		VAR_STORE;

		protected final int flag;

		Type() {
			this.flag = 1 << (this.ordinal() + 1);
		}
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

		/** A map of all Opcodes to a mutable (key) and immutable (value) list of Opcodes that are of that type */
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


	/** An inner class with a map of opcode {@link Type#MATH_OP} types to math symbols.
	 * Inner class necessary because static blocks in enums are called after enums are initialized I think...?
	 * @author TeamworkGuy2
	 * @since 2014-4-19
	 */
	private enum MathSymbols {
		;

		/** A map of math opcodes to math operator symbols */
		private static final EnumMap<Opcodes, String> mathSymbolsMap;

		static {
			EnumMap<Opcodes, String> map = new EnumMap<>(Opcodes.class);
			map.put(Opcodes.IADD, "+");
			map.put(Opcodes.LADD, "+");
			map.put(Opcodes.FADD, "+");
			map.put(Opcodes.DADD, "+");
			map.put(Opcodes.ISUB, "-");
			map.put(Opcodes.LSUB, "-");
			map.put(Opcodes.FSUB, "-");
			map.put(Opcodes.DSUB, "-");
			map.put(Opcodes.IMUL, "*");
			map.put(Opcodes.LMUL, "*");
			map.put(Opcodes.FMUL, "*");
			map.put(Opcodes.DMUL, "*");
			map.put(Opcodes.IDIV, "/");
			map.put(Opcodes.LDIV, "/");
			map.put(Opcodes.FDIV, "/");
			map.put(Opcodes.DDIV, "/");
			map.put(Opcodes.IREM, "%");
			map.put(Opcodes.LREM, "%");
			map.put(Opcodes.FREM, "%");
			map.put(Opcodes.DREM, "%");
			map.put(Opcodes.INEG, "-");
			map.put(Opcodes.LNEG, "-");
			map.put(Opcodes.FNEG, "-");
			map.put(Opcodes.DNEG, "-");
			map.put(Opcodes.ISHL, "<<");
			map.put(Opcodes.LSHL, "<<");
			map.put(Opcodes.ISHR, ">>");
			map.put(Opcodes.LSHR, ">>");
			map.put(Opcodes.IUSHR, ">>>");
			map.put(Opcodes.LUSHR, ">>>");
			map.put(Opcodes.IAND, "&&");
			map.put(Opcodes.LAND, "&&");
			map.put(Opcodes.IOR, "||");
			map.put(Opcodes.LOR, "||");
			map.put(Opcodes.IXOR, "^");
			map.put(Opcodes.LXOR, "^");
			mathSymbolsMap = map;
		}
	}


	/** An inner class with a map of opcode {@link Type#CONDITION} types to comparison symbols.
	 * Inner class necessary because static blocks in enums are called after enums are initialized I think...?
	 * @author TeamworkGuy2
	 * @since 2020-9-7
	 */
	private enum ComparisonSymbols {
		;

		/** A map of comparison opcodes to comparison operator symbols */
		private static final EnumMap<Opcodes, Map.Entry<String, String>> comparisonSymbolsMap;

		static {
			EnumMap<Opcodes, Map.Entry<String, String>> map = new EnumMap<>(Opcodes.class);
			map.put(Opcodes.IFEQ, entry("==", "!="));
			map.put(Opcodes.IFNE, entry("!=", "=="));
			map.put(Opcodes.IFLT, entry("<", ">="));
			map.put(Opcodes.IFGE, entry(">=", "<"));
			map.put(Opcodes.IFGT, entry(">", "<="));
			map.put(Opcodes.IFLE, entry("<=", ">"));
			map.put(Opcodes.IFNULL, entry("==", "!="));
			map.put(Opcodes.IFNONNULL, entry("!=", "=="));
			map.put(Opcodes.IF_ICMPEQ, entry("==", "!="));
			map.put(Opcodes.IF_ICMPNE, entry("!=", "=="));
			map.put(Opcodes.IF_ICMPLT, entry("<", ">="));
			map.put(Opcodes.IF_ICMPGE, entry(">=", "<"));
			map.put(Opcodes.IF_ICMPGT, entry(">", "<="));
			map.put(Opcodes.IF_ICMPLE, entry("<=", ">"));
			map.put(Opcodes.IF_ACMPEQ, entry("==", "!="));
			map.put(Opcodes.IF_ACMPNE, entry("!=", "=="));
			comparisonSymbolsMap = map;
		}
	}


	/** An inner class with a map of opcode constants.
	 * Inner class necessary because static blocks in enums are called after enums are initialized I think...?
	 * @author TeamworkGuy2
	 * @since 2020-07-04
	 */
	private enum OpcodeConstants {
		;

		/** A map of Opcodes to a mutable (key) and immutable (value) list of Opcodes that are of that type */
		private static final EnumMap<Opcodes, Object> constValues;
		
		static {
			constValues = new EnumMap<>(Opcodes.class);
			for(Opcodes op : Opcodes.values()) {
				String name = op.name();
				int idx = name.indexOf("_");
				boolean isLoadOrStore = false;
				if(idx > -1 && (op.hasBehavior(Type.CONST_LOAD) || (isLoadOrStore = op.hasBehavior(Type.VAR_LOAD) || op.hasBehavior(Type.VAR_STORE)))) {
					String str = name.substring(idx + 1);
					str = str.charAt(0) == 'M' ? '-' + str.substring(1) : str;
					str = str.toLowerCase();

					Number value = null;
					if(str.equals("null")) {
						value = null;
					}
					else if(isLoadOrStore || name.startsWith("I")) {
						value = Integer.parseInt(str);
					}
					else if(name.startsWith("L")) {
						value = Long.parseLong(str);
					}
					else if(name.startsWith("F")) {
						value = Float.parseFloat(str);
					}
					else if(name.startsWith("D")) {
						value = Double.parseDouble(str);
					}
					constValues.put(op, value);
				}
			}
		}
	}


	private final int opcode;
	private final int operandCount;
	private final int popCount;
	private final int pushCount;
	private final String displayName;
	private final int typesBitSet;
	private final OpcodeOperations operations;

	/** Create an opcode enum with an opcode value and related fields.
	 * @param opcode the opcode's value
	 * @param operandCount the number of operands this opcode consumes
	 * @param types the opcode categories that this opcode falls under
	 * @param ops a set of operations that this opcode supports
	 */
	Opcodes(int opcode, int operandCount, EnumSet<Type> types, Op ops) {
		validateTypes(types);
		this.opcode = opcode;
		this.operandCount = operandCount;
		this.popCount = calcPopCount(types);
		this.pushCount = calcPushCount(types);
		this.displayName = this.name().toLowerCase();
		this.typesBitSet = bitSet(types);
		this.operations = ops != null ? ops.create() : OpcodeOperations.EMPTY;
		if(opcode >= 0) {
			setupTypes(types); // don't setup UNDEFINED
		}
	}


	/** Add the list of {@link Type OpcodeTypes} associated with this Opcode into the {@link #typeMap}
	 * containing all of the opcodes associated with a specific type.
	 */
	private final void setupTypes(EnumSet<Type> types) {
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


	public int opcode() {
		return this.opcode;
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
		return (this.typesBitSet & type.flag) == type.flag;
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


	/**
	 * @return the number of operands this instruction pops off the operand stack or -1 if the pop count is unpredictable
	 */
	public int popCount() {
		return popCount;
	}


	/**
	 * @return the number of operands this instruction pushes onto the operand stack or -1 if the push count is unpredictable
	 */
	public int pushCount() {
		return pushCount;
	}


	/**
	 * @return the constant value of this opcode if it {@link #hasBehavior(Type)} of {@link Type#CONST_LOAD}
	 */
	public Object getConstantValue() {
		return OpcodeConstants.constValues.get(this);
	}


	/**
	 * @return the math operation symbol of this opcode if it {@link #hasBehavior(Type)} of {@link Type#MATH_OP}
	 */
	public String getMathSymbol() {
		return MathSymbols.mathSymbolsMap.get(this);
	}


	/**
	 * @return the comparison symbol of this opcode if it {@link #hasBehavior(Type)} of {@link Type#CONDITION}
	 */
	public String getComparisonSymbol() {
		return ComparisonSymbols.comparisonSymbolsMap.get(this).getKey();
	}


	/**
	 * @return the inverse (not '!') comparison symbol of this opcode if it {@link #hasBehavior(Type)} of {@link Type#CONDITION}
	 */
	public String getComparisonSymbolInverse() {
		return ComparisonSymbols.comparisonSymbolsMap.get(this).getValue();
	}


	public int getJumpDestination(byte[] code, int location) {
		int offset = this.operations.getCodeOffsetGetter().getOffset(code, location);
		return location + offset;
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


	private static int calcPopCount(EnumSet<Type> types) {
		if(types.contains(Type.POP1)) {
			return 1;
		}
		else if(types.contains(Type.POP2)) {
			return 2;
		}
		else if(types.contains(Type.POP3)) {
			return 3;
		}
		else if(types.contains(Type.POP_UNPREDICTABLE)) {
			return -1;
		}
		else {
			return 0;
		}
	}


	private static int calcPushCount(EnumSet<Type> types) {
		if(types.contains(Type.PUSH1)) {
			return 1;
		}
		else {
			return 0;
		}
	}


	private static int bitSet(EnumSet<Type> types) {
		int res = 0;
		for(var type : types) {
			res |= 1 << (type.ordinal() + 1);
		}
		return res;
	}

	private static <K, V> Map.Entry<K, V> entry(K key, V value) {
		return new AbstractMap.SimpleImmutableEntry<K, V>(key, value);
	}


	private static void validateTypes(EnumSet<Type> types) {
		if(types.contains(Type.ARRAY_LOAD)) {
			if(!(types.contains(Type.POP2) && types.contains(Type.PUSH1))) throw new IllegalArgumentException("Array load opcode expected to pop two operands and push one operand: ..., arrayref, index ->; ..., value ->");
		}
		if(types.contains(Type.ARRAY_STORE)) {
			if(!types.contains(Type.POP3)) throw new IllegalArgumentException("Array store opcode expected to pop three operands: ..., arrayref, index, value ->");
		}
		if(types.contains(Type.VAR_LOAD)) {
			if(!types.contains(Type.PUSH1)) throw new IllegalArgumentException("Variable load opcode expected to push one operand: ... ->; ..., value");
		}
		if(types.contains(Type.VAR_STORE)) {
			if(!types.contains(Type.POP1)) throw new IllegalArgumentException("Variable store opcode expected to pop one operand: ..., value ->");
		}
		if(types.contains(Type.MATH_OP)) {
			if(!(types.contains(Type.POP1) || types.contains(Type.POP2))) throw new IllegalArgumentException("Math operation opcode expected to pop one or two operands: ..., value1[, value2] ->; ..., result");
			if(!types.contains(Type.PUSH1)) throw new IllegalArgumentException("Math operation opcode expected to push one operand: ... ->; ..., result");
		}
		if(types.contains(Type.CONDITION)) {
			if(!types.contains(Type.JUMP)) throw new IllegalArgumentException("Condition expected to also be a Jump");
		}
	}

}
