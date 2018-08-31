$(function(){
	$('#form').submit(function() {
		var email = $("#email").val();
		return confirm("Un email de confirmation sera envoyé à l'adresse " + email + ".");
	});
});