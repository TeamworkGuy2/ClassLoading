package twg2.jbcm.modify;

import twg2.collections.primitiveCollections.IntArrayList;
import twg2.collections.primitiveCollections.IntList;
import twg2.jbcm.classFormat.ClassFile;

/**
 * @author TeamworkGuy2
 * @since 2021-08-04
 */
public class FindCpIndexUsage {

	public static IntList findUnusedIndexes(ClassFile classFile) {
		int constantPoolSize = classFile.getConstantPoolCount();

		CpIndexVisitor visitor = new CpIndexVisitor(constantPoolSize);
		classFile.changeCpIndex(visitor);
	
		IntArrayList unusedIndexes = new IntArrayList();
		for(int i = 0; i < constantPoolSize; i++) {
			if(visitor.indexVisitCounts[i] == 0) {
				unusedIndexes.add(i);
			}
		}

		return unusedIndexes;
	}



	/** Track all the constant pool indexes visited, without modifying them
	 * @author TeamworkGuy2
	 * @since 2021-08-05
	 */
	public static class CpIndexVisitor extends CpIndexChanger {
		protected int[] indexVisitCounts;

		public CpIndexVisitor(int constantPoolSize) {
			super((short)0, (short)0);
			this.indexVisitCounts = new int[constantPoolSize];
		}


		@Override
		public short indexChange(short currentIndex) {
			indexVisitCounts[currentIndex]++;
			return currentIndex;
		}

	}

}
