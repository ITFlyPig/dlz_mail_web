package com.dlz.mail.utils;

public class Log {
//	/**
//	 * 打印log
//	 * @param tag
//	 * @param text
//	 */
//	public static void d(String tag, String text){
//		if (tag == null) {
//			tag = "";
//		}
//		if (text == null) {
//			text = "";
//		}
//		System.out.println(tag + ": " + text);
//	}
//
//	/**
//	 * 打印log
//	 * @param text
//	 */
//	public static void d( String text){
//		if (text == null) {
//			text = "";
//		}
//		System.out.println( ": " + text);
//	}

//	/**
//	 * 打印log
//	 * @param text
//	 */
//	public static void e( String text){
//		if (text == null) {
//			text = "";
//		}
//
//
//	}

	/**
	 * 只可以获取当面的，有局限性
	 *
	 * 获取文件名、方法名和行号
	 * @return
	 */
	private static String[] getUseableInfo(){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement element = stackTraceElements[2];
		return new String[]{element.getFileName(), element.getMethodName(), String.valueOf(element.getLineNumber())};

	}

}
