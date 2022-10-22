package twg2.jbcm.modify;

/** Default parameter implementation for {@link CpIndexChangeable}
 * @author TeamworkGuy2
 * @since 2014-4-21
 */
public class CpIndexChanger {
	/** the old index to replace when found*/
	protected short oldIndex;
	/** the new index to replace matching old indices with */
	protected short newIndex;


	public CpIndexChanger(short oldIndex, short newIndex) {
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
	}


	/** Call {@link CpIndexChangeable#changeCpIndex(short, short)} on each object in the specified array
	 * @param objs the array of objects to call {@link CpIndexChangeable#changeCpIndex(short, short)} on
	 */
	public void indexChange(CpIndexChangeable[] objs) {
		for(CpIndexChangeable obj : objs) {
			obj.changeCpIndex(this);
		}
	}


	/** Call {@link CpIndexChangeable#changeCpIndex(short, short)} on the specified object
	 * @param obj the object to call {@link CpIndexChangeable#changeCpIndex(short, short)} on
	 */
	public void indexChange(CpIndexChangeable obj) {
		if(obj != null) {
			obj.changeCpIndex(this);
		}
	}


	/** Check if the specified index matches the old index and if so, return the new index, else return the old index.
	 * An example of this might be: {@code myIndex = indexChange(myIndex, 30, 35)}<br/>
	 * If {@code (myIndex == 30)}, then {@code 35} would be returned, else {@code myIndex} would be returned.
	 * @param currentIndex the current index to compare and replace if necessary
	 */
	public short indexChange(short currentIndex) {
		return (currentIndex == oldIndex) ? newIndex : currentIndex;
	}

}
