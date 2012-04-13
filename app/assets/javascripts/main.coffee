$(() ->
    typingTimer = null
    xhr = null

    $('.rating').bind('click', () ->
        updateRating.call($(@), $(@).attr('data-rating'))
    )

    $('.remove-rating').bind('click', () ->
        updateRating.call($(@), 0)
    )

    updateRating = (newRating) ->
        song = @.parents('.song')
        songId = song.attr('id')

        $.post('/rating/' + songId + '/' + newRating, () =>
            onRatingUpdated.call(@, newRating, song)
        )

    updateRatedSongs = (amount) ->
        updateNumericField($('#rated-songs'), amount)

    updateRatedAlbums = (amount) ->
        updateNumericField($('#rated-albums'), amount)

    updateNumericField = (field, amount) ->
        field.text(parseInt(field.text()) + amount)

    onRatingUpdated = (rating, song) ->
        album = song.parents('.album')

        @siblings().removeClass('selected')

        if rating
            if song.hasClass('not-rated')
                song.removeClass('not-rated')
                updateRatedSongs(1)

            if !song.siblings('.not-rated').length and album.hasClass('not-rated')
                album.removeClass('not-rated')
                updateRatedAlbums(1)

            @addClass 'selected'
        else
            if !album.hasClass('not-rated')
                album.addClass('not-rated')
                updateRatedAlbums(-1)

            if !song.hasClass('not-rated')
                song.addClass('not-rated')
                updateRatedSongs(-1)

    $('.up').bind('click', () ->
        scrollToSong '.song.not-rated:above-the-top:last'
    )

    $('.down').bind('click', () ->
        scrollToSong '.song.not-rated:below-the-fold:first'
    )

    scrollToSong = (element) ->
        if $(element).length
            $.scrollTo($(element), duration: 300, offset: top: -50)

    $('.comment').keyupAsObservable()
        .throttle(2000)
        .subscribe((evt) ->
            song = $(evt.target).parents('.song').attr('id')
            $.post('/rating/' + song, comment: $(evt.target).val(), () -> )
        )
)
