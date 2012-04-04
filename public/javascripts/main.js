$(function() {
    var createRater = function() {
        var html = '';

        for (var i = 0; i < 10; i++) {
            html += '<span class="rating" id="' + (i + 1) + '">' + (i + 1) + '</span>';
        }
        return html;
    };

    $('.rater').html(createRater());

});