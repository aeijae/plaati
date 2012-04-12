$(() ->
    updateRating = (newRating) ->
        parentSong = this.parents('.song')
        song = parentSong.attr('id')
        ratedSongCount = $('#rated-songs')
        ratedAlbumCount = $('#rated-albums')

        $.post('/rating/' + song + '/' + newRating, () =>
            parentAlbum = parentSong.parents('.album')

            this.siblings().removeClass('selected')

            if newRating is -1
                if !parentAlbum.hasClass('not-rated')
                    parentAlbum.addClass('not-rated')
                    updateRatedAlbums(-1)

                if !parentSong.hasClass('not-rated')
                    updateRatedSongs(-1)
                    parentSong.addClass('not-rated')
                    this.siblings().removeClass('selected')
            else
                if parentSong.hasClass('not-rated')
                    updateRatedSongs(1)
                    parentSong.removeClass('not-rated')

                if !parentSong.siblings('.not-rated').length and parentAlbum.hasClass('not-rated')
                    parentAlbum.removeClass('not-rated')
                    updateRatedAlbums(1)

                this.addClass('selected')
        )

        updateRatedSongs = (amount) ->
            ratedSongCount.text(parseInt(ratedSongCount.text()) + amount)

        updateRatedAlbums = (amount) ->
            ratedAlbumCount.text(parseInt(ratedAlbumCount.text()) + amount)

    $('.rating').bind('click', () ->
        updateRating.call($(this), $(this).attr('data-rating'))
    )

    $('.remove-rating').bind('click', () ->
        updateRating.call($(this), -1)
    )

    $('.comment').bind('keyup', () ->
        self = $(this)
        song = self.parents('.song').attr('id')

        $.post('/rating/' + song, comment: self.attr('value'), () -> )
    )

    $('.up').bind('click', () ->
        firstAboveTop = $('.song.not-rated:above-the-top:last')

        if firstAboveTop.length > 0
            $.scrollTo(firstAboveTop, duration: 300, offset: top: -10);
    )

    $('.down').bind('click', () ->
        $.scrollTo($('.song.not-rated:below-the-fold:first'), duration: 300, offset: top: -100);
    )
)