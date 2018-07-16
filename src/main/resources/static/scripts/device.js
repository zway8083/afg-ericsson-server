function confirmDeleteMessage(subjectFirstName,deviceSerial,deviceId){

	if (confirm("Voulez vous supprimer le "+deviceSerial+" de sujet "+subjectFirstName +"?"))
	{
		
	var deleteForm = document.getElementById("deleteForm");
	
	deleteForm.method="post";
    deleteForm.action="/mydevices/"+deviceId+"/delete";
    deleteForm.submit();
     return true;
	}
	else return false;
	
	
}
