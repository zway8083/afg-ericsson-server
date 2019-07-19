$(function() {	
	$(".report-grade table tr").each(function( i ) {
		$("td", this).each(function( j ) {
	  		if (j == 3) {
			    console.log("".concat("row: ", i, ", col: ", j, ", value: ", $(this).text()));
			    var partsArray = $(this).text().split('%');
			    
				if (partsArray[0] < 60) {
						color = "Red";
						$(this).css("box-shadow", "5px 0px 0px 0px " + color + " inset");
				} else if (partsArray[0] < 90) {
						color = "Gold";
						$(this).css("box-shadow", "5px 0px 0px 0px " + color + " inset");
				} else if (partsArray[0] >= 90 && partsArray[0] <= 100) {
						color = "LimeGreen";
						$(this).css("box-shadow", "5px 0px 0px 0px " + color + " inset");
				}
			}
		});
	});
	$(".report-grade .fill span").each(function(){
		var percent = $(this).html();
		var pTop = 100 - ( percent.slice(0, percent.length - 1) ) + "%";
		$(this).parent().css({
			'height' : percent,
			'top' : pTop
		});
		if (percent.slice(0, percent.length - 1) < 60) {
			//color = "Red";
			$(this).parent().css({'background': "#FF0000"});
		} else if (percent.slice(0, percent.length - 1) < 90) {
			//color = "Gold";
			$(this).parent().css({'background': "#FFD700"});
		} else if (percent.slice(0, percent.length - 1) >= 90 && percent.slice(0, percent.length - 1) <= 100) {
			//color = "LimeGreen";
			$(this).parent().css({'background':"#32CD32"});
		}
	});
	$(".report-grade .hours span").each(function(){
		var mov = $(this).html();
		var percenthours = (mov.slice(0, mov.length))/70*100 +"%"; //Define that the maximum accepted movement is 70
		var pTophours = 100 - ( percenthours.slice(0, percenthours.length - 1) ) + "%";
		$(this).parent().css({
			'height' : percenthours,
			'top' : pTophours
		});
		$(this).parent().css({'background': "#626161"});
	});
});