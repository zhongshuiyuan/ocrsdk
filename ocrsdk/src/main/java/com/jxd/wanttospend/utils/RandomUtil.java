package com.jxd.wanttospend.utils;

import java.util.Random;

/**
 * @author renji
 */
public class RandomUtil {

	public static final Random random = new Random();

	public static String getRandom(int length) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < length; i++) {
			boolean isChar = (random.nextInt(2) % 2 == 0);
			if (isChar) {
				int choice = (random.nextInt(2) % 2 == 0) ? 65 : 97;
				ret.append((char) (choice + random.nextInt(26)));
			} else {
				ret.append(Integer.toString(random.nextInt(10)));
			}
		}
		return ret.toString();
	}

}
