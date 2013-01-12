(function(window){

    $('#input-type').change(function() {
        var el = $(this);
        var val = el.val();
        if(val === 'text')
            $('div.extra-controls').empty();
        else if(val === 'choices') {
            var choices = $($('script#choices').html());
            var newchoice = $($('script#choice').html());
            choices.find('div.controls').append(newchoice);
            $('div.extra-controls').html(choices);
        }
    });

    $('i#add-choice', 'body').live('click', function() {
        var newchoice = $($('script#choice').html());
        $('div.extra-controls div.controls').append(newchoice);
    });

}(window));
