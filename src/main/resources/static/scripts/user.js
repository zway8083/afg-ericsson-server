$(function() {
	$("#sleepStart").hide();
	$("#sleepEnd").hide();
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
		} else {
			$("#sleepStart").hide();
			$("#sleepEnd").hide();
			$("#password").prop('required', true);
			$("#email").prop('required', true);
			$("#email").attr("placeholder", "obligatoire");
			$("#password").attr("placeholder", "obligatoire");
		}
	});
});

$( document ).ready(function() {
	$('#role').trigger("change");
});
