package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>StackMapFrame</code> for a {@link StackMapTable}
 * @author TeamworkGuy2
 * @since 2013-10-6
 */
public class StackMapFrame implements ReadWritable {
	ClassFile resolver;
	/* 
	 * 0-63 = same_frame
	 * 64-127 = same_locals_1_stack_item_frame
	 * 247 = same_locals_1_stack_item_frame_extended
	 * 248-250 = chop_frame
	 * 251 = same_frame_extended
	 * 252-254 = append_frame
	 * 255 = full_frame
	 */
	int frame_type;
	/* same_locals_1_stack_item_frame:
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
	/* full_frame:
	 */
	short number_of_locals;
	// The value of the line_number_table_length item indicates the number of entries in the line_number_table array.
	VerificationTypeInfo[] locals;
	/* full_frame:
	 */
	short number_of_stack_items;
	/* full_frame:
	 */
	VerificationTypeInfo[] stack;


	public StackMapFrame(ClassFile resolver) {
		this.resolver = resolver;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(stack, oldIndex, newIndex);
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
		str.append("StackMapFrame(frame_type=" + frame_type + ", offset=" + offset_delta + ", locals=" + number_of_locals + " [");
		// Local stack items
		for(int i = 0; i < number_of_locals-1; i++) {
			str.append(locals[i] + ", ");
		}
		if(number_of_locals> 0) { str.append(locals[number_of_locals-1]); }
		// Stack items
		str.append("], stack_items=" + number_of_stack_items);
		for(int i = 0; i < number_of_stack_items-1; i++) {
			str.append(stack[i] + ", ");
		}
		if(number_of_stack_items > 0) { str.append(stack[number_of_stack_items-1]); }
		str.append("])");
		return str.toString();
	}

}
