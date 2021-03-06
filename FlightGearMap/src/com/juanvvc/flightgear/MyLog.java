
package com.juanvvc.flightgear;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.util.Log;

/** Use this class instead of android.util.Log: simplify the process of uploading to Google Play
 * @author juanvi
 */
public class MyLog{
	private static final boolean debug=false;
	
	public static void i(Object o, String msg){
		if(debug) Log.i(o.getClass().getSimpleName(), msg);
	}
	public static void d(Object o, String msg){
		if(debug) Log.d(o.getClass().getSimpleName(), msg);
	}
	public static void v(Object o, String msg){
		if(debug) Log.v(o.getClass().getSimpleName(), msg);
	}
	public static void e(Object o, String msg){
		if(debug) Log.e(o.getClass().getSimpleName(), msg);
	}
	public static void w(Object o, String msg){
		if(debug) Log.e(o.getClass().getSimpleName(), msg);
	}
	
	public static String stackToString(Exception e) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		return result.toString();
	}
}