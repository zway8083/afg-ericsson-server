$(function(){
	$('#error').hide();
	$('#form-password').submit(function() {
		var password = $('#newPassword');
		var repeat = $('#repeat');
		var errMsg = $('#error');
		var err = false;
		if (password.val().length < 6) {
			errMsg.text("Le mot de passe doit comporter au moins 6 caractères.");
			err = true;
		} else if (password.val() !== repeat.val()) {
			password.val("");
			repeat.val("");
			errMsg.text("Les deux mots de passe sont différents.");
			err = true;
		}
		if (err === true) {
			$(".alert").hide();
			errMsg.show();
		} else {
			errMsg.hide();
		}
		return !err;
	});
});