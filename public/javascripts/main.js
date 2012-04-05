$(function() {
    $('.rating').bind('click', function() {
        var self = $(this),
            rating = self.attr('id'),
            song = self.parents('.song').attr('id');

        $.post('/rating/' + song + '/' + rating, function() {
            self.siblings().removeClass('selected');
            self.addClass('selected');
        })
    });

    $('.comment').bind('keypress', function() {

    });

    $('textarea').bind('focus', function() {
        this.style.background = '#e5fff3';
    }).bind('blur', function() {
        this.style.background = 'white';
    });
});