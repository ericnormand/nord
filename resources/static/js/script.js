(function(window){
  window.done = false;
  window.data = null;

  $.ajax('/location/list.json', {dataType: 'json',
         success: function(d) {
           if(window.done) {
             $.each(d.parks, function(i, e) {
               if(e.latitude && e.longitude) {
                 console.log(e.name);
                 console.log(e.latitude);
                 console.log(e.longitude);
                 var point = new google.maps.LatLng(e.latitude, e.longitude);
                 $('#map-view').gmap('addMarker', {'position': point, 
                                     'bounds': false, 
                                     'icon' : pin});
               }
             });
           }
           data = d;
           makeNeighborhoodList();
         }});

         $('#input-type').change(function() {
           var el = $(this);
           var val = el.val();
           if(val === 'choices') {
             var choices = $($('script#choices').html());
             var newchoice = $($('script#choice').html());
             choices.find('div.controls').append(newchoice);
             $('div.extra-controls').html(choices);
           } else
             $('div.extra-controls').empty();
         });

         $('i#add-choice', 'body').live('click', function() {
           var newchoice = $($('script#choice').html());
           $('div.extra-controls div.controls').append(newchoice);
         });

         $('img.sel').click(function() {
           var el = $('div.selector-list');
           if(el.hasClass('active')) {
             el.removeClass('active');
             $('header').removeClass('selector-list');
           } else {
             el.addClass('active');
             $('header').addClass('selector-list');
             $('#map-view').click(function(){
               $('#map-view').unbind('click');
               $('img.sel').trigger('click');
             });
           }
         });



         $('img.list').click(function() {
           var el = $('div.neigh-list');
           if(el.hasClass('active')) {
             el.removeClass('active');
             $('header').removeClass('neigh-list');
           } else {
             el.addClass('active');
             $('header').addClass('neigh-list');
             $('#map-view').click(function(){
               $('#map-view').unbind('click');
               $('img.list').trigger('click');
             });
             makeNeighborhoodList();
           }
         });

         var youarehere = {
           'url' : '/img/you_are_here.png'
         };

         var pin = {
           'url' : '/img/pin.png',
           'anchor' : new google.maps.Point(10, 56)
         };

         window.clearmap  = function() {
           $('#map-view').gmap('clear', 'markers');
         };

         window.addtomap = function() {
           $.each(data.parks, function(i, e) {
             if(e.latitude && e.longitude) {
               var x = false;
               $.each(attributes, function(o, a) {
                 if(!window.filters || (window.filters[a.attribute] && e[a.attribute] && e[a.attribute] !== 'none' && e[a.attribute] !== 'None'))
                   x = true;
               });
               if(x) {
                 var point = new google.maps.LatLng(e.latitude, e.longitude);
                 $('#map-view').gmap('addMarker', {'position': point, 
                                     'bounds': false, 
                                     'icon' : pin})
                                     .click(function() {
                                       showPark(e['park-id']);
                                     });
               }
             }
           });
         };

         $(function(){
           $('#map-view').gmap({'center' : '-90,30',
                               'navigationControl' : false,
                               'mapTypeControl' : false,
                               'zoomControl' : false,
                               'streetViewControl': false,
                               'zoom' : 12
           }).bind('init', function(evt, map) {
             $('#map-view').gmap('getCurrentPosition', function(position, status) {
               if ( status === 'OK' ) {
                 var clientPosition = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
                 $('#map-view').gmap('addMarker', {'position': clientPosition, 
                                     'bounds': false, 
                                     'icon' : youarehere});
                                     $('#map-view').gmap('get', 'map').setOptions({'center' : clientPosition,
                                                                                  'zoom' : 14});
                                                                                  window.done = true;
                                                                                  if(data)

                                                                                    addtomap();
               }
             });
           });
         });

         function showPark(pid) {
           var park;
           $.each(window.data.parks, function(i, e) {
             if(e['park-id'] === pid) {
               park = e;
             }
           });

           if(park) {
             var template = $($('.park-view-template').html());
             var at = $('.attribute-template').html();
             template.find('img.big-img').attr('src', park['main-image']);
             template.find('.name').text(park.name);
             template.find('.address').text(park.address);
             template.find('button.close').click(function() {
               template.remove();
             });
             var t = template.find('div.attributes');
             $.each(window.attributes, function(i, e) {
               if(park[e.attribute] && park[e.attribute] !== 'none' && park[e.attribute] !== 'None') {
                 var g = $(at);
                 g.find('img.icon').attr('src', e.icon);
                 g.find('.txt').text(e.title);
                 t.append(g);
               }
             });

             $('body').append(template);
           } else {
             console.log('could not find park with id: ' + pid);
           }
         }
         var m = $('#map-view');
         window.loadLatLong = function loadLatLong(i) {
           if(i < window.data.parks.length) {
             var e = window.data.parks[i];
             if(e.address) {
               console.log('processing ' + e.name);
               console.log('searching for ' + e.address);
               m.gmap('search', {'address' : e.address}, function(results, status) {
                 if(status === 'OK') {
                   console.log('found it!');
                   var r = results[0];
                   var lat = '' + r.geometry.location.lat();
                   var lon = '' + r.geometry.location.lng();
                   console.log(lat + ',' + lon);
                   e.latitude = lat;
                   e.longitude = lon;
                   console.log('posting');
                   $.ajax('/location/' + e['park-id'],
                          {type : 'POST',
                            data : e,
                            success: function() {
                              loadLatLong(i+1);
                            },
                            error: function() {
                              console.log('error on ' + i);
                            }});
                 } else {
                   console.log("didn't get address back for " + i);
                 }
               });
             } else {
               console.log('could not find address for ' + e.name);
             }
           }
         }

         window.filters = null;

         var first = true;
         $('.selector-list div').click(function() {
           var el = $(this);
           var a = el.attr('data-attr');
           var c = 'selected';
           if(a === 'all') {
             first = true;
             window.filters = null;
             $('.selector-list div').addClass(c);
             clearmap();
             addtomap();
             return;
           } else if(first) {
             $('.selector-list div').removeClass(c);
             el.addClass(c);
             filters = {};
             filters[a] = true;
           } else {
             if(el.hasClass(c)) {
               el.removeClass(c);
               delete filters[a];
             } else {
               el.addClass(c);
               filters[a] = true;
             }
           }
           clearmap();
           addtomap();
           first = false;
         });

         function makeNeighborhoodList() {
           var t = $('script.neighborhood-template').html();
           var nl = $('div.neigh-list').empty();
           $.each(neighborhoods, function(i, n) {
             var x = $(t);

             x.find('.name').text(n);
             x.find('a.accordion-toggle').attr('href', "#collapse" + i);
             x.find('div.accordionBody').attr('id', 'collapse' + i);
             var ps = x.find('ul.parks');
             var tp = $('.n-park-template').html();
             $.each(window.data.parks, function(i, p) {
               if(p.neighborhood === n) {
                 var v = false;
                 $.each(attributes, function(o, a) {
                   if(!window.filters || (window.filters[a.attribute] && p[a.attribute] && p[a.attribute] !== 'none' && p[a.attribute] !== 'None'))
                     v = true;
                 });
                 if(v) {
                   var x = $(tp);
                   x.find('a.name').text(p.name)
                   .attr('data-park', p['park-id']);
                   ps.append(x);
                 }
               }
             });

             nl.append(x.html());

           });
           // bind search inputs for each neighborhood
         //
         $('input.neigh-filter').keydown(function(){
           var input = this;
           window.setTimeout(function(){
            var val = $(input).val().toLowerCase();
            $(input).closest('.accordion-inner').find('.parks').children('.park').each(function(){
              if(!$(this).children('a').text().toLowerCase().match(val)){
                $(this).addClass('hidden');
              }
              else{
                $(this).removeClass('hidden');
              }
            });
           }, 5);
         });
         $('form.form-search button.btn').click(function(e){
           e.preventDefault();
           $(this).siblings('input.neigh-filter').val('');
           $(this).siblings('input.neigh-filter').trigger('keydown');
           return false;
         });

         }

         $('.neigh-list a.name', 'body').live('click', function() {
           showPark($(this).attr('data-park'));
           return false;
         });

         
}(window));
