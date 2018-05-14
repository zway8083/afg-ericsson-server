$(function(){
	$('#form').submit(function() {
		var email = $("#email").val();
		return confirm("Un email de confirmation sera envoyé à l'adresse " + email + ".");
	});

	

	
});

function confirmDeleteMessage(accompanistFirstName,accompanistId,subjectFirstName,subjectId){

	if (confirm("Are you sure want to delete " + accompanistFirstName+" from the subject "+subjectFirstName +"?"))
	{
		
	//post("/accompanist/"+accompanistId+"/"+subjectId+"/delete");
	//post("/accompanist/delete");
	var deleteForm = document.getElementById("deleteForm");
	
	deleteForm.method="post";
    deleteForm.action="/accompanist/"+accompanistId+"/"+subjectId+"/delete";
    deleteForm.submit();
     return true;
	}
	else return false;
	
	
}