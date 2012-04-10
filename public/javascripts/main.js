$(function() {
    var updateRating = function(newRating) {
        var self = $(this),
            parentSong = self.parents('.song'),
            song = parentSong.attr('id');

        $.post('/rating/' + song + '/' + newRating, function() {
            self.siblings().removeClass('selected');

            if (parentSong.siblings('.not-rated').length === 0) {
                parentSong.parents('.album').removeClass('not-rated');
            }
            if (newRating === -1) {
                self.siblings().removeClass('selected');
                parentSong.addClass('not-rated').parents('.album').addClass('not-rated');
            } else {
                self.addClass('selected');
                parentSong.removeClass('not-rated');
            }
        })
    }

    $('.rating').bind('click', function() {
        updateRating.call(this, $(this).attr('data-rating'));
    });

    $('.remove-rating').bind('click', function() {
        updateRating.call(this, -1);
    });

    $('.comment').bind('keyup', function() {
        var self = $(this),
            comment = self.attr('value'),
            song = self.parents('.song').attr('id');

        $.post('/rating/' + song, { comment:comment }, function() {});
    });
});