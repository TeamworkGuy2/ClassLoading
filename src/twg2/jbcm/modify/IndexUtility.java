package twg2.jbcm.modify;

/** Helper methods for {@link IndexChanger}
 * @author TeamworkGuy2
 * @since 2014-4-21
 */
public class IndexUtility {

	/** Call {@link IndexChanger#changeCpIndex(short, short)} on each object in the specified array
	 * @param objs the array of objects to call {@link IndexChanger#changeCpIndex(short, short)} on
	 * @param oldIndex the old index to look for
	 * @param newIndex the new index to replace matching old indices with
	 */
	public static final void indexChange(IndexChanger[] objs, short oldIndex, short newIndex) {
		for(IndexChanger obj : objs) {
			obj.changeCpIndex(oldIndex, newIndex);
		}
	}


	/** Check if each index in the specified array matches the old index and if so, replace it with the new index
	 * @param indices an array of short indices to check
	 * @param oldIndex the old index to look for
	 * @param newIndex the new index to replace matching old indices with
	 */
	public static final void indexChange(short[] indices, short oldIndex, short newIndex) {
		for(int i = indices.length-1; i > -1; i--) {
			if(indices[i] == oldIndex) {
				indices[i] = newIndex;
			}
		}
	}


	/** Call {@link IndexChanger#changeCpIndex(short, short)} on the specified object
	 * @param obj the object to call {@link IndexChanger#changeCpIndex(short, short)} on
	 * @param oldIndex the old index to look for
	 * @param newIndex the new index to replace matching old indices with
	 */
	public static final void indexChange(IndexChanger obj, short oldIndex, short newIndex) {
		if(obj != null) {
			obj.changeCpIndex(oldIndex, newIndex);
		}
	}


	/** Check if the specified index matches the old index and if so, return the new index, else return the old index.
	 * An example of this might be: {@code myIndex = indexChange(myIndex, 30, 35)}<br/>
	 * If {@code (myIndex == 30)}, then {@code 35} would be returned, else {@code myIndex} would be returned.
	 * @param index the index to compare to the old index
	 * @param oldIndex the old index to compare to
	 * @param newIndex the new index to return if the specified index matches the old index
	 */
	public static final short indexChange(short index, short oldIndex, short newIndex) {
		return (index == oldIndex) ? newIndex : index;
	}

}
