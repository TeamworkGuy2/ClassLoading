package twg2.jbcm.classFormat.attributes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ClassFileAttributes;
import twg2.jbcm.classFormat.ConstantPoolAllowZero;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.ReadWritable;
import twg2.jbcm.classFormat.Settings;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Module;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Package;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.modify.IndexUtility;

/** A Java class file format Attribute of type <code>Module</code>
 * @author TeamworkGuy2
 * @since 2017-12-22
 */
public class Module implements Attribute_Type {
	public static final String ATTRIBUTE_NAME = "Module";
	ClassFile resolver;
	/** The value of the attribute_name_index item must be a valid index into the constant_pool table.
	 * The constant_pool entry at that index must be a CONSTANT_Utf8_info structure representing the
	 * string "Module".
	 */
	CpIndex<CONSTANT_Utf8> attribute_name_index;
	/** The value of the attribute_length item indicates the length of the attribute,
	 * excluding the initial six bytes. */
	int attribute_length;

	/** The value of the module_name_index item must be a valid index into the constant_pool table. The constant_pool entry
	 * at that index must be a CONSTANT_Module_info structure denoting the current module.
	 */
	CpIndex<CONSTANT_Module> module_name_index;
	/** The value of the module_flags item is as follows:<br>
	 * {@code 0x0020 (ACC_OPEN)	Indicates that this module is open.}<br>
	 * {@code 0x1000 (ACC_SYNTHETIC)	Indicates that this module was not explicitly or implicitly declared.}<br>
	 * {@code 0x8000 (ACC_MANDATED)	Indicates that this module was implicitly declared.}
	 */
	short module_flags;
	/** The value of the module_version_index item must be either zero or a valid index into the constant_pool table.
	 * If the value of the item is zero, then no version information about the current module is present.
	 * If the value of the item is nonzero, then the constant_pool entry at that index must be a CONSTANT_Utf8_info structure
	 * representing the version of the current module.
	 */
	@ConstantPoolAllowZero
	CpIndex<CONSTANT_Utf8> module_version_index;

	/**The value of the requires_count item indicates the number of entries in the requires table.
	 * If the current module is java.base, then requires_count must be zero.
	 * If the current module is not java.base, then requires_count must be at least one. 
	 */
	short requires_count;
	/** Each entry in the requires table specifies a dependence of the current module.
	 */
	ModuleRequire[] requires;

	/** The value of the exports_count item indicates the number of entries in the exports table.
	 */
	short exports_count;
	/** Each entry in the exports table specifies a package exported by the current module, such that public and protected
	 * types in the package, and their public and protected members, may be accessed from outside the current module, possibly
	 * from a limited set of "friend" modules.
	 */
	ModuleExport[] exports;

	/** The value of the opens_count item indicates the number of entries in the opens table.
	 * opens_count must be zero if the current module is open. 
	 */
	short opens_count;
	/** Each entry in the opens table specifies a package opened by the current module, such that all types in the package, and
	 * all their members, may be accessed from outside the current module via the reflection libraries of the Java SE Platform,
	 * possibly from a limited set of "friend" modules.
	 */
	ModuleOpen[] opens;

	/** The value of the uses_count item indicates the number of entries in the uses_index table.
	 */
	short uses_count;
	/** The value of each entry in the uses_index table must be a valid index into the constant_pool table. The constant_pool entry
	 * at that index must be a CONSTANT_Class_info structure representing a service interface which the current module may discover
	 * via java.util.ServiceLoader.  At most one entry in the uses_index table may specify a service interface of a given name.
	 */
	CpIndex<CONSTANT_Class>[] uses_index;

	/** The value of the provides_count item indicates the number of entries in the provides table.
	 */
	short provides_count;
	/** Each entry in the provides table represents a service implementation for a given service interface.
	 */
	ModuleProvide[] provides;


	public Module(ClassFile resolver, short attributeNameIndex) {
		this.attribute_name_index = resolver.getAttributeNameIndex(attributeNameIndex);
		this.resolver = resolver;
	}


