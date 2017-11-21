$(function() {
	$("#raspberryList").hide();
	$("#switch").prop('checked', true);
	$("#switch").click(function() {
		if (this.checked) {
			$("#raspberryList").hide();
		} else {
			$("#raspberryList").show();
		}
	});
});