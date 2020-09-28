package com.xhumanity.social.utils;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class Utils {

	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public static boolean isEmailValid(String email) {
		if (email == null) {
			return false;
		} else {
			String regex = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
			return Pattern.matches(regex, email);
		}
	}

	public static StringBuffer replace(StringBuffer sb, String text) {
		sb.delete(0, sb.length());
		sb.append(text);
		return sb;
	}
}
