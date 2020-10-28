/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function () {
    /* Aquí podría filtrar que controles necesitará manejar,
     * en el caso de incluir un dropbox $('input, select');
     */
    tb = $('input');

    if ($.browser.mozilla) {
        $(tb).keypress(enter2tab);
    } else {
        $(tb).keydown(enter2tab);
    }
});


function enter2tab(e) {
    
    if (e.keyCode == 13) {
        
        cb = parseInt($(this).attr('tabindex'));

        if ($(':input[tabindex=\'' + (cb + 1) + '\']') != null) {
            $(':input[tabindex=\'' + (cb + 1) + '\']').focus();
            $(':input[tabindex=\'' + (cb + 1) + '\']').select();
            e.preventDefault();

            return false;
        }
    }
}