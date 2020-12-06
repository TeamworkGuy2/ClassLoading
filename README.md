ClassLoading
==========

Java class file parsing, manipulation, and decompilation library.
This library is mostly experimental for my own personal learning.
It can load and save class files and lookup class file dependencies, but contains few helpers for making changes to class files or validating those changes.
The class file format is fully implemented in [twg2.jbcm.classFormat](./ClassLoading/tree/master/src/twg2/jbcm/classFormat) and sub packages, including all [constant pool entry types](https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.4) and [class file attributes](https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7).
See the `twg2.jbcm.main.UsageCliMain` class for a simple command line interface you can use to load and print class file info.

Reference: [Java Virtual Machine Spec (Java 9)](https://docs.oracle.com/javase/specs/jvms/se9/html/index.html)

Packages:
### twg2.jbcm.classFormat
Contains implementation of the [class file format](https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html)
with related attributes (`twg2.jbcm.classFormat.attributes`) and constant pool types (`twg2.jbcm.classFormat.constantPool`).

### twg2.jbcm & twg2.jbcm.modify
Interfaces and utilities for searching and modifying class files.

### twg2.jbcm
Utilities and the `Opcodes` enum containing detailed, programatic information about the [Java instruction set opcodes](https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5).
Also see [extract-opcodes.js](extract-opcodes.js) file for how the enum literals in `Opcodes` are generated.

### twg2.jbcm.dynamicModification & twg2.jbcm.parserExamples
Classes used by the example and test packages.

### twg2.jbcm.runtimeLoading
Runtime class loading.

### twg2.jbcm.main
Example console apps.