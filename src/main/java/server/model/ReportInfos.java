package server.model;

import java.util.ArrayList;
import java.util.List;

public class ReportInfos {
	private Long id;
	private String date;
	private List<String> actions;

	public ReportInfos() {
		actions = new ArrayList<>();
	}

	public ReportInfos(Long id, String date) {
		this.id = id;
		this.date = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}
}
