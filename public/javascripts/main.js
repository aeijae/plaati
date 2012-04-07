$(function() {
    $('.rating').bind('click', function() {
        var self = $(this),
            rating = self.attr('data-rating'),
            parentSong = self.parents('.song'),
            song = parentSong.attr('id');

        $.post('/rating/' + song + '/' + rating, function() {
            self.addClass('selected')
                .siblings().removeClass('selected');
            parentSong.removeClass('not-rated');

            if (parentSong.siblings('.not-rated').length == 0) {
                parentSong.parents('.album').removeClass('not-rated');
            }
        })
    });

    $('.remove-rating').bind('click', function() {
        var self = $(this),
            parentSong = self.parents('.song'),
            song = parentSong.attr('id'),
            comment = parentSong.find('.comment').attr('value');

        $.post('/rating/' + song + '/-1', function() {
            self.siblings().removeClass('selected');
            parentSong.addClass('not-rated').parents('.album').addClass('not-rated');
        })
    });

    $('.comment').bind('keyup', function() {
        var self = $(this),
            comment = self.attr('value'),
            song = self.parents('.song').attr('id');

        $.post('/rating/' + song, { comment:comment }, function() {});
    });
});