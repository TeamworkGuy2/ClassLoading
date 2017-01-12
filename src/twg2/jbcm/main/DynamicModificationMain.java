package twg2.jbcm.main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import twg2.jbcm.classFormat.ClassFile;
import twg2.jbcm.classFormat.ConstantPoolTag;
import twg2.jbcm.classFormat.CpIndex;
import twg2.jbcm.classFormat.attributes.Code;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Class;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Methodref;
import twg2.jbcm.classFormat.constantPool.CONSTANT_NameAndType;
import twg2.jbcm.classFormat.constantPool.CONSTANT_Utf8;
import twg2.jbcm.dynamicModification.ListInterfaceAddon;
import twg2.jbcm.dynamicModification.OtherSimplImpl;
import twg2.jbcm.dynamicModification.SimpleInterface;

/**
 * @author TeamworkGuy2
 * @since 2014-1-23
 */
public class DynamicModificationMain {
	private List<SimpleInterface> simpleDynamic = new ArrayList<SimpleInterface>();
	private List<String> simpleDynamicName = new ArrayList<String>();
	private SimpleInterface simplePlus;


	/** Load the specified class file, modify its contents, and call the new methods in the class
	 * @param file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void testSimpleTest(String methodName, String methodDescriptor, File file, String fileClasspath, File projectFolder) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if(!file.isFile()) { throw new IllegalArgumentException("the file must be a valid file"); }
		if(!projectFolder.isDirectory()) { throw new IllegalArgumentException("the project folder must be a valid folder"); }
		String dynamicSubFolder = "load";
		String dynamicClassName = "DynamicTest";

		// TODO USES SimpleInterfaceImpl.java, don't delete SimpleInterfaceImpl.java
		ClassFile classFile = new ClassFile();
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		classFile.readData(in);
		in.close();

		//classFile.print();

		classFile.rename(fileClasspath + '/' + dynamicSubFolder + '/' + dynamicClassName);

		CpIndex<CONSTANT_Utf8> methodNameCp = classFile.findConstantPoolString(methodName);
		CpIndex<CONSTANT_Utf8> methodDescriptorCp = classFile.findConstantPoolString(methodDescriptor);

		System.out.println("Found method: " + methodName + " at " + methodNameCp + ": " + methodDescriptorCp);

		CONSTANT_NameAndType nameAndType = new CONSTANT_NameAndType(classFile);
		nameAndType.setNameIndex(methodNameCp); // callB method name
		nameAndType.setDescriptorIndex(methodDescriptorCp); // ()V method call
		CpIndex<CONSTANT_NameAndType> methodTypeCp = classFile.addToConstantPool(nameAndType);

		CONSTANT_Methodref methodRef = new CONSTANT_Methodref(classFile);
		methodRef.setClassIndex(classFile.getClassIndex()); // method class
		methodRef.setNameAndTypeIndex(methodTypeCp); // previously added name and type
		CpIndex<CONSTANT_Methodref> methodCp = classFile.addToConstantPool(methodRef);

		byte[] bytes = ((Code)classFile.getMethod(1).getAttribute(0)).getCode();
		bytes[2] = (byte)((methodCp.getIndex() >> 8) & 0xFF);
		bytes[3] = (byte)(methodCp.getIndex() & 0xFF);

		//classFile.print();

		File dynamicFolder = new File(projectFolder, dynamicSubFolder);
		File dynamicFile = new File(dynamicFolder, dynamicClassName + ".class");
		if(!dynamicFolder.exists()) {
			dynamicFolder.mkdir();
		}
		DataOutputStream out = new DataOutputStream(new FileOutputStream(dynamicFile));
		classFile.writeData(out);
		out.close();

		// Load modified file and test it
		Class<?> test = Thread.currentThread().getContextClassLoader().loadClass(fileClasspath + '.' + dynamicSubFolder + "." + dynamicClassName);
		simpleDynamic.add( (SimpleInterface)test.newInstance());
		simpleDynamicName.add(methodDescriptor);
		simplePlus = new OtherSimplImpl();
	}


	public void dynamicallyAddInterfaceTo(String fileClasspath, File projectFolder) throws IOException {
		String dynamicSubFolder = "load";
		String dynamicClassName = "ListInterfaceTest";

		ClassFile listImplClass = new ClassFile();
		DataInputStream in = new DataInputStream(new FileInputStream(new File("bin/twg2/jbcm/dynamicModification/ListInterfaceAddonImpl.class")));
		listImplClass.readData(in);
		in.close();
		listImplClass.print(System.out);

		System.out.println("\n\n");

		in = new DataInputStream(new FileInputStream(new File("bin/twg2/jbcm/dynamicModification/ListNotInterfaceAddon.class")));
		listImplClass.readData(in);
		in.close();

		listImplClass.rename(fileClasspath.replace('.', '/') + '/' + dynamicSubFolder + '/' + dynamicClassName);

		// Create a class structure and UTF-8 string for the interface link
		CONSTANT_Class clazz = (CONSTANT_Class)ConstantPoolTag.CLASS.create(listImplClass);
		CpIndex<CONSTANT_Class> classCp = listImplClass.addToConstantPool(clazz);

		CONSTANT_Utf8 str = (CONSTANT_Utf8)ConstantPoolTag.UTF8.create(listImplClass);
		str.setString("twg2/jbcm/dynamicModification/ListInterfaceAddon");
		CpIndex<CONSTANT_Utf8> strCp = listImplClass.addToConstantPool(str);

		clazz.setNameIndex(strCp);
		// Add the interface link
		listImplClass.addInterface(classCp);

		listImplClass.print(System.out);

		File dynamicFolder = new File(projectFolder, dynamicSubFolder);
		File dynamicFile = new File(dynamicFolder, dynamicClassName + ".class");
		if(!dynamicFolder.exists()) {
			dynamicFolder.mkdir();
		}
		DataOutputStream out = new DataOutputStream(new FileOutputStream(dynamicFile));
		listImplClass.writeData(out);
		out.close();
	}


	public void testAddedInterface(String fileClasspath) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String dynamicSubFolder = "load";
		String dynamicClassName = "ListInterfaceTest";

		Class<?> test = Thread.currentThread().getContextClassLoader().loadClass(fileClasspath + '.' + dynamicSubFolder + "." + dynamicClassName);
		@SuppressWarnings("unchecked")
		ListInterfaceAddon<String> listTestInstance = (ListInterfaceAddon<String>)test.newInstance();
		System.out.println("dynamic instance: " + listTestInstance + "\nsize(): " + listTestInstance.size() + "\nget(0): " + listTestInstance.get(0));	
	}


	/** Process user requests and translate them to method calls
	 * @param in the input scanner to read user input lines from
	 */
	public void processUserRequests(Scanner in) {
		boolean exit = false;

		while(!exit) {
			// Get the user's input request
			System.out.print("Enter request (");
			for(int i = 0; i < simpleDynamic.size(); i++) {
				System.out.print("'" + simpleDynamicName.get(i) + "()', ");
			}
			System.out.print("'plus()', 'exit'): ");
			String userLine = in.nextLine();

			for(int i = 0; i < simpleDynamic.size(); i++) {
				if((simpleDynamicName.get(i)+"()").equals(userLine)) {
					simpleDynamic.get(i).callTest();
				}
			}
			if("plus()".equals(userLine)) {
				simplePlus.callTest();
			}
			else if("exit".equals(userLine)) {
				exit = true;
				break;
			}

			// Print the current objects' state
			System.out.println("\nplus object: " + simplePlus.getCount());
			for(int i = 0; i < simpleDynamic.size(); i++) {
				System.out.println(simpleDynamicName.get(i) + " object: " + simpleDynamic.get(i).getCount());
			}
			System.out.println();
		}
	}


	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		File projectFolder = new File("bin/twg2/jbcm/dynamicModification");
		String fileClasspath = "twg2.jbcm.dynamicModification";

		DynamicModificationMain test = new DynamicModificationMain();
		test.dynamicallyAddInterfaceTo(fileClasspath, projectFolder);
		test.testAddedInterface(fileClasspath);

		/*
		File file = new File(projectFolder, "/SimpleInterfaceImpl.class");

		Scanner in = new Scanner(System.in);

		System.out.print("Method name ('alias methodName'): ");
		String[] userLine = in.nextLine().split(" ");
		String methodDescription = userLine[0];
		String methodName = userLine[1];

		//System.out.println(file.getAbsolutePath());

		DynamicModificationMain test = new DynamicModificationMain();
		test.testSimpleTest(methodName, methodDescription, file, fileClasspath, projectFolder);
		test.processUserRequests(in);
		*/
	}

}
