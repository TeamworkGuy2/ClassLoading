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

	// Return values
	// ireturn 172 0xAC
	// lreturn 173 0xAD
	// freturn 174 0xAE
	// dreturn 175 0xAF
	// areturn 176 0xB0
	// return 177 0xB1
	// athrow 191 0xBF


	/* 0 0x0 */NOP                  (0, 0, null, null),
	/* 1 0x1 */ACONST_NULL          (1, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 2 0x2 */ICONST_m1            (1, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 3 0x3 */ICONST_0             (3, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 4 0x4 */ICONST_1             (4, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 5 0x5 */ICONST_2             (5, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 6 0x6 */ICONST_3             (6, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 7 0x7 */ICONST_4             (7, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 8 0x8 */ICONST_5             (8, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 9 0x9 */LCONST_0             (9, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 10 0xA */LCONST_1            (10, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 11 0xB */FCONST_0            (11, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 12 0xC */FCONST_1            (12, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 13 0xD */FCONST_2            (13, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 14 0xE */DCONST_0            (14, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 15 0xF */DCONST_1            (15, 0, enums(Type.PUSH, Type.PUSH_CONST), null),
	/* 16 0x10 */BIPUSH             (16, 1, enums(Type.PUSH), null),
	/* 17 0x11 */SIPUSH             (17, 2, enums(Type.PUSH), null),
	/* 18 0x12 */LDC                (18, 1, enums(Type.PUSH, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 1))),
	/* 19 0x13 */LDC_W              (19, 2, enums(Type.PUSH, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 20 0x14 */LDC2_W             (20, 2, enums(Type.PUSH, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 21 0x15 */ILOAD              (21, 1, enums(Type.PUSH, Type.LVA_INDEX), null),
	/* 22 0x16 */LLOAD              (22, 1, enums(Type.PUSH, Type.LVA_INDEX), null),
	/* 23 0x17 */FLOAD              (23, 1, enums(Type.PUSH, Type.LVA_INDEX), null),
	/* 24 0x18 */DLOAD              (24, 1, enums(Type.PUSH, Type.LVA_INDEX), null),
	/* 25 0x19 */ALOAD              (25, 1, enums(Type.PUSH, Type.LVA_INDEX, Type.ARRAY), null),
	/* 26 0x1A */ILOAD_0            (26, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 27 0x1B */ILOAD_1            (27, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 28 0x1C */ILOAD_2            (28, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 29 0x1D */ILOAD_3            (29, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 30 0x1E */LLOAD_0            (30, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 31 0x1F */LLOAD_1            (31, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 32 0x20 */LLOAD_2            (32, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 33 0x21 */LLOAD_3            (33, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 34 0x22 */FLOAD_0            (34, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 35 0x23 */FLOAD_1            (35, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 36 0x24 */FLOAD_2            (36, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 37 0x25 */FLOAD_3            (37, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 38 0x26 */DLOAD_0            (38, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 39 0x27 */DLOAD_1            (39, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 40 0x28 */DLOAD_2            (40, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 41 0x29 */DLOAD_3            (41, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 42 0x2A */ALOAD_0            (42, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 43 0x2B */ALOAD_1            (43, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 44 0x2C */ALOAD_2            (44, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 45 0x2D */ALOAD_3            (45, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 46 0x2E */IALOAD             (46, 0, enums(Type.PUSH, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 47 0x2F */LALOAD             (47, 0, enums(Type.POP, Type.PUSH, Type.ARRAY), null),
	/* 48 0x30 */FALOAD             (48, 0, enums(Type.POP, Type.PUSH, Type.ARRAY), null),
	/* 49 0x31 */DALOAD             (49, 0, enums(Type.POP, Type.PUSH, Type.ARRAY), null),
	/* 50 0x32 */AALOAD             (50, 0, enums(Type.POP, Type.PUSH, Type.ARRAY), null),
	/* 51 0x33 */BALOAD             (51, 0, enums(Type.POP, Type.PUSH, Type.ARRAY), null),
	/* 52 0x34 */CALOAD             (52, 0, enums(Type.POP, Type.PUSH, Type.ARRAY), null),
	/* 53 0x35 */SALOAD             (53, 0, enums(Type.POP, Type.PUSH, Type.ARRAY), null),
	/* 54 0x36 */ISTORE             (54, 1, enums(Type.POP, Type.LVA_INDEX), null),
	/* 55 0x37 */LSTORE             (55, 1, enums(Type.POP, Type.LVA_INDEX), null),
	/* 56 0x38 */FSTORE             (56, 1, enums(Type.POP, Type.LVA_INDEX), null),
	/* 57 0x39 */DSTORE             (57, 1, enums(Type.POP, Type.LVA_INDEX), null),
	/* 58 0x3A */ASTORE             (58, 1, enums(Type.POP, Type.LVA_INDEX), null),
	/* 59 0x3B */ISTORE_0           (59, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 60 0x3C */ISTORE_1           (60, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 61 0x3D */ISTORE_2           (61, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 62 0x3E */ISTORE_3           (62, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 63 0x3F */LSTORE_0           (63, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 64 0x40 */LSTORE_1           (64, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 65 0x41 */LSTORE_2           (65, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 66 0x42 */LSTORE_3           (66, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 67 0x43 */FSTORE_0           (67, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 68 0x44 */FSTORE_1           (68, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 69 0x45 */FSTORE_2           (69, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 70 0x46 */FSTORE_3           (70, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 71 0x47 */DSTORE_0           (71, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 72 0x48 */DSTORE_1           (72, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 73 0x49 */DSTORE_2           (73, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 74 0x4A */DSTORE_3           (74, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 75 0x4B */ASTORE_0           (75, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 76 0x4C */ASTORE_1           (76, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 77 0x4D */ASTORE_2           (77, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 78 0x4E */ASTORE_3           (78, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX), null),
	/* 79 0x4F */IASTORE            (79, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX, Type.ARRAY), null),
	/* 80 0x50 */LASTORE            (80, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX, Type.ARRAY), null),
	/* 81 0x51 */FASTORE            (81, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX, Type.ARRAY), null),
	/* 82 0x52 */DASTORE            (82, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX, Type.ARRAY), null),
	/* 83 0x53 */AASTORE            (83, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX, Type.ARRAY), null),
	/* 84 0x54 */BASTORE            (84, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX, Type.ARRAY), null),
	/* 85 0x55 */CASTORE            (85, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX, Type.ARRAY), null),
	/* 86 0x56 */SASTORE            (86, 0, enums(Type.POP, Type.LVA_CONST_INDEX, Type.LVA_INDEX, Type.ARRAY), null),
	/* 87 0x57 */POP                (87, 0, enums(Type.POP), null),
	/* 88 0x58 */POP2               (88, 0, enums(Type.POP), null),
	/* 89 0x59 */DUP                (89, 0, enums(Type.PUSH), null),
	/* 90 0x5A */DUP_X1             (90, 0, enums(Type.PUSH), null),
	/* 91 0x5B */DUP_X2             (91, 0, enums(Type.PUSH), null),
	/* 92 0x5C */DUP2               (92, 0, enums(Type.PUSH), null),
	/* 93 0x5D */DUP2_X1            (93, 0, enums(Type.PUSH), null),
	/* 94 0x5E */DUP2_X2            (94, 0, enums(Type.PUSH), null),
	/* 95 0x5F */SWAP               (95, 0, none(Type.class), null),
	/* 96 0x60 */IADD               (96, 0, enums(Type.POP, Type.PUSH), null),
	/* 97 0x61 */LADD               (97, 0, enums(Type.POP, Type.PUSH), null),
	/* 98 0x62 */FADD               (98, 0, enums(Type.POP, Type.PUSH), null),
	/* 99 0x63 */DADD               (99, 0, enums(Type.POP, Type.PUSH), null),
	/* 100 0x64 */ISUB              (100, 0, enums(Type.POP, Type.PUSH), null),
	/* 101 0x65 */LSUB              (101, 0, enums(Type.POP, Type.PUSH), null),
	/* 102 0x66 */FSUB              (102, 0, enums(Type.POP, Type.PUSH), null),
	/* 103 0x67 */DSUB              (103, 0, enums(Type.POP, Type.PUSH), null),
	/* 104 0x68 */IMUL              (104, 0, enums(Type.POP, Type.PUSH), null),
	/* 105 0x69 */LMUL              (105, 0, enums(Type.POP, Type.PUSH), null),
	/* 106 0x6A */FMUL              (106, 0, enums(Type.POP, Type.PUSH), null),
	/* 107 0x6B */DMUL              (107, 0, enums(Type.POP, Type.PUSH), null),
	/* 108 0x6C */IDIV              (108, 0, enums(Type.POP, Type.PUSH), null),
	/* 109 0x6D */LDIV              (109, 0, enums(Type.POP, Type.PUSH), null),
	/* 110 0x6E */FDIV              (110, 0, enums(Type.POP, Type.PUSH), null),
	/* 111 0x6F */DDIV              (111, 0, enums(Type.POP, Type.PUSH), null),
	/* 112 0x70 */IREM              (112, 0, enums(Type.POP, Type.PUSH), null),
	/* 113 0x71 */LREM              (113, 0, enums(Type.POP, Type.PUSH), null),
	/* 114 0x72 */FREM              (114, 0, enums(Type.POP, Type.PUSH), null),
	/* 115 0x73 */DREM              (115, 0, enums(Type.POP, Type.PUSH), null),
	/* 116 0x74 */INEG              (116, 0, enums(Type.POP, Type.PUSH), null),
	/* 117 0x75 */LNEG              (117, 0, enums(Type.POP, Type.PUSH), null),
	/* 118 0x76 */FNEG              (118, 0, enums(Type.POP, Type.PUSH), null),
	/* 119 0x77 */DNEG              (119, 0, enums(Type.POP, Type.PUSH), null),
	/* 120 0x78 */ISHL              (120, 0, enums(Type.POP, Type.PUSH), null),
	/* 121 0x79 */LSHL              (121, 0, enums(Type.POP, Type.PUSH), null),
	/* 122 0x7A */ISHR              (122, 0, enums(Type.POP, Type.PUSH), null),
	/* 123 0x7B */LSHR              (123, 0, enums(Type.POP, Type.PUSH), null),
	/* 124 0x7C */IUSHR             (124, 0, enums(Type.POP, Type.PUSH), null),
	/* 125 0x7D */LUSHR             (125, 0, enums(Type.POP, Type.PUSH), null),
	/* 126 0x7E */IAND              (126, 0, enums(Type.POP, Type.PUSH), null),
	/* 127 0x7F */LAND              (127, 0, enums(Type.POP, Type.PUSH), null),
	/* 128 0x80 */IOR               (128, 0, enums(Type.POP, Type.PUSH), null),
	/* 129 0x81 */LOR               (129, 0, enums(Type.POP, Type.PUSH), null),
	/* 130 0x82 */IXOR              (130, 0, enums(Type.POP, Type.PUSH), null),
	/* 131 0x83 */LXOR              (131, 0, enums(Type.POP, Type.PUSH), null),
	/* 132 0x84 */IINC              (132, 2, enums(Type.LVA_INDEX), null),
	/* 133 0x85 */I2L               (133, 0, none(Type.class), null),
	/* 134 0x86 */I2F               (134, 0, none(Type.class), null),
	/* 135 0x87 */I2D               (135, 0, none(Type.class), null),
	/* 136 0x88 */L2I               (136, 0, none(Type.class), null),
	/* 137 0x89 */L2F               (137, 0, none(Type.class), null),
	/* 138 0x8A */L2D               (138, 0, none(Type.class), null),
	/* 139 0x8B */F2I               (139, 0, none(Type.class), null),
	/* 140 0x8C */F2L               (140, 0, none(Type.class), null),
	/* 141 0x8D */F2D               (141, 0, none(Type.class), null),
	/* 142 0x8E */D2I               (142, 0, none(Type.class), null),
	/* 143 0x8F */D2L               (143, 0, none(Type.class), null),
	/* 144 0x90 */D2F               (144, 0, none(Type.class), null),
	/* 145 0x91 */I2B               (145, 0, none(Type.class), null),
	/* 146 0x92 */I2C               (146, 0, none(Type.class), null),
	/* 147 0x93 */I2S               (147, 0, none(Type.class), null),
	/* 148 0x94 */LCMP              (148, 0, enums(Type.POP, Type.PUSH), null),
	/* 149 0x95 */FCMPL             (149, 0, enums(Type.POP, Type.PUSH), null),
	/* 150 0x96 */FCMPG             (150, 0, enums(Type.POP, Type.PUSH), null),
	/* 151 0x97 */DCMPL             (151, 0, enums(Type.POP, Type.PUSH), null),
	/* 152 0x98 */DCMPG             (152, 0, enums(Type.POP, Type.PUSH), null),
	/* 153 0x99 */IFEQ              (153, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(153, 1, 2))),
	/* 154 0x9A */IFNE              (154, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(154, 1, 2))),
	/* 155 0x9B */IFLT              (155, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(155, 1, 2))),
	/* 156 0x9C */IFGE              (156, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(156, 1, 2))),
	/* 157 0x9D */IFGT              (157, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(157, 1, 2))),
	/* 158 0x9E */IFLE              (158, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(158, 1, 2))),
	/* 159 0x9F */IF_ICMPEQ         (159, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(159, 1, 2))),
	/* 160 0xA0 */IF_ICMPNE         (160, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(160, 1, 2))),
	/* 161 0xA1 */IF_ICMPLT         (161, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(161, 1, 2))),
	/* 162 0xA2 */IF_ICMPGE         (162, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(162, 1, 2))),
	/* 163 0xA3 */IF_ICMPGT         (163, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(163, 1, 2))),
	/* 164 0xA4 */IF_ICMPLE         (164, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(164, 1, 2))),
	/* 165 0xA5 */IF_ACMPEQ         (165, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(165, 1, 2))),
	/* 166 0xA6 */IF_ACMPNE         (166, 2, enums(Type.POP, Type.CONDITION, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(166, 1, 2))),
	/* 167 0xA7 */GOTO              (167, 2, enums(Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(167, 1, 2))),
	/* 168 0xa8 */JSR               (168, 2, enums(Type.PUSH, Type.JUMP, Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(168, 1, 2))),
	/* 169 0xA9 */RET               (169, 1, null, null),
	/* 170 0xAA */TABLESWITCH       (170, Const.UNPREDICTABLE, enums(Type.POP, Type.CODE_OFFSET), Op.of(IoUtility.TableswitchOffsetModifier)),
	/* 171 0xAB */LOOKUPSWITCH      (171, Const.UNPREDICTABLE, enums(Type.POP, Type.CODE_OFFSET), Op.of(IoUtility.LookupswitchOffsetModifier)),
	/* 172 0xAC */IRETURN           (172, 0, enums(Type.POP, Type.RETURN), null),
	/* 173 0xAD */LRETURN           (173, 0, enums(Type.POP, Type.RETURN), null),
	/* 174 0xAE */FRETURN           (174, 0, enums(Type.POP, Type.RETURN), null),
	/* 175 0xAF */DRETURN           (175, 0, enums(Type.POP, Type.RETURN), null),
	/* 176 0xB0 */ARETURN           (176, 0, enums(Type.POP, Type.RETURN), null),
	/* 177 0xB1 */RETURN            (177, 0, enums(Type.RETURN), null),
	/* 178 0xB2 */GETSTATIC         (178, 2, enums(Type.PUSH, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 179 0xB3 */PUTSTATIC         (179, 2, enums(Type.POP, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 180 0xB4 */GETFIELD          (180, 2, enums(Type.POP, Type.PUSH, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 181 0xB5 */PUTFIELD          (181, 2, enums(Type.POP, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 182 0xB6 */INVOKEVIRTUAL     (182, 2, enums(Type.POP, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 183 0xB7 */INVOKESPECIAL     (183, 2, enums(Type.POP, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 184 0xB8 */INVOKESTATIC      (184, 2, enums(Type.POP, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 185 0xB9 */INVOKEINTERFACE   (185, 4, enums(Type.POP, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 186 0xBA */INVOKEDYNAMIC     (186, Const.UNDEFINED, enums(Type.POP, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 187 0xBB */NEW               (187, 2, enums(Type.PUSH, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 188 0xBC */NEWARRAY          (188, 1, enums(Type.POP, Type.PUSH), null),
	/* 189 0xBD */ANEWARRAY         (189, 2, enums(Type.POP, Type.PUSH, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 190 0xBE */ARRAYLENGTH       (190, 0, none(Type.class), null),
	/* 191 0xBF */ATHROW            (191, 0, none(Type.class), null),
	/* 192 0xC0 */CHECKCAST         (192, 2, enums(Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 193 0xC1 */INSTANCEOF        (193, 2, enums(Type.POP, Type.PUSH, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 194 0xC2 */MONITORENTER      (194, 0, enums(Type.POP), null),
	/* 195 0xC3 */MONITOREXIT       (195, 0, enums(Type.POP), null),
	/* 196 0xC4 */WIDE              (196, Const.UNPREDICTABLE, enums(Type.LVA_INDEX), null),
	/* 197 0xC5 */MULTIANEWARRAY    (197, 3, enums(Type.POP, Type.PUSH, Type.CP_INDEX), Op.of(IoUtility.cpIndex(197, 1, 2))),
	/* 198 0xc6 */IFNULL            (198, 2, enums(Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(198, 1, 2))),
	/* 199 0xC7 */IFNONNULL         (199, 2, enums(Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(199, 1, 2))),
	/* 200 0xC8 */GOTO_W            (200, 4, enums(Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(200, 1, 4))),
	/* 201 0xC9 */JSR_W             (201, 4, enums(Type.CODE_OFFSET), Op.of(IoUtility.offsetModifier(201, 1, 4))),
	/* 202 0xCA */BREAKPOINT        (202, 0, none(Type.class), null),
	UNDEFINED_203(203, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_204(204, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_205(205, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_206(206, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_207(207, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_208(208, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_209(209, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_210(210, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_211(211, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_212(212, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_213(213, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_214(214, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_215(215, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_216(216, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_217(217, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_218(218, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_219(219, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_220(220, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_221(221, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_222(222, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_223(223, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_224(224, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_225(225, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_226(226, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_227(227, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_228(228, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_229(229, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_230(230, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_231(231, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_232(232, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_233(233, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_234(234, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_235(235, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_236(236, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_237(237, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_238(238, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_239(239, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_240(240, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_241(241, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_242(242, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_243(243, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_244(244, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_245(245, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_246(246, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_247(247, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_248(248, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_249(249, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_250(250, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_251(251, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_252(252, Const.UNDEFINED, none(Type.class), null),
	UNDEFINED_253(253, Const.UNDEFINED, none(Type.class), null),
	/* 254 0xFE */IMPDEP1           (254, Const.RESERVED, none(Type.class), null),
	/* 255 0xFF */IMPDEP2           (255, Const.RESERVED, none(Type.class), null);

	@SafeVarargs
	private static final <E extends Enum<E>> EnumSet<E> enums(E... es) {
		return EnumSet.copyOf(Arrays.asList(es));
	}


	private static final <E extends Enum<E>> EnumSet<E> none(Class<E> cls) {
		return EnumSet.noneOf(cls);
	}


	/**
	 * @author TeamworkGuy2
	 * @since 2014-4-19
	 */
	public enum Type {
		ARRAY,
		CODE_OFFSET,
		CP_INDEX,
		CONDITION,
		JUMP,
		LVA_CONST_INDEX,
		LVA_INDEX,
		PUSH,
		PUSH_CONST,
		@SuppressWarnings("hiding")
		POP,
		@SuppressWarnings("hiding")
		RETURN;
	}


	public static final class Const {
		/** Undefined */
		public static final int UNDEFINED = -1;
		/** Unpredictable */
		public static final int UNPREDICTABLE = -2;
		/** Restricted */
		public static final int RESERVED = -3;
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
		if(opcode < 0) { throw new IllegalArgumentException("opcode value cannot be less than 0"); }
		this.opcode = opcode;
		this.operandCount = operandCount;
		this.types = types;
		this.operations = ops != null ? ops.create() : OpcodeOperations.EMPTY;
		setupTypes();
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


	/**
	 * @return the set of operations that this opcode supports
	 */
	public final OpcodeOperations getOperations() {
		return operations;
	}


	/**
	 * @param opcode the opcode to compare to this opcode
	 * @return true if this opcode equals the specified opcode value
	 */
	public final boolean is(int opcode) {
		return this.opcode == opcode;
	}


	/**
	 * @return the number of opcodes this instruction uses (i.e. the number of instruction bytes
	 * in a code array after this opcode that belong to this opcode). Note: negative values represent
	 * unknown or illegal operand counts, see {@link Const#UNDEFINED},
	 * {@link Const#UNPREDICTABLE}, {@link Const#RESERVED}.
	 */
	public final int getOperandCount() {
		return operandCount;
	}


	/** Return the list of opcodes that are of a specific type
	 * @param type the opcode type to filter by
	 * @return an immutable list of opcodes associated with the specific opcode type
	 */
	public static final List<Opcodes> getOpcodeSet(Type type) {
		return OpcodeMap.typeMap.get(type).getValue();
	}


	/** Return the opcode with the specified opcode value
	 * @param opcode the opcode value to lookup
	 * @return the instruction corresponding to the specified opcode
	 */
	public static final Opcodes getOpcode(int opcode) {
		return OpcodeMap.opcodes[opcode];
	}

}
