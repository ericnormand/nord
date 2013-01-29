(function(window){

    window.filters = null;

    window.attributes = [
        {'attribute' : "running",
         'fn'        : function(park) {
             return park['track-field'] || 
                 park['running'];
         },
         'title'     : "Running",
         'icon'      : "/img/icon/running.png"},
        {'attribute' : "playground",
         'title'     :  "Playground",
         'fn'        : function(park) {
             return park.playground;
         },
         'icon'      : "/img/icon/playground.png"},
        {'attribute' : "dog-park",
         'title'     : "Dog park",
         'fn'        : function(park) {
             return park['dog-park'];
         },
         'icon'      : "/img/icon/dogpark.png"},
        {'attribute' : "tennis",
         'title'     : "Tennis",
         'fn'        : function(park) {
             return park['tennis-courts'] && park['tennis-courts'] > 0;
         },
         'icon'      : "/img/icon/tennis.png"},
        {'attribute' : "basketball",
         'title'     : "Basketball",
         'fn'        : function(park) {
             return (park['indoor-basketball-courts'] && park['indoor-basketball-courts'] > 0) ||
                 (park['outdoor-basketball-courts'] && park['outdoor-basketball-courts'] > 0) ||
                 (park['outdoor-covered-basketball-courts'] && park['outdoor-covered-basketball-courts'] > 0);
         },
         'icon'      : "/img/icon/basketball.png"},
        {'attribute' : "bicycle",
         'title'     : "Bicycle",
         'fn'        : function(park) {
             return park['bicycle-path'];
         },
         'icon'      : "/img/icon/bicycle.png"},
        {'attribute' : "picnic-tables",
         'title'     : "Picnic tables",
         'fn'        : function(park) {
             return park['uncovered-picnic-tables'] ||
                 park['covered-picnic-tables'];
         },
         'icon'      : "/img/icon/picnic.png"},
        {'attribute' : "all-purpose-field",
         'title'     : "Sports field",
         'fn'        : function(park) {
             return park['all-purpose-field'];
         },
         'icon'      : "/img/icon/sportsfield.png"},
        {'attribute' : "historic",
         'title'     : "Sightseeing",
         'fn'        : function(park) {
             return park['sightseeing'];
         },
         'icon'      : "/img/icon/monument.png"},
        {'attribute' : "swimming-pool",
         'title'     : "Swimming",
         'fn'        : function(park) {
             return park['indoor-pool'] ||
                 park['outdoor-pool'];
         },
         'icon'      : "/img/icon/swimming.png"},
        {'attribute' : "super-saturday",
         'title'     : '<a href="http://www.neworleanssuperbowl.com/events/supersaturday.php" target="_blank">Super Saturday of Service</a>',
         'fn'        : function(park) {
             return park['super-saturday'];
         },
         'icon'      : "/img/icon/supersaturday.png"}
    ];

    window.subattributes = [
        function(park) {
            if(park['off-street-parking'])
                return "Off-Street Parking";
        },
        function(park) {
            if(park['indoor-pool'])
                return "Indoor Pool";
        },
        function(park) {
            if(park['outdoor-pool'])
                return "Outdoor Pool (open summer only)";
        },
        function(park) {
            if(park['showers-pool'])
                return "Pool Showers";
        },
        function(park) {
            if(park['restroom-building'])
                return "Restroom Building";
        },
        function(park) {
            if(park['port-o-lets'])
                return "Port-o-Lets";
        },
        function(park) {
            if(park['water-fountain'])
                return "Water Fountain";
        },
        function(park) {
            if(park['recreation-center'])
                return "Recreation Center";
        },
        function(park) {
            if(park['showers-recreation-center'])
                return "Recreation Center Showers";
        },
        function(park) {
            if(park['club-house'])
                return "Club House";
        },
        function(park) {
            if(park['high-mast-lighting'])
                return "High Mast Lighting";
        },
        function(park) {
            if(park['batting-cage'])
                return "Batting Cage";
        },
        function(park) {
            if(park['dug-outs'])
                return "Dug Outs";
        },
        function(park) {
            if(park['playground'])
                return "Playground Equipment";
        },
        function(park) {
            if(park['fencing'])
                return "Fencing";
        },
        function(park) {
            if(park['bleachers'])
                return 'Bleachers';
        },
        function(park) {
            if(park['facility-lights'])
                return "Facility Lights";
        },
        function(park) {
            if(park['stadium'])
                return "Stadium";
        },
        function(park) {
            if(park['track-field'])
                return "Track Field";
        },
        function(park) {
            if(park['running'])
                return "Nice for Running";
        },
        function(park) {
            if(park['all-purpose-field'])
                return "All Purpose Field";
        },
        function(park) {
            if(park['fitness-park'])
                return "Outdoor Fitness Park";
        },
        function(park) {
            if(park['tennis-courts'])
                return "Tennis Courts: " + park['tennis-courts'];
        },
        function(park) {
            if(park['tennis-building'])
                return "Tennis Building";
        },
        function(park) {
            if(park['indoor-basketball-courts'])
                return "Indoor Basketball Courts: " + park['indoor-basketball-courts'];
        },
        function(park) {
            if(park['outdoor-basketball-courts'])
                return "Outdoor Basketball Courts: " + park['outdoor-basketball-courts'];
        },
        function(park) {
            if(park['outdoor-covered-basketball-courts'])
                return "Covered Outdoor Basketball Courts: " + park['outdoor-covered-basketball-courts'];
        },
        function(park) {
            if(park['booster-club'])
                return "Booster Club";
        }

    ];

    window.addtomap = function() {
        $.each(window.parks, function(i, e) {
            if(e.latitude && e.longitude) {
                var x = false;
                if(filters) {
                    if(!e['has-sub-park'])
                        $.each(attributes, function(o, a) {
                            if(filters[a.attribute] && a.fn(e)) {
                                x = true;
                                return false;
                            }
                        });
                } else {
                    if(!e['is-sub-park'])
                        x = true;
                }
                if(x) {
                    var point = new google.maps.LatLng(e.latitude, e.longitude);
                    window.map.gmap('addMarker', {'position': point, 
                                                  'bounds': false, 
                                                  'icon' : pin,
                                                  'zIndex' : 100})
                        .click(function(event) {
                            $.bbq.pushState({
                                'park-id' : e['park-id']
                            });
                            return false;
                        });
                }
            }
        });
    };

    window.map = $('#map-view');


    if(window.map.length > 0) {
        mixpanel.track('Open app');

        var parkid = $.bbq.getState('park-id');
        
        if(parkid) {
            window.location = "#park-id=" + parkid;
            showPark(parkid);
        } else {
            window.location = "#";
        }
        
        var pin = {
            'url' : '/img/pin.png',
            'anchor' : new google.maps.Point(5, 29)
        };

        window.map.gmap({'center' : '29.951066,-90.071532',
                         //'navigationControl' : false,
                         'mapTypeControl' : false,
                         //'zoomControl' : !Modernizr.touch,
                         'streetViewControl': false,
                         'styles' : [
                             {
                                 "featureType": "poi",
                                 "elementType": "labels",
                                 "stylers": [
                                     { "visibility": "off" }
                                 ]
                             }
                         ],
                         'zoom' : 14
                        })
            .bind('init', function(evt, map) {
                if(Modernizr.geolocation) {
                    window.map.gmap('getCurrentPosition', function(position, status) {
                        if ( status === 'OK' && 
                             position.coords.latitude <= 30.1 && position.coords.latitude >= 29.8 &&
                             position.coords.longitude <= -89.8 && position.coords.longitude >= -90.2
                           ) {
                            var clientPosition = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
                            window.map.gmap('addMarker', {'position': clientPosition, 
                                                          'bounds': false, 
                                                          'icon' : youarehere});
                            window.map.gmap('get', 'map').setOptions({'center' : clientPosition,
                                                                      'zoom' : 14});
                        }
                    });
                }
                addtomap();
        });

        function showSelectorList() {
            mixpanel.track('Open filter list');
            $('div.wrapper').addClass('selector-list-active');
        }

        function hideSelectorList() {
            mixpanel.track('Close filter list');
            $('div.wrapper').removeClass('selector-list-active');
        }

        function showNeighborhoodList() {
            mixpanel.track('Open neighborhood list');
            makeNeighborhoodList();
            $('div.wrapper').addClass('neigh-list-active');
        }

        function hideNeighborhoodList() {
            mixpanel.track('Close neighborhood list');
            $('div.wrapper').removeClass('neigh-list-active');
        }

        $(window).bind('hashchange', function(e) {
            if($.bbq.getState('selector-list')) {
                showSelectorList();
            } else {
                hideSelectorList();
            }
            if($.bbq.getState('neighborhood-list')) {
                showNeighborhoodList();
            } else {
                hideNeighborhoodList();
            }
            var parkid = $.bbq.getState('park-id');
            if(parkid) {
                showPark(parkid);
            } else {
                hidePark();
            }
        });

        if(Modernizr.touch) {
            window.selscroller = new iScroll('selector-list');
            window.neiscroller = new iScroll('nl');
        }

        makeNeighborhoodList();        
    }
    
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
        if($.bbq.getState('selector-list'))
            $.bbq.removeState('selector-list');
        else
            $.bbq.pushState({'selector-list' : true});
        return false;
    });

    window.map.click(function() {
        $.bbq.removeState('selector-list', 'neighborhood-list');
    });

    $('img.list').click(function() {
        if($.bbq.getState('neighborhood-list'))
            $.bbq.removeState('neighborhood-list');
        else
            $.bbq.pushState({'neighborhood-list' : true});
        return false;
    });

    $('img.map').click(function() {
        if($.bbq.getState('neighborhood-list'))
            $.bbq.removeState('neighborhood-list');
        else
            $.bbq.pushState({'neighborhood-list' : true});
        return false;
    });

    var youarehere = {
        'url' : '/img/you_are_here.png'
    };


    window.clearmap  = function() {
        window.map.gmap('clear', 'markers');
    };


    function hidePark() {
        mixpanel.track('Hide park');
        $('div.park-view').remove();
    }

    
    function showPark(pid) {
        var park;
        $.each(window.parks, function(i, e) {
            if(e['park-id'] === pid) {
                park = e;
                return false;
            }
        });

        if(park) {
            mixpanel.track('Show park',
                           {'Park ID' : pid});
            var template = $($('.park-view-template').html());
            var at = $('.attribute-template').html();
            template.find('img.big-img').attr('src', park['image-url'] || 'https://dl.dropbox.com/s/iy8a1j8dqn79n9e/City%20Park%207.jpg?dl=1');
            template.find('.body a').attr('href', 'https://maps.google.com/?q=' + park.latitude + ',' + park.longitude);
            template.find('.name').text(park.name);
            template.find('.address').html(park.address + "<br /> New Orleans, LA");
            var twitter = park['twitter-search'] || '@NolaParksApp';
            template.find(".twitter").tweet({
                avatar_size: 32,
                count: 4,
                query: twitter,
                loading_text: "searching twitter...",
                refresh_interval: 60
            }).bind("loaded", function () {
                $(this).find("a").attr("target","_blank");
                setTimeout(function() {
                    window.scroller && window.scroller.refresh();
                }, 100);
            });
            template.find('button.close').click(function() {
                $.bbq.removeState('park-id');
            });
            if(park['hours-of-operation'])
                template.find('div.hoursofoperation').text(park['hours-of-operation']);
            else
                template.find('h3.hoursofoperation').remove();
            if(park.url)
                template.find('a.website').attr('href', park.url).text('Visit Website');
            var t = template.find('div.attributes');
            var c = 0;
            $.each(window.attributes, function(i, e) {
                if(e.fn(park)) {
                    var g = $(at);
                    g.find('img.icon').attr('src', e.icon);
                    g.find('.txt').html(e.title);
                    t.append(g);
                    c++;
                }
            });
            if(c === 0)
                template.find('h3.attributes').remove();
            var st = template.find('ul.subattributes');
            c = 0;
            $.each(window.subattributes, function(i, sa) {
                var s = sa(park);
                if(s) {
                    st.append($('<li />').html(s));
                    c++;
                }
            });
            if(c === 0)
                template.find('h3.subattributes').remove();

            $('body').append(template);
            window.scroller && window.scroller.destroy();
            window.scroller = null;
                
            if(Modernizr.touch)
                setTimeout(function() {
                    window.scroller = new iScroll('park-view-wrapper');
                }, 100);
        } else {
            mixpanel.track('Error',
                           {'Park ID' : pid,
                            'Message' : 'could not find park'});
        }
    }

    var m = $('#map-view');
    window.loadLatLong = function loadLatLong(i) {
        if(i < window.parks.length) {
            var e = window.parks[i];
            console.log(e.latitude);
            console.log(e.longitude);
            if(e.address && !(e.latitude && e.latitude <= 30.1 && e.latitude >= 29.8) && !(e.longitude && e.longitude <= -89.8 && e.longitude >= -90.2) ) {
                console.log('processing ' + e.name);
                var aa = e.address + ", New Orleans, LA";
                console.log('searching for ' + aa);
                m.gmap('search', {'address' : aa}, function(results, status) {
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
                                    loadLatLong(i+1);
                                }});
                    } else {
                        console.log("didn't get address back for " + i);
                        loadLatLong(i+1);
                    }
                });
            } else if(e.address) {
                console.log('have address and lat long for ' + e.name);
                loadLatLong(i+1);

            } else {
                console.log('could not find address for ' + e.name);
                loadLatLong(i+1);
            }
        }
    }

    var first = true;
    $('.selector-list li a').click(function() {
        var el = $(this);
        var a = el.attr('data-attr');
        var c = 'active';
        if(a === 'all') {
            mixpanel.track('Select all');
            first = true;
            window.filters = null;
            $('.selector-list li').addClass(c);
            clearmap();
            addtomap();
        } else if(first) {
            mixpanel.track('Select amenity',
                           {'Amenity' : a,
                            'First' : true});
            $('.selector-list li').removeClass(c);
            el.parent().addClass(c);
            filters = {};
            filters[a] = true;
            first = false;
        } else {
            if(el.parent().hasClass(c)) {
                mixpanel.track('Unselect amenity',
                               {'Amenity' : a});
                el.parent().removeClass(c);
                delete filters[a];
                first = false;
            } else {
                mixpanel.track('Select amenity',
                               {'Amenity' : a,
                                'First' : false});
                el.parent().addClass(c);
                filters[a] = true;
                first = false;
            }
        }
        clearmap();
        addtomap();
        makeNeighborhoodList();
        return false;
    });

    function makeNeighborhoodList() {
        var t = $('script.neighborhood-template').html();
        var nl = $('div.neigh-list .accordion').empty();
        $.each(neighborhoods, function(i, n) {
            var x = $(t);

            x.find('.name').text(n);
            x.find('a.accordion-toggle').attr('href', "#collapse" + i);
            x.find('div.accordionBody').attr('id', 'collapse' + i);
            var ps = x.find('ul.parks');
            var tp = $('.n-park-template').html();
            var c = 0;
            $.each(window.parks, function(i, p) {
                if(p.neighborhood === n) {
                    var v = false;
                    if(filters) {
                        if(!p['has-sub-park'])
                            $.each(attributes, function(o, a) {
                                if(filters[a.attribute] && a.fn(p)) {
                                    v = true;
                                    return false;
                                }
                            });
                    } else {
                        if(!p['is-sub-park'])
                            v = true;
                    }
                    if(v) {
                        var x = $(tp);
                        x.find('a.name').text(p.name)
                            .attr('data-park', p['park-id']);
                        ps.append(x);
                        c += 1;
                    }
                }
            });

            x.find('.number').text(' (' + c + ')');

            nl.append(x.html());

        });

        if(window.neiscroller)
            setTimeout(function() {
                window.neiscroller.refresh();
            }, 500);
    }

    $('.neigh-list a.name', 'body').live('click', function() {
        var pid = $(this).attr('data-park');
        $.bbq.pushState({'park-id' : pid});
        return false;
    });

    $('.neigh-list .accordion-heading a').live('click', function() {
        var el = $(this);
        var name = el.find('.name').text();
        var item = $(el.attr('href'));
        if(!item.hasClass('in')) {
            mixpanel.track('Collapse neighborhood',
                           {'Neighborhood' : name});
        } else {
            mixpanel.track('Expand neighborhood',
                           {'Neighborhood' : name});
        }
        if(window.neiscroller)
            setTimeout(function() {
                window.neiscroller.refresh();
            }, 500);
    });

}(window));
