$(function() {
	$("#sleepStart").hide();
	$("#sleepEnd").hide();
	//$("#emailON").hide();
	$("#password").prop('required', true);
	$("#email").prop('required', true);
	$('#role').on('change', function() {
		if (this.value === 'ROLE_SUJET') {
			$("#sleepStart").show();
			$("#sleepEnd").show();
			$("#email").prop('required', false);
			$("#password").prop('required', false);
			$("#email").attr("placeholder", "");
			$("#password").attr("placeholder", "");
			//$("#emailON").show();
		} else {
			$("#sleepStart").hide();
			$("#sleepEnd").hide();
			$("#password").prop('required', true);
			$("#email").prop('required', true);
			$("#email").attr("placeholder", "mandatory");
			$("#password").attr("placeholder", "mandatory");
			//$("#emailON").hide();
		}
	});
});

$( document ).ready(function() {
	$('#role').trigger("change");
});
