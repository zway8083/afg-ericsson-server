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


	private static String progressBar(final int indentLevel) {
		if (indentLevel != 0)
			return String.format("style=\"float: left; height: %dpx; width: 30px; margin-right: 35px;\"", indentLevel * indentBase *10);
		return "style=\"text-align: center;\"";
	}
	private static String progressTrack(final int indentLevel) {
		if (indentLevel != 0)
			return String.format("style=\"position: relative; height: 300px; width: %dpx; background: #f5f5f5;\"", indentLevel * indentBase);
		return "style=\"text-align: center;\"";
	}
	private static String progressFill(final int indentLevel) {
		if (indentLevel != 0)
			return String.format("style=\"position: relative; width: %dpx; color: #000; text-align: center; font-size: 12px; line-height: 20px;\"", indentLevel * indentBase);
		return "style=\"text-align: center;\"";
	}




	public static String barPourcent(final ArrayList<ArrayList<String>> results, int indentLevel){
		if (results.size() == 0)
			throw new IllegalArgumentException("Invalid bar size");
		final int height = results.size();
		final int width = results.get(0).size();

		int i=0;
		String bar = String.format("<div %s >","padding-left: 30px;");

		for (int j = 0; j < width; j++) {
			bar+= String.format("<div %s>", progressBar(indentLevel))+ String.format("<div %s>",progressTrack(indentLevel)) + String.format("<div class=\"fill\"  %s>",progressFill(indentLevel))+ String.format("<span > %s",results.get(i).get(j));
			bar+="</span>\r\n</div>\r\n </div>\r\n";
			bar+="<div style=\" text-align : center;\">" + String.format("<span> %s",results.get(1).get(j)) +"</span>\r\n</div>\r\n</div>\r\n";

		}
		bar+="<div style=\" padding:200px;\">\r\n </div>";
		bar+="</div>\r\n";

		return bar;
	}

	public static String barHours(final ArrayList<ArrayList<String>> results, int indentLevel){
		if (results.size() == 0)
			throw new IllegalArgumentException("Invalid bar size");
		final int height = results.size();
		final int width = results.get(0).size();

		int i=0;
		String bar = String.format("<div %s >","padding-left: 30px;");

		for (int j = 0; j < width; j++) {
			bar+= String.format("<div class=\"bar\" %s>", progressBar(indentLevel))+ String.format("<div class=\"track\" %s>",progressTrack(indentLevel)) + String.format("<div class=\"hours\" %s>",progressFill(indentLevel))+ String.format("<span > %s",results.get(i).get(j));
			bar+="</span>\r\n</div>\r\n </div>\r\n" + "<div style=\"padding:10px;\">\r\n </div>\r\n";
			bar+="<div style=\" text-align : center;\">" + String.format("<span> %s",results.get(1).get(j)) +"</span>\r\n</div>\r\n</div>\r\n";

		}
		bar+="<div style=\" padding:200px;\">\r\n </div>";
		bar+="</div>\r\n";

		return bar;
	}
}
