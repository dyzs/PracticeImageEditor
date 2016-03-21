package com.dyzs.conciseimageeditor.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * create by maidou
 */
public class ColorUtil {
	/**
	 * 随机生成漂亮的颜色
	 * @return
	 */
	public static int randomColor() {
		Random random = new Random();

		int red = random.nextInt(150) + 50;
		
		int green = random.nextInt(150) + 50;
		
		int blue = random.nextInt(150) + 50;

		return Color.rgb(red, green, blue);		// 根据rgb混合生成一种新的颜色
	}

	/**
	 * 随机生成漂亮的颜色,带透明度的
	 * @return
	 */
	public static int randomColorArgb() {
		Random random = new Random();

		int alpha = random.nextInt(70) + 30;

		int red = random.nextInt(150) + 50;

		int green = random.nextInt(150) + 50;

		int blue = random.nextInt(150) + 50;

		return Color.argb(alpha, red, green, blue);		// 根据argb混合生成一种新的颜色
	}

	/**
	 * 根据RGB三色的值获取颜色
	 * @param intRgb
	 * @return
	 */
	public static int getColorByRGB(int[] intRgb) {
		if (intRgb.length != 3) {
			return 0xffffff;
		}
		System.out.println(intRgb[0] + "|" + intRgb[1] + "|" + intRgb[2] + "|");
		return Color.rgb(intRgb[0], intRgb[1], intRgb[2]);
	}
}