	@Override
	public String getAttributeName() {
		return ATTRIBUTE_NAME;
	}


	@Override
	public int getAttributeLength() {
		return attribute_length;
	}


	@Override
	public void changeCpIndex(short oldIndex, short newIndex) {
		IndexUtility.indexChange(attribute_name_index, oldIndex, newIndex);
		IndexUtility.indexChange(module_name_index, oldIndex, newIndex);
		IndexUtility.indexChange(module_version_index, oldIndex, newIndex);
		IndexUtility.indexChange(requires, oldIndex, newIndex);
		IndexUtility.indexChange(exports, oldIndex, newIndex);
		IndexUtility.indexChange(opens, oldIndex, newIndex);
		IndexUtility.indexChange(uses_index, oldIndex, newIndex);
		IndexUtility.indexChange(provides, oldIndex, newIndex);
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		attribute_name_index.writeData(out);
		out.writeInt(attribute_length);

		module_name_index.writeData(out);
		out.writeShort(module_flags);
		module_version_index.writeData(out);

		out.writeShort(requires_count);
		for(int i = 0; i < requires_count; i++) {
			requires[i].writeData(out);
		}

		out.writeShort(exports_count);
		for(int i = 0; i < exports_count; i++) {
			exports[i].writeData(out);
		}

		out.writeShort(opens_count);
		for(int i = 0; i < opens_count; i++) {
			opens[i].writeData(out);
		}

		out.writeShort(uses_count);
		for(int i = 0; i < uses_count; i++) {
			uses_index[i].writeData(out);
		}

		out.writeShort(provides_count);
		for(int i = 0; i < provides_count; i++) {
			provides[i].writeData(out);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void readData(DataInput in) throws IOException {
		if(Settings.readAttributeName) {
			attribute_name_index = ClassFileAttributes.readAttributeNameIndex(in, resolver, ATTRIBUTE_NAME);
		}
		attribute_length = in.readInt();

		module_name_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Module.class);
		module_flags = in.readShort();
		module_version_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);

		requires_count = in.readShort();
		requires = new ModuleRequire[requires_count];
		for(int i = 0; i < requires_count; i++) {
			ModuleRequire rqr = new ModuleRequire(resolver);
			rqr.readData(in);
			requires[i] = rqr;
		}

		exports_count = in.readShort();
		exports = new ModuleExport[exports_count];
		for(int i = 0; i < exports_count; i++) {
			ModuleExport exp = new ModuleExport(resolver);
			exp.readData(in);
			exports[i] = exp;
		}

		opens_count = in.readShort();
		opens = new ModuleOpen[opens_count];
		for(int i = 0; i < opens_count; i++) {
			ModuleOpen opn = new ModuleOpen(resolver);
			opn.readData(in);
			opens[i] = opn;
		}

		uses_count = in.readShort();
		uses_index = new CpIndex[uses_count];
		for(int i = 0; i < uses_count; i++) {
			uses_index[i] = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
		}

		provides_count = in.readShort();
		provides = new ModuleProvide[provides_count];
		for(int i = 0; i < provides_count; i++) {
			ModuleProvide prv = new ModuleProvide(resolver);
			prv.readData(in);
			provides[i] = prv;
		}
	}


	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(ATTRIBUTE_NAME);
		str.append("(module_name=").append(module_name_index.getCpObject());
		str.append(", flags=").append(Integer.toHexString(module_flags));
		str.append(", module_version=").append(module_version_index.getIndex() > 0 ? module_version_index.getCpObject() : "null");

		str.append(", requires[");
		for(int i = 0; i < requires_count-1; i++) {
			str.append(requires[i]);
			str.append(", ");
		}
		if(requires_count > 0) { str.append(requires[requires_count-1]); }
		str.append("]");

		str.append(", exports[");
		for(int i = 0; i < exports_count-1; i++) {
			str.append(exports[i]);
			str.append(", ");
		}
		if(exports_count > 0) { str.append(exports[exports_count-1]); }
		str.append("]");

