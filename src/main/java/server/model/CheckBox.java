package server.model;

import java.util.List;

public class CheckBox {
	private List<String> CheckedRoles;

	public CheckBox() {
	}

	public List<String> getCheckedRoles() {
		return CheckedRoles;
	}

	public void setCheckedRoles(List<String> checkedRoles) {
		CheckedRoles = checkedRoles;
	}

}
