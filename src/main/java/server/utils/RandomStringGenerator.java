package server.utils;

import java.security.SecureRandom;

public class RandomStringGenerator {
	public static String randomString(final int length) {
		final char[] specials = new char[] { '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+' };
		char[] symbols = new char[('z' - 'a' + 1) * 2 + 10 + specials.length];
		// Fill symbols with alphanumeric chars + specials
		int i = 0;
		for (char c = 'a'; c <= 'z'; c++)
			symbols[i++] = c;
		for (char c = 'A'; c <= 'Z'; c++)
			symbols[i++] = c;
		for (char c = '0'; c <= '9'; c++)
			symbols[i++] = c;
		for (char c : specials) {
			symbols[i++] = c;
		}

		SecureRandom random = new SecureRandom();
		char[] buf = new char[length];
		for (i = 0; i < length; i++)
			buf[i] = symbols[random.nextInt(symbols.length)];

		return new String(buf);
	}
}
