$(function () {
	$('.input-group.date').each(function() {
		var elem = this;
		if ($(elem).hasClass('sleep-start')) {
			$(elem).datetimepicker({
				locale: 'fr',
				format: 'HH:mm'
			});
		} else if ($(elem).hasClass('sleep-end')) {
			$(elem).datetimepicker({
				locale: 'fr',
				format: 'HH:mm'
			});
		}
		$(elem).children().eq(0).on('click', function() {
			$(elem).children().eq(1).click();
		})
	});

	//$('.input-group.checkbox').each(function() {
	//	var test = localStorage.input === 'true'? true: false;
	//	$('input').prop('checked', test || false);
	//});

	//$('input').on('change', function() {
	//	localStorage.input = $(this).is(':checked');
	//	console.log($(this).is(':checked'));
	//});

	$('.form-night').each(function() {
		$(this).submit(function () {
			$(this).find('.input-group.date').each(function() {
				$(this).children().eq(0).attr('type', 'text');
			});
		})
	})
});








//$(elem).prop("emailON", true);
//querySelector.ClassDiv("divNAme").add Attribute to emailON (checked)
//$(elem).("emailON", true);
//$(elem).on('change', function() {
//localStorage.input = $(elem).is(':checked');
//console.log($(elem).is(':checked'));
//});