		str.append(", opens[");
		for(int i = 0; i < opens_count-1; i++) {
			str.append(opens[i]);
			str.append(", ");
		}
		if(opens_count > 0) { str.append(opens[opens_count-1]); }
		str.append("]");

		str.append(", uses[");
		for(int i = 0; i < uses_count-1; i++) {
			str.append(uses_index[i].getCpObject());
			str.append(", ");
		}
		if(uses_count > 0) { str.append(uses_index[uses_count-1].getCpObject()); }
		str.append("]");

		str.append(", requires[");
		for(int i = 0; i < requires_count-1; i++) {
			str.append(requires[i]);
			str.append(", ");
		}
		if(requires_count > 0) { str.append(requires[requires_count-1]); }
		str.append("]");

		str.append(")");

		return str.toString();
	}



	/** Java class file format Code Attribute for {@code Module}
	 * @author TeamworkGuy2
	 * @since 2017-12-22
	 */
	public static class ModuleRequire implements ReadWritable {
		ClassFile resolver;
		/** The value of the requires_index item must be a valid index into the constant_pool table. The constant_pool entry
		 * at that index must be a CONSTANT_Module_info structure denoting a module on which the current module depends.
		 * At most one entry in the requires table may specify a module of a given name with its requires_index item.
		 */
		CpIndex<CONSTANT_Module> requires_index;
		/** The value of the requires_flags item is as follows:<br>
		 * {@code 0x0020 (ACC_TRANSITIVE)	Indicates that any module which depends on the current module, implicitly declares a dependence on the module indicated by this entry.}<br>
		 * {@code 0x0040 (ACC_STATIC_PHASE)	Indicates that this dependence is mandatory in the static phase, i.e., at compile time, but is optional in the dynamic phase, i.e., at run time.}<br>
		 * {@code 0x1000 (ACC_SYNTHETIC)	Indicates that this dependence was not explicitly or implicitly declared in the source of the module declaration.}<br>
		 * {@code 0x8000 (ACC_MANDATED)	Indicates that this dependence was implicitly declared in the source of the module declaration.}
		 */
		short requires_flags;
		/** The value of the requires_version_index item must be either zero or a valid index into the constant_pool table.
		 * If the value of the item is zero, then no version information about the dependence is present.
		 * If the value of the item is nonzero, then the constant_pool entry at that index must be a CONSTANT_Utf8_info
		 * structure representing the version of the module specified by requires_index.
		 */
		@ConstantPoolAllowZero
		CpIndex<CONSTANT_Utf8> requires_version_index;


		public ModuleRequire(ClassFile resolver) {
			this.resolver = resolver;
		}


		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
			IndexUtility.indexChange(requires_index, oldIndex, newIndex);
			IndexUtility.indexChange(requires_version_index, oldIndex, newIndex);
		}


		@Override
		public void writeData(DataOutput out) throws IOException {
			requires_index.writeData(out);
			out.writeShort(requires_flags);
			requires_version_index.writeData(out);
		}


		@Override
		public void readData(DataInput in) throws IOException {
			requires_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Module.class);
			requires_flags = in.readShort();
			requires_version_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Utf8.class);
		}


		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("ModuleRequire(dependency=").append(requires_index.getCpObject());
			sb.append(", flags=").append(Integer.toHexString(requires_flags));
			sb.append(", version=").append(requires_version_index.getIndex() > 0 ? requires_version_index.getCpObject() : "");
			sb.append(")");
			return sb.toString();
		}

	}




	/** Java class file format Code Attribute for {@code Module}
	 * @author TeamworkGuy2
	 * @since 2017-12-22
	 */
	public static class ModuleExport implements ReadWritable {
		ClassFile resolver;
		/** The value of the exports_index item must be a valid index into the constant_pool table. The constant_pool entry
		 * at that index must be a CONSTANT_Package_info structure representing a package exported by the current module.
		 * At most one entry in the exports table may specify a package of a given name with its exports_index item.
		 */
		CpIndex<CONSTANT_Package> exports_index;
		/** The value of the exports_flags item is as follows:<br>
		 * {@code 0x1000 (ACC_SYNTHETIC)	Indicates that this export was not explicitly or implicitly declared in the source of the module declaration.}<br>
		 * {@code 0x8000 (ACC_MANDATED)	Indicates that this export was implicitly declared in the source of the module declaration.}
		 */
		short exports_flags;
		/** The value of the exports_to_count indicates the number of entries in the exports_to_index table.
		 * If exports_to_count is zero, then this package is exported by the current module in an unqualified fashion;
		 * code in any other module may access the types and members in the package.
		 * If exports_to_count is nonzero, then this package is exported by the current module in a qualified fashion;
		 * only code in the modules listed in the exports_to_index table may access the types and members in the package.
		 */
		short exports_to_count;
		/** The value of each entry in the exports_to_index table must be a valid index into the constant_pool table.
		 * The constant_pool entry at that index must be a CONSTANT_Module_info structure denoting a module whose code
		 * can access the types and members in this exported package.
		 * For each entry in the exports table, at most one entry in its exports_to_index table may specify a module of a given name.
		 */
		CpIndex<CONSTANT_Module>[] exports_to_index;


		public ModuleExport(ClassFile resolver) {
			this.resolver = resolver;
		}


		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
			IndexUtility.indexChange(exports_index, oldIndex, newIndex);
			IndexUtility.indexChange(exports_to_index, oldIndex, newIndex);
		}


		@Override
		public void writeData(DataOutput out) throws IOException {
			exports_index.writeData(out);
			out.writeShort(exports_flags);
			out.writeShort(exports_to_count);
			for(int i = 0; i < exports_to_count; i++) {
				exports_to_index[i].writeData(out);
			}
		}


		@SuppressWarnings("unchecked")
		@Override
		public void readData(DataInput in) throws IOException {
			exports_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Package.class);
			exports_flags = in.readShort();
			exports_to_count = in.readShort();
			exports_to_index = new CpIndex[exports_to_count];
			for(int i = 0; i < exports_to_count; i++) {
				exports_to_index[i] = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Module.class);
			}
		}


		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("ModuleExport(package=").append(exports_index.getCpObject());
			sb.append(", flags=").append(Integer.toHexString(exports_flags));
			sb.append(", exports_to=[");
			for(int i = 0; i < exports_to_count; i++) {
				sb.append(exports_to_index[i].getCpObject());
			}
			sb.append("])");
			return sb.toString();
		}

	}




	/** Java class file format Code Attribute for {@code Module}
	 * @author TeamworkGuy2
	 * @since 2017-12-22
	 */
	public static class ModuleOpen implements ReadWritable {
		ClassFile resolver;
		/** The value of the opens_index item must be a valid index into the constant_pool table. The constant_pool entry
		 * at that index must be a CONSTANT_Package_info structure representing a package opened by the current module.
		 * At most one entry in the opens table may specify a package of a given name with its opens_index item.
		 */
		CpIndex<CONSTANT_Package> opens_index;
		/** The value of the opens_flags item is as follows:<br>
		 * {@code 0x1000 (ACC_SYNTHETIC)	Indicates that this opening was not explicitly or implicitly declared in the source of the module declaration.}<br>
		 * {@code 0x8000 (ACC_MANDATED)	Indicates that this opening was implicitly declared in the source of the module declaration.}
		 */
		short opens_flags;
		/** The value of the opens_to_count indicates the number of entries in the opens_to_index table.
		 * If opens_to_count is zero, then this package is opened by the current module in an unqualified fashion;
		 * code in any other module may reflectively access the types and members in the package.
		 * If opens_to_count is nonzero, then this package is opened by the current module in a qualified fashion;
		 * only code in the modules listed in the exports_to_index table may reflectively access the types and members in the package.
		 */
		short opens_to_count;
		/** The value of each entry in the opens_to_index table must be a valid index into the constant_pool table.
		 * The constant_pool entry at that index must be a CONSTANT_Module_info structure denoting a module whose code
		 * can access the types and members in this opened package.
		 * For each entry in the opens table, at most one entry in its opens_to_index table may specify a module of a given name.
		 */
		CpIndex<CONSTANT_Module>[] opens_to_index;


		public ModuleOpen(ClassFile resolver) {
			this.resolver = resolver;
		}


		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
			IndexUtility.indexChange(opens_index, oldIndex, newIndex);
			IndexUtility.indexChange(opens_to_index, oldIndex, newIndex);
		}


		@Override
		public void writeData(DataOutput out) throws IOException {
			opens_index.writeData(out);
			out.writeShort(opens_flags);
			out.writeShort(opens_to_count);
			for(int i = 0; i < opens_to_count; i++) {
				opens_to_index[i].writeData(out);
			}
		}


		@SuppressWarnings("unchecked")
		@Override
		public void readData(DataInput in) throws IOException {
			opens_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Package.class);
			opens_flags = in.readShort();
			opens_to_count = in.readShort();
			opens_to_index = new CpIndex[opens_to_count];
			for(int i = 0; i < opens_to_count; i++) {
				opens_to_index[i] = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Module.class);
			}
		}


		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("ModuleOpen(package=").append(opens_index.getCpObject());
			sb.append(", flags=").append(Integer.toHexString(opens_flags));
			sb.append(", open_to=[");
			for(int i = 0; i < opens_to_count; i++) {
				sb.append(opens_to_index[i].getCpObject());
			}
			sb.append("])");
			return sb.toString();
		}

	}




	/** Java class file format Code Attribute for {@code Module}
	 * @author TeamworkGuy2
	 * @since 2017-12-22
	 */
	public static class ModuleProvide implements ReadWritable {
		ClassFile resolver;
		/** The value of the provides_index item must be a valid index into the constant_pool table. The constant_pool entry
		 * at that index must be a CONSTANT_Class_info structure representing a service interface for which the current module provides a service implementation.
		 * At most one entry in the provides table may specify a service interface of a given name with its provides_index item.
		 */
		CpIndex<CONSTANT_Class> provides_index;
		/** The value of the provides_with_count indicates the number of entries in the provides_with_index table.
		 * provides_with_count must be nonzero.
		 */
		short provides_to_count;
		/** The value of each entry in the provides_with_index table must be a valid index into the constant_pool table.
		 * The constant_pool entry at that index must be a CONSTANT_Class_info structure representing a service implementation
		 * for the service interface specified by provides_index.
		 * For each entry in the provides table, at most one entry in its provides_with_index table may specify a service implementation of a given name.
		 */
		CpIndex<CONSTANT_Class>[] provides_to_index;


		public ModuleProvide(ClassFile resolver) {
			this.resolver = resolver;
		}


		@Override
		public void changeCpIndex(short oldIndex, short newIndex) {
			IndexUtility.indexChange(provides_index, oldIndex, newIndex);
			IndexUtility.indexChange(provides_to_index, oldIndex, newIndex);
		}


		@Override
		public void writeData(DataOutput out) throws IOException {
			provides_index.writeData(out);
			out.writeShort(provides_to_count);
			for(int i = 0; i < provides_to_count; i++) {
				provides_to_index[i].writeData(out);
			}
		}


		@SuppressWarnings("unchecked")
		@Override
		public void readData(DataInput in) throws IOException {
			provides_index = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
			provides_to_count = in.readShort();
			provides_to_index = new CpIndex[provides_to_count];
			for(int i = 0; i < provides_to_count; i++) {
				provides_to_index[i] = resolver.getCheckCpIndex(in.readShort(), CONSTANT_Class.class);
			}
		}


		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("ModuleProvide(interface=").append(provides_index.getCpObject());
			sb.append(", implementations=[");
			for(int i = 0; i < provides_to_count; i++) {
				sb.append(provides_to_index[i].getCpObject());
			}
			sb.append("])");
			return sb.toString();
		}

	}

}
