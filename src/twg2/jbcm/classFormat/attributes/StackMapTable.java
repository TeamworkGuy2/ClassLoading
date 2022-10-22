package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ClassFileAttributes;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.CpIndexChangeable;
import twg2.jbcm.modify.CpIndexChanger;

/** A Java class file format Attribute of type <code>StackMapTable</code>
 * @author TeamworkGuy2
 * @since 2013-10-6
 */
public class StackMapTable implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "StackMapTable";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table. The constant_pool
	 * entry at that index must be a CONSTANT_Utf8_info (§4.4.7) structure representing the string "StackMapTable".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item of a ConstantValue_attribute structure must be 2. */
	int attribute_length;
	/** The value of the number_of_entries item gives the number of stack_map_frame entries in the entries table.
	 */
	short number_of_entries;
	/** The entries array gives the method's stack_map_frame structures.
	 * Each stack_map_frame structure specifies the type state at a particular bytecode offset.
	 * Each frame type specifies (explicitly or implicitly) a value, offset_delta, that is used
	 * to calculate the actual bytecode offset at which a frame applies. The bytecode offset at
	 * which a frame applies is calculated by adding offset_delta + 1 to the bytecode offset of
	 * the previous frame, unless the previous frame is the initial frame of the method, in which
	 * case the bytecode offset is offset_delta.
	 * By using an offset delta rather than the actual bytecode offset we ensure, by definition,
	 * that stack map frames are in the correctly sorted order. Furthermore, by consistently
	 * using the formula offset_delta + 1 for all explicit frames, we guarantee the absence
	 * of duplicates. 
	 */
	StackMapFrame[] entries;


	public StackMapTable(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = resolver.getAttributeNameIndex(attributeNameIndex);
		this.resolver = resolver;
	}


	@Override
	public String getAttributeName() {
		return attribute_name_index.getCpObject().getString();
	}


	@Override
	public int getAttributeLength() {
		return attribute_length;
	}


	@Override
	public void changeCpIndex(CpIndexChanger indexChanger) {
		indexChanger.indexChange(attribute_name_index);
		indexChanger.indexChange(entries);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);
		out.writeShort(number_of_entries);
		for(int i = 0; i < number_of_entries; i++) {
			entries[i].writeData(out);
		}
	}


	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.readAttributeName) {
			attribute_name_index = ClassFileAttributes.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();
		number_of_entries = in.readShort();
		entries = new StackMapFrame[number_of_entries];
		for(int i = 0; i < number_of_entries; i++) {
			entries[i] = new StackMapFrame(resolver);
			entries[i].readData(in);
		}
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ATTRIBUTE_NAME).append("(entries=").append(number_of_entries).append(",\n");
		for(int i = 0; i < number_of_entries; i++) {
			sb.append("\t").append(entries[i]).append(",\n");
		}
		sb.append(")");
		return sb.toString();
	}



	/** A Java class file format Attribute of type <code>StackMapFrame</code> for a {@link StackMapTable}
	 * @author TeamworkGuy2
	 * @since 2013-10-6
	 */
	public static class StackMapFrame implements ReadWritable, CpIndexChangeable {
		ClassFile resolver;
		/**
		 * 0-63 = same_frame
		 * 64-127 = same_locals_1_stack_item_frame
		 * 247 = same_locals_1_stack_item_frame_extended
		 * 248-250 = chop_frame
		 * 251 = same_frame_extended
		 * 252-254 = append_frame
		 * 255 = full_frame
		 */
		int frame_type;
		/** same_locals_1_stack_item_frame:
		 * The offset_delta value for the frame is the value (frame_type - 64). There is a verification_type_info
		 * following the frame_type for the one stack item.
		 *
		 * same_locals_1_stack_item_frame_extended:
		 * chop_frame:
		 * same_frame_extended:
		 * append_frame:
		 * full_frame:
		 * The offset_delta value for the frame is given explicitly.
		 */
		short offset_delta;
		/** full_frame:
		 */
		short number_of_locals;
		/** The value of the line_number_table_length item indicates the number of entries in the line_number_table array. */
		VerificationTypeInfo[] locals;
		/** full_frame:
		 */
		short number_of_stack_items;
		/** full_frame:
		 */
		VerificationTypeInfo[] stack;


		public StackMapFrame(ClassFile resolver) {
			this.resolver = resolver;
		}


		@Override
		public void changeCpIndex(CpIndexChanger indexChanger) {
			indexChanger.indexChange(stack);
		}


		@Override
		public void writeData(DataOutput out) throws IOException {
			out.writeByte(frame_type);
			// same_frame
			if(frame_type >= 0 && frame_type <= 63) {
				// No other data to write
			}
			// same_locals_1_stack_item_frame
			else if(frame_type >= 64 && frame_type <= 127) {
				stack[0].writeData(out);
			}
			// same_locals_1_stack_item_frame_extended
			else if(frame_type == 247) {
				out.writeShort(offset_delta);
				stack[0].writeData(out);
			}
			// chop_frame
			else if(frame_type >= 248 && frame_type <= 250) {
				out.writeShort(offset_delta);
			}
			// same_frame_extended
			else if(frame_type == 251) {
				out.writeShort(offset_delta);
			}
			// append_frame
			else if(frame_type >= 252 && frame_type <= 254) {
				// k additional locals are defined = (frame_type-251)
				out.writeShort(offset_delta);
				int k = (frame_type-251);
				for(int i = 0; i < k; i++) {
					locals[i].writeData(out);
				}
			}
			// full_frame
			else if(frame_type == 255) {
				out.writeShort(offset_delta);
				out.writeShort(number_of_locals);
				for(int i = 0; i < number_of_locals; i++) {
					locals[i].writeData(out);
				}
				out.writeShort(number_of_stack_items);
				for(int i = 0; i < number_of_stack_items; i++) {
					stack[i].writeData(out);
				}
			}
			else {
				throw new IllegalStateException("Unknown StackMapFrame frame type " + frame_type);
			}
		}


		@Override
		public void readData(DataInput in) throws IOException {
			frame_type = (in.readByte() & 0xFF);
			// same_frame
			if(frame_type >= 0 && frame_type <= 63) {
				offset_delta = (short)frame_type;
			}
			// same_locals_1_stack_item_frame
			else if(frame_type >= 64 && frame_type <= 127) {
				offset_delta = (short)(frame_type-64);
				stack = new VerificationTypeInfo[1];
				stack[0] = new VerificationTypeInfo(resolver);
				stack[0].readData(in);
			}
			// same_locals_1_stack_item_frame_extended
			else if(frame_type == 247) {
				offset_delta = in.readShort();
				stack = new VerificationTypeInfo[1];
				stack[0] = new VerificationTypeInfo(resolver);
				stack[0].readData(in);
			}
			// chop_frame
			else if(frame_type >= 248 && frame_type <= 250) {
				// k last locals are absent = (251-frame_type)
				offset_delta = in.readShort();
				number_of_locals -= (251-frame_type);
			}
			// same_frame_extended
			else if(frame_type == 251) {
				// Same locals as previous stack map frame and stack size is zero
				offset_delta = in.readShort();
			}
			// append_frame
			else if(frame_type >= 252 && frame_type <= 254) {
				// k additional locals are defined = (frame_type-251)
				offset_delta = in.readShort();
				int k = (frame_type-251);
				number_of_locals += k;
				locals = new VerificationTypeInfo[k];
				for(int i = 0; i < k; i++) {
					locals[i] = new VerificationTypeInfo(resolver);
					locals[i].readData(in);
				}
			}
			// full_frame
			else if(frame_type == 255) {
				offset_delta = in.readShort();
				number_of_locals = in.readShort();
				locals = new VerificationTypeInfo[number_of_locals];
				for(int i = 0; i < number_of_locals; i++) {
					locals[i] = new VerificationTypeInfo(resolver);
					locals[i].readData(in);
				}
				number_of_stack_items = in.readShort();
				stack = new VerificationTypeInfo[number_of_stack_items];
				for(int i = 0; i < number_of_stack_items; i++) {
					stack[i] = new VerificationTypeInfo(resolver);
					stack[i].readData(in);
				}
			}
			else {
				throw new IllegalStateException("Unknown StackMapFrame frame type " + frame_type);
			}
		}


		@Override
		public String toString() {
			StringBuilder str = new StringBuilder();
			str.append("StackMapFrame(frame_type=").append(frame_type)
				.append(", offset=").append(offset_delta)
				.append(", locals=").append(number_of_locals)
				.append(" [");
			// Local stack items
			for(int i = 0; i < number_of_locals - 1; i++) {
				str.append(locals[i]).append(", ");
			}
			if(number_of_locals> 0) { str.append(locals[number_of_locals - 1]); }
			// Stack items
			str.append("], stack_items=").append(number_of_stack_items);
			for(int i = 0; i < number_of_stack_items - 1; i++) {
				str.append(stack[i]).append(", ");
			}
			if(number_of_stack_items > 0) { str.append(stack[number_of_stack_items - 1]); }
			str.append("])");
			return str.toString();
		}

	}

}

