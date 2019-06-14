package server.utils;

import java.util.ArrayList;

public class   HTMLGenerator {
	private final static String indentStyle = "style=\"%s-left: %dpx;\"";
	private final static int indentBase = 30;

	private static String padding(final int indentLevel) {
		if (indentLevel == 0)
			return "";
		return String.format(indentStyle, "padding", indentLevel * indentBase);
	}

	private static String margin(final int indentLevel) {
		if (indentLevel == 0)
			return "";
		return String.format(indentStyle, "margin", indentLevel * indentBase);
	}

	public static String strongAttributeValue(final String attribute, final String value, final int indentLevel) {
		return String.format("<p %s><strong>%s :&nbsp;</strong>%s</p>\r\n", padding(indentLevel), attribute, value,
				indentLevel * indentBase);
	}

	public static String strongAttribute(final String attribute, final int indentLevel) {
		return String.format("<p %s><strong>%s :&nbsp;</strong></p>\r\n", padding(indentLevel), attribute);
	}
	
	public static String value(final String value, final int indentLevel) {
		return String.format("<p %s>%s</p>\r\n", padding(indentLevel), value);
	}

	private static String columnStyle(final int indentLevel) {
		if (indentLevel != 0)
			return String.format("style=\"text-align: center; padding-left: %dpx;\"", indentLevel * indentBase);
		return "style=\"text-align: center;\"";
	}

	public static String table(final ArrayList<ArrayList<String>> values, final int indentLevel) {
		if (values.size() == 0)
			throw new IllegalArgumentException("Invalid table size");
		final int height = values.size();
		final int width = values.get(0).size();
		String table = String.format("<table %s border=\"5\"><tbody %s>\r\n", margin(indentLevel), padding(indentLevel));
		for (int i = 0; i < height; i++) {
			String row = String.format("<tr %s>\r\n", padding(indentLevel));
			final String style = columnStyle(indentLevel);
			for (int j = 0; j < width; j++) {
				row += String.format("<td %s>%s</td>\r\n", style, values.get(i).get(j));
			}
			row += "</tr>\r\n";
			table += row;
		}
		table += "</tbody></table>\r\n";
		return table;
	}
}
