package twg2.jbcm.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/** File utilities related to class loading and bytecode manipulation
 * @author TeamworkGuy2
 * @since 2022-08-21
 */
public class FileUtility {

	/** The source class file is copied and written over the target class file.
	 * <br/>
	 * Unfortunately, the trick to get this to work requires that both class files be located
	 * in the same named relative class path with unique base paths, for example, to load a
	 * new version of the class "myProject.Original" the new class must also be contained in
	 * a path ending with "myProject.Original" but must be located in a folder outside of the
	 * current application's classpath. The original class might be located in
	 * "/home/name/project/myProject/Original.class" and the new class might be
	 * located in "/home/name/projectAlt/myProject/Original.class".<br/>
	 * <br/>
	 * @param sourcePath the directory location of the new class to copy (jar file paths are not supported).
	 * @param sourceClassName the fully qualifying Java name of the source class to copy,
	 * the path separated by '.' periods.
	 * @param targetPath the directory location of the class to overwrite (jar file paths are not supported).
	 * @param targetClassName the fully qualifying Java name of the class to overwrite,
	 * the path separated by '.' periods.
	 * @return true if the class was successfully overwritten, false if there was an IO error
	 */
	public static boolean overwriteClass(String sourcePath, String sourceClassName, String targetPath, String targetClassName) {
		try {
			String sourceClass = sourceClassName.replace('.', File.separatorChar) + ".class";
			String targetClass = targetClassName.replace('.', File.separatorChar) + ".class";
			String fullSourcePath = sourcePath + ((sourcePath.endsWith("\\") || sourcePath.endsWith("/")) ? sourceClass : File.separatorChar + sourceClass);
			String fullTargetPath = targetPath + ((targetPath.endsWith("\\") || targetPath.endsWith("/")) ? targetClass : File.separatorChar + targetClass);
			// Faster when the destination file exists
			overwriteFile(fullSourcePath, fullTargetPath);
			// Faster when the destination files does not exist
			//Files.copy(Paths.get(fullSourcePath), Paths.get(fullTargetPath), StandardCopyOption.REPLACE_EXISTING);
		} catch(IOException ioe) {
			System.err.println("Error, could not overwrite the old file, caused by " + ioe.getMessage() + "\nCause:" + ioe.getCause());
			return false;
		}
		return true;
	}


	/** Overwrite the destination file with the source file
	 * @param sourceFilePath the source file to copy
	 * @param targetFilePath the destination file to overwrite with the contents of the source file
	 * @throws IOException if there is an error opening either of the files or copying the source file's
	 * contents into the target file
	 */
	private static void overwriteFile(String sourceFilePath, String targetFilePath) throws IOException {
		FileChannel src = null;
		FileChannel dst = null;
		try {
			File sourceFile = new File(sourceFilePath).getCanonicalFile();
			File targetFile = new File(targetFilePath).getCanonicalFile();
			//System.out.println("Overwriting: " + sourceFile.getAbsolutePath() + ", with: " + targetFile.getAbsolutePath());

			@SuppressWarnings("resource")
			FileInputStream srcStream = new FileInputStream(sourceFile);
			src = srcStream.getChannel();

			@SuppressWarnings("resource")
			FileOutputStream dstStream = new FileOutputStream(targetFile);
			dst = dstStream.getChannel();

			long transferCount = 0;
			long size = src.size();
			do {
				transferCount += dst.transferFrom(src, transferCount, size - transferCount);
			} while(transferCount < size);
		} catch (IOException e) {
			throw e;
		}
		finally {
			try {
				if(src != null) {
					src.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(dst != null) {
					dst.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
