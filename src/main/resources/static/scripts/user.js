$(function() {
	$("#sleepStart").hide();
	$("#sleepEnd").hide();
	$("#check").prop('checked', false);
	$("#check").click(function() {
		if (this.checked) {
			$("#sleepStart").show();
			$("#sleepEnd").show();
			$("#sleepStartInput").prop('required', true);
			$("#sleepEndInput").prop('required', true);
		} else {
			$("#sleepStart").hide();
			$("#sleepEnd").hide();
			$("#sleepStartInput").prop('required', false);
			$("#sleepEndInput").prop('required', false);
		}
	});
});
