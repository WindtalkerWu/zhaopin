package com.qianniu.zhaopin.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;

public class NumberUtils {
	/**
	 * 随机指定范围内N个不重复的数 利用HashSet的特征，只能存放不同的值
	 * 
	 * @param min
	 *            指定范围最小值
	 * @param max
	 *            指定范围最大值
	 * @param n
	 *            随机数个数
	 * @param HashSet
	 *            <Integer> set 随机数结果集
	 */
	public static void randomSet(int min, int max, int n, HashSet<Integer> set) {
		if (n > (max - min + 1) || max < min) {
			return;
		}
		for (int i = 0; i < n; i++) {
			// 调用Math.random()方法
			int num = (int) (Math.random() * (max - min)) + min;
			set.add(num);// 将不同的数存入HashSet中
		}
		int setSize = set.size();
		// 如果存入的数小于指定生成的个数，则调用递归再生成剩余个数的随机数，如此循环，直到达到指定大小
		if (setSize < n) {
			randomSet(min, max, n - setSize, set);// 递归
		}
	}

	public static int[] rang(int startNumber, int endNumber, int count) {
		int total = 0;// 已经生成数据量
		boolean flag = false;// 是否重复,true 重复,false 不重复

		int[] intArray = new int[count];
		Random random = new Random();
		int intTemp;// 随机生成的临时数据
		if (count > (endNumber - startNumber) + 1) {
			System.exit(0);
		}
		while (total < count) {
			intTemp = random.nextInt(endNumber - startNumber + 1) + startNumber;
			// 判断是否与生成的数据相等
			for (int j = 0; j < total; j++) {
				if (intTemp == intArray[j]) {
					flag = true;
					break;
				} else {
					flag = false;
				}
			}
			if (false == flag) {
				intArray[total] = intTemp;
				total++;
			}
		}

		return intArray;
	}

	public static boolean isHighVersion(String oldVersion, String newVersion) {
		String[] oldV = oldVersion.split("\\.");
		String[] newV = newVersion.split("\\.");
		// 如果版本.长度不一样，后面的缺省为0，如果保证长度一样，这里可以不需要
		if (oldV.length < newV.length) {
			String[] t = new String[newV.length];
			System.arraycopy(oldV, 0, t, 0, oldV.length);
			for (int i = oldV.length; i < t.length; i++) {
				t[i] = "0";
			}
			oldV = t;
		} else if (oldV.length > newV.length) {
			String[] t = new String[oldV.length];
			System.arraycopy(newV, 0, t, 0, newV.length);
			for (int i = newV.length; i < t.length; i++) {
				t[i] = "0";
			}
			newV = t;
		}

		for (int i = 0; i < oldV.length; i++) {
			try {
				int n1 = Integer.valueOf(oldV[i]).intValue();
				int n2 = Integer.valueOf(newV[i]).intValue();
				if (n1 < n2) {
					return true;
				} else if (n1 > n2) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}
	public static String scaleFormat2(float value) {
		DecimalFormat df2  = new DecimalFormat("###.00");
		return df2.format(value);
	}
	//缩放到小数点后2位
	public static float scaleDecimal2(float value) {
		String str = String.format("%.2f", value);
		float result = Float.parseFloat(str);
//		System.out.println("value##" + value + "");
//		BigDecimal bd = new BigDecimal(value + "");
//		System.out.println("BigDecimal##" + bd);
//		bd.setScale(2, BigDecimal.ROUND_HALF_UP);
//		float result = bd.floatValue();
//		System.out.println("result##" + result);
//		bd = null;
		return result;
	}
}
