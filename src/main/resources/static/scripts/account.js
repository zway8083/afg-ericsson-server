function confirmDeleteAccount(name,userEmail){

    if (confirm("VOULEZ-VOUS VRAIMENT SUPPRIMER LE COMPTE DE "+ name + " utilisant l'adresse email " +userEmail + "? " + "ATTENTION LA SUPPRESSION DE VOTRE COMPTE SERA PERMANENTE. SI VOUS ETES LE SEUL PARENT RATTACHE  A UN ENFANT, ALORS SES DONNEES SERONT AUSSI SUPPRIMEES"))
    {return true;}
    else return false;


}