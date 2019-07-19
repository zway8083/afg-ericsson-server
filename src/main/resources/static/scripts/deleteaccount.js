$(document).ready(function(){
    var cc = document.getElementById('select-all');
    $(cc).on('click', function(){
        if (cc.checked){
            $('input[type=checkbox]').prop('checked', true);
        } else {
            $('input[type=checkbox]').prop('checked', false);
        }
    });
});


$(document).ready(function(){
    var ct = document.getElementById('button');
    $(ct).on('click', function(){
        document.getElementById('deleteForm').submit();
        document.getElementById('logout-form').submit();
    });
});


