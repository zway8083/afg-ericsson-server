function confirmDeleteMessage(subjectFirstName,subjectId){

	if (confirm("Voulez vous supprimer le sujet "+subjectFirstName +"?"))
	{
		
	var deleteForm = document.getElementById("deleteForm");
	
	deleteForm.method="post";
    deleteForm.action="/mysubjects/"+subjectId+"/delete";
    deleteForm.submit();
     return true;
	}
	else return false;
	
	
}
