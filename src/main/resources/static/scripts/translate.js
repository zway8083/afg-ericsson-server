function confirmChangeLanguage(langue){
    if (confirm("Vous avez selectionné la langue : " + langue))
        return true;
}


function getXMLHttpRequest() {
    var xhr = null;
    if (window.XMLHttpRequest || window.ActiveXObject) {
        if (window.ActiveXObject) {
            try {
                xhr = new ActiveXObject("Msxml2.XMLHTTP");
            } catch(e) {
                xhr = new ActiveXObject("Microsoft.XMLHTTP");
            }
        } else {
            xhr = new XMLHttpRequest();
        }
    } else {
        alert("Votre navigateur ne supporte pas l'objet XMLHTTPRequest...");
        return null;
    }
    return xhr;
}
// initialisation du catalogue
var catalogue = [];
function executerRequete(callback) {
    // on vérifie si le catalogue a déjà été chargé pour n'exécuter la requête AJAX
    // qu'une seule fois
    if (catalogue.length === 0) {
        // on récupère un objet XMLHttpRequest
        var xhr = getXMLHttpRequest();
        // on réagit à l'événement onreadystatechange
        xhr.onreadystatechange = function() {
            // test du statut de retour de la requête AJAX
            if (xhr.readyState == 4 && (xhr.status == 200 || xhr.status == 0)) {
                // on désérialise le catalogue et on le sauvegarde dans une variable
                catalogue = JSON.parse(xhr.responseText);
                // on lance la fonction de callback avec le catalogue récupéré
                callback();
            }
        }
        // la requête AJAX : lecture de data.json
        xhr.open("GET", "multilangues.json", true);
        xhr.send();
    } else {
        // on lance la fonction de callback avec le catalogue déjà récupéré précédemment
        callback();
    }
}

function versionFr() {
    for(var i= 0; i <id_list.length; i++) {
        if (document.getElementById(id_list[i])===null) {
            //alert("id n'est pas dans la page");
        }
        else{
            document.getElementById(id_list[i]).innerHTML = catalogue["fr"][id_list[i]];
         }
        var x=document.getElementsByClassName(id_list[i]);
        if (x.length!==0){
            //alert("il y a : " + x.length + " "  + id_list[i] + " dans la page");
        }

        for(var j=0; j <x.length;j++){
            x[j].innerHTML= catalogue["fr"][id_list[i]];
        }
    }

}

function versionEn() {
    for(var i= 0; i <id_list.length; i++) {
        //alert("id  " + id_list2[i]);
        if (document.getElementById(id_list[i])===null) {
            //alert("id n'est pas dans la page");
        }
        else{
            document.getElementById(id_list[i]).innerHTML = catalogue["en"][id_list[i]];
        }
        var x=document.getElementsByClassName(id_list[i]);
        if (x.length!==0){
            //alert("il y a : " + x.length + " "  + id_list[i] + " dans la page");
        }

        for(var j=0; j <x.length;j++){
            x[j].innerHTML= catalogue["en"][id_list[i]];
        }


    }

}
function versionSp() {
    for(var i= 0; i <id_list.length; i++) {
        if (document.getElementById(id_list[i])===null) {
            //alert("id n'est pas dans la page");
        }
        else{
            document.getElementById(id_list[i]).innerHTML = catalogue["sp"][id_list[i]];
        }
        var x=document.getElementsByClassName(id_list[i]);
        if (x.length!==0){
            //alert("il y a : " + x.length + " "  + id_list[i] + " dans la page");
        }

        for(var j=0; j <x.length;j++){
            x[j].innerHTML= catalogue["sp"][id_list[i]];
        }
    }

}

function dofr(){
    $.ajax().done(function(e) {
        location.reload(true);
    });
}

function doen(){
    $.ajax().done(function(e) {
        location.reload(true);
    });

}
function dosp(){
    $.ajax().done(function(e) {
        location.reload(true);
    });
}