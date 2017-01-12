package examples;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TeamworkGuy2
 * @since 2014-4-23
 */
public class ExampleMain implements Runnable {
	private int num;


	public ExampleMain() {
		this.num = 20;
	}


	@Override
	public void run() {
		List<Integer> list = new ArrayList<Integer>();
		series(num, 0, list);
		System.out.print(list);
	}


	private void series(int count, int initial, List<Integer> values) {
		int vn1 = values.size() > 0 ? values.get(values.size()-1) : 1;
		int vn2 = values.size() > 1 ? values.get(values.size()-2) : 0;
		values.add(vn1 + vn2);
		if(count > 0) {
			series(count-1, initial, values);
		}
	}


	public static void main(String[] args) {
		ExampleMain example = new ExampleMain();
		example.run();
	}

}
