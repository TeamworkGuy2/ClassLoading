package twg2.jbcm.modify;

/** Represents an object that contains constant pool indexes that can be changed.
 * @author TeamworkGuy2
 * @since 2014-4-20
 */
public interface CpIndexChangeable {

	public void changeCpIndex(CpIndexChanger indexChanger);

}
