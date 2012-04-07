$(function() {
    $('.rating').bind('click', function() {
        var self = $(this),
            rating = self.attr('id'),
            parentSong = self.parents('.song');
            song = parentSong.attr('id');

        $.post('/rating/' + song + '/' + rating, function() {
            self.siblings().removeClass('selected');
            self.addClass('selected');
            parentSong.removeClass('not-rated');
        })
    });

    $('.comment').bind('keyup', function() {
        var self = $(this),
            comment = self.attr('value'),
            song = self.parents('.song').attr('id');

        $.post('/rating/' + song, { comment:comment }, function() {
        });
    });

    $('textarea').bind('focus', function() {
        this.style.background = '#e5fff3';
    }).bind('blur', function() {
        this.style.background = 'white';
    });
});