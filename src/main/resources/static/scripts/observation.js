var grade = 0;
history.pushState({},"","/observation");

$(function() {
	$("#grade").attr("value", 0);
	$("#grade-btn").click(function() {
		grade = (grade + 1) % 3
		if (grade === 0) {
			$("i", this).attr("class", "fa fa-smile-o fa-3x");
			$("i", this).css("color", "LimeGreen");
			$("#grade").attr("value", 0);
		}
		if (grade === 1) {
			$("i", this).attr("class", "fa fa-meh-o fa-3x");
			$("i", this).css("color", "Gold");
			$("#grade").attr("value", 1);
		}
		if (grade === 2) {
			$("i", this).attr("class", "fa fa-frown-o fa-3x");
			$("i", this).css("color", "Red");
			$("#grade").attr("value", 2);
		}
	});
	
	$(".obs-grade").each(function() {
		var color = "";
		switch($(this).attr("value")) {
			case "0":
				color = "LimeGreen";
				break;
			case "1":
				color = "Gold";
				break;
			case "2":
				color = "Red";
				break;
			default:
				color = "Grey";
		}
		$(this).css("box-shadow", "5px 0px 0px 0px " + color + " inset");
	});
	
	
});

function confirmDeleteMessage(descriptionId){

	if (confirm("Voulez vous supprimer votre description?"))
	{
		
	var deleteForm = document.getElementById("deleteForm");
	
	deleteForm.method="post";
    deleteForm.action="/observation/"+descriptionId+"/delete";
	deleteForm.submit();

   
	return true;
	}
	else{ 
		
		
		return false;}
	
	
}