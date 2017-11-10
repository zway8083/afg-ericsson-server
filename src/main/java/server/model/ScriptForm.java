package server.model;

public class ScriptForm {
	private Long userId;
	private String script;

	public ScriptForm() {
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
