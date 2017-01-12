package twg2.jbcm.runtimeLoading;

/** An interface representing a custom resource classloader
 * @author TeamworkGuy2
 * @since 2013-9-24
 */
public interface ResourceClassLoader {

	public Class<?> findClass(String name);

	public void addClassByteCode(String name, byte[] b);

}
