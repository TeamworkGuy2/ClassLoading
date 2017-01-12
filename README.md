ClassLoading
==========
version: 0.1.0

Java class file parsing and manipulation library.
Reference: [Java Virtual Machine Spec](http://docs.oracle.com/javase/specs/jvms/se8/html/index.html)

###See `twg2.jbcm.main/`
for example code and interactive command line tools.

####`twg2.jbcm.classFormat`
Contains implementation of the [class file format](http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html)
with related attributes and constant pool types.

####`twg2.jbcm` and `twg2.jbcm.modify`
Interfaces and utilities for searching and modifying class files.

####`twg2.jbcm.opcode`
Partial implementation of [Java instruction set opcodes](http://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html#jvms-6.5).

####`twg2.jbcm.dynamicModification`, `twg2.jbcm.parserExamples`
Classes used by the example and test packages.

####`twg2.jbcm.runtimeLoading`
Runtime class loading.

####`twg2.jbcm.main`
Example console apps.