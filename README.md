ClassLoading
==========
version: 0.3.0

Java class file parsing and manipulation library.
This library is mostly experimental for my own personal learning.
It can load and save class files and lookup class file dependencies, but contains very little code for making changes to class files or validating those changes.
See the `twg2.jbcm.main.UsageCliMain` class for a simple command line interface you can use to load and print info about class files.
Reference: [Java Virtual Machine Spec (Java 9)](http://docs.oracle.com/javase/specs/jvms/se9/html/index.html)

### `twg2.jbcm.classFormat`
Contains implementation of the [class file format](http://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html)
with related attributes (`twg2.jbcm.classFormat.attributes`) and constant pool types (`twg2.jbcm.classFormat.constantPool`).

### `twg2.jbcm` and `twg2.jbcm.modify`
Interfaces and utilities for searching and modifying class files.

### `twg2.jbcm.opcode`
Partial implementation of [Java instruction set opcodes](http://docs.oracle.com/javase/specs/jvms/se9/html/jvms-6.html#jvms-6.5).

### `twg2.jbcm.dynamicModification` and `twg2.jbcm.parserExamples`
Classes used by the example and test packages.

### `twg2.jbcm.runtimeLoading`
Runtime class loading.

### `twg2.jbcm.main`
Example console apps.