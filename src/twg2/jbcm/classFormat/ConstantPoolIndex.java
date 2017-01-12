package twg2.jbcm.classFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/** Indicates that a field is an index into some real or hypothetical Java
 * class file Constant Pool data structure.<br/>
 * The value is the class of the data that should be stored at that index
 * in the constant pool.
 * For example:<br/>
 * <code>@ConstantPoolIndex(CONSTANT_Utf8.class)</code><br/>
 * should be attached to a short or int field that represents an index into
 * the constant pool where a UTF-8 structure is stored.
 * @author TeamworkGuy2
 * @since 2013-12-3
 */
@Target(ElementType.FIELD)
public @interface ConstantPoolIndex {
	Class<?>[] value();
}
