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
	$('.form-night').each(function() {
		$(this).submit(function () {
			$(this).find('.input-group.date').each(function() {
		        $(this).children().eq(0).attr('type', 'text');
		    });
		})
	})
});