package twg2.jbcm.classFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/** Indicates that a field is an array of indices into some real or
 * hypothetical Java class file Constant Pool data structure.<br/>
 * The value is an array of classes representing the valid data classes
 * that the indices in this array should point to in the constant pool.<br/><br/>
 * For example:<br/>
 * <code>@ConstantPoolIndexArray(CONSTANT_Utf8.class)</code><br/>
 * should be attached to a short[] or int[] field that represents an array of
 * indices into the constant pool where UTF-8 structures are stored.
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
@Target(ElementType.FIELD)
public @interface ConstantPoolIndexArray {
	Class<?>[] value();
}
