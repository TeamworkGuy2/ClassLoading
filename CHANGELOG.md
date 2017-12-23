# Change Log
All notable changes to this project will be documented in this file.
This project does its best to adhere to [Semantic Versioning](http://semver.org/).


--------
### [0.3.0](N/A) - 2017-12-22
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
