package twg2.jbcm.test;

import java.io.IOException;

/**
 * @author TeamworkGuy2
 * @since 2022-09-15
 */
public class LoopsDecompileInspection {

	public static void loopConditionWithNestedIfOrGoto() throws IOException {
		int i = 0;
		while(Math.random() > 0 ? LoopsDecompileInspection.class.getResourceAsStream(LoopsDecompileInspection.class.getName()).read() > 0 : true) {
			System.out.println("iteration " + i);
			if(i < 10) {
				i++;
			}
			else {
				break;
			}
		}
	}

}
