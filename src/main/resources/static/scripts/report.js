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
});