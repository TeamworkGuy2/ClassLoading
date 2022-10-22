# Change Log
All notable changes to this project will be documented in this file.
This project does its best to adhere to [Semantic Versioning](http://semver.org/).

--------
### [0.6.0](N/A) - 2022-10-22
__Add loop and if-statements detection to decompilation, also handle basic try-catch and synchronized blocks.__
#### Added
* `StringBuilderIndent` provides the same API as `StringBuilder` (unfortunately we can't extend StringBuilder because it is final) and implements `Indent` so that this class can be used to easily build source code strings
* `src/twg2/jbcm/toSource/structures` with state handlers for inserting more complex structures such as loops and try-catch statements into source code during opcode iteration

#### Changed
* `twg2.jbcm.classFormat.attributes.Code` `toClassString()` renamed `toClassCodeString()`
* `CpIndexChanger` is now stateful and contains the old and new index and uses a proper visitor pattern to handle changing indexes
* `Indent` changed from a class to an interface with a public static `Impl` subclass
* `IterateCode` renamed `CodeIterator`
* `RuntimeReloadMain` refactored, more complex threaded loading and invocation of methods from updated class files, some code moved to new classes `twg2.jbcm.runtime.ClassLoaders` and `FileUtility`
* `twg2.jbcm.runtimeLoading` package renamed `twg2.jbcm.runtime`
* `CodeFlow` contains algorithms for detecting loops and if-statements in byte code
* `DataCountingInputStream` added and used in `ClassFile` when parsing a class to improve debug and error message with exact byte index locations
* Several new unit/integration tests added, `CompileTest` renamed `CompileJava`
* Fixed compiling code during runtime to support class names with arbitrary package paths, required extensive changes to `CompileSource`
* `JumpConditionInfo` rewritten to support representing loop and nested if-statement conditions
* `TypeUtility.classNameFieldDescriptor()` renamed `toBinaryClassName()`


--------
### [0.5.1](https://github.com/TeamworkGuy2/ClassLoading/commit/3653372d7564f749135a7db119a70f77df8b1696) - 2020-12-05
__Fix `switch` statements to decompile much more accurately based on code flow analysis. Start work on `if` statements.__
#### Added
* new `Indent` class to handle `SourceWriter` indentation
* `Switch` to handle switch code flow initialization and info

#### Changed
* Add code flow analysis initialization and info to `JumpConditionInfo`
* Assume that a forward goto right before a condition is an `else` statement


--------
### [0.5.0](https://github.com/TeamworkGuy2/ClassLoading/commit/2497bc4caaa27e6574afad64cace3475238da9f2) - 2020-12-05
__Decompilation to source code in-progress and first round trip compile/decompile unit tests__
#### Added
* A new `twg2.jbcm.ir` package with helper classes for tracking state and data related to decompilation
* `CodeOffsetGetter` interface implemented by `ChangeCpIndex`
* `CodeFlow` for helping analyze potential code flow paths through a method
* Unit tests with tests which perform compilation of source code and check decompilation back to intermediate view

#### Changed
* `CodeToSource` is in-progress, simple code can be converted, loops, switch statements, and interface/dynamic method calls are still TODO
* Split `IoUtility` into new `CodeUtility` class and moved static no-op `CpIndexChanger` and `CodeOffsetChanger` into their respective interfaces
* Renamed `ByteCodeConsumer` -> `BytcodeConsumer`
* Moved `MethodStack` to new `twg2.jbcm.ir` package
* Lots of new functionality in `Opcodes` to support to source decompilation
* Extensive `CompileSource` changes/improvements to support simple use cases like compiling in-memory (although a physical temp file still gets written under the hood)
* Moved `LookupswitchOffsetModifier` and `TableswitchOffsetModifier` out of `IoUtility` and into their own classes

#### Removed
* `CpIndexChanger.shiftIndex()` interface method since it was unused and `CodeOffsetChanger` is equivalent
* Unused classes: `Offset`, `OffsetOpcode`, `Opcode`, `OpcodeObject`


--------
### [0.4.0](https://github.com/TeamworkGuy2/ClassLoading/commit/1d29c1923096438571a751511cd1f2085d708bb9) - 2020-06-29
#### Added
* `ClassFileToSource` and `CodeToSource` work-in-progress to convert class file back into Java source code
* `CONSTANT_CP_Info.toShortString()` added interface method and to implementations

#### Changed
* Clean up `Settings` mega-utility class. Move methods into `ConstantPoolExtensions`, `ClassFile`, and `ClassFileAttributes`
* Cleaned up `Opcodes`

#### Removed
* Unused `ConstantPoolIndexArray` and `ConstantPoolTag` annotations


--------
### [0.3.0](https://github.com/TeamworkGuy2/ClassLoading/commit/a9d89312f7c97837b332ecda61f6ef38de53f70f) - 2017-12-22
#### Changed
* Upgrade to Java 9
* Java 9 ClassFile format support:
  * New Attributes: `Module`, `ModuleMainClass`, `ModulePackages`
  * New constant pool types: `CONSTANT_Module`, `CONSTANT_Package`
* Renamed/changed the `public static final int CONSTANT_*_info` field to `public static final byte TAG` and removed the instanced `tag` field from all constantPool classes
* Renamed `Target_Info_Type` -> `Target_Info_Union`
* Consolidated `twg2.jbcm.classFormat.attributes`, nested classes that are only used by one class inside the class
* Added/fixed several attributes' `toString()` methods
* Cleaned up some attributes field documentation
* Renamed `UnitTest` -> `UsageCliMain` and added support for printing dependencies and loading multiple classes, printing multiple classes, and clearing loaded classes


--------
### [0.2.1](https://github.com/TeamworkGuy2/ClassLoading/commit/555e988ff0c21d0510296cefd9b300d46fd6eb04) - 2017-08-14
#### Changed
* Forgot to update jar file


--------
### [0.2.0](https://github.com/TeamworkGuy2/ClassLoading/commit/604a82930aaf0e24f129f31e2e946aded5e19931) - 2017-08-14
#### Changed
* Cleaned up unnecessary twg2.jbcm.opcode package classes
* Renamed twg2.jbcm.modify.IndexChange -> IndexChanger
* Renamed twg2.jbcm.modify.OpcodeChangeCpIndex -> CpIndexChanger
* Renamed twg2.jbcm.modify.OpcodeChangeOffset -> CodeOffsetChanger


--------
### [0.1.0](https://github.com/TeamworkGuy2/ClassLoading/commit/dc1eda0b07c1d9746b8a63affff8d8b7b0d2103f) - 2017-01-11
#### Added
* Initial versioning of existing code, including classfile loading/parsing, dynamic classfile modification, and runtime class loading.
