function createCookie(name,value,days) {
	if (days) {
		var date = new Date();
		date.setTime(date.getTime()+(days*24*60*60*1000));
		var expires = "; expires="+date.toGMTString();
	}
	else var expires = "";
	document.cookie = name+"="+value+expires+"; path=/";
}

function readCookie(name) {
	var nameEQ = name + "=";
	var ca = document.cookie.split(';');
	for(var i=0;i < ca.length;i++) {
		  var c = ca[i];
		while (c.charAt(0)==' ') c = c.substring(1,c.length);
		if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
	}
	return null;
}

function eraseCookie(name) {
	createCookie(name,"",-1);
}

function hideMessage(){
	document.getElementById("footer_tc_privacy").style.visibility='hidden';
}

function check() {
	var langue = "";
	langue = readCookie("lang");
	//alert(langue);
	if (langue !== null && langue !== "inconnu") {
		document.getElementById("footer_tc_privacy").style.visibility='hidden';
	} else {
		document.getElementById("footer_tc_privacy").style.visibility='visible';
	}
}

function checktranslation() {
	var langue = "";
	langue = readCookie("lang");
	if (langue !== null && langue !== "inconnu") {
		if (langue==="1"){
			executerRequete(versionFr);
			//alert("on devrait avoir les mots traduits en français");
		}
		else if(langue ==="2"){
			executerRequete(versionEn);
			//alert("on devrait avoir les 	mots traduits en anglais");
		}
		else if(langue ==="3"){
			executerRequete(versionSp);
			//alert("on devrait avoir les mots traduits en anglais");
		}
		else{
			alert("La langue ne correspond pas")
		}
	} else {
		document.getElementById("footer_tc_privacy").style.visibility='visible';
		executerRequete(versionEn);
	}
}