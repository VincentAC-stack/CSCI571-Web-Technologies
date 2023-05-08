// 'use strict';
/**
 * Geocoding API: AIzaSyC_ySEVsAJyF-bYUGPeHtNKZNDjLRaxR-Y
 * Ticketmaster API: 027XqtGGJmSaJSTQuO1abywiqPGKCnkO
 * IP API: 5f3978d3111782
 * Spotify Client ID: 110f5e3b7a4441e7992ec44f824d0c10
 * Spotify Client Secret: ec354ee767b641dc8d528c014583ef49
 */

// [START gae_node_request_example]
const cors = require('cors');
const express = require('express');
const path = require('path');
const axios = require('axios');
var geohash = require('ngeohash');
var SpotifyWebApi = require('spotify-web-api-node');

const app = express();
app.use(cors());


const Geocoding_API = 'AIzaSyC_ySEVsAJyF-bYUGPeHtNKZNDjLRaxR-Y';
const Ticket_API = '027XqtGGJmSaJSTQuO1abywiqPGKCnkO';
const Geocoding_base_url = 'https://maps.googleapis.com/maps/api/geocode/json?';
const Ticket_base_url = 'https://app.ticketmaster.com/discovery/v2/events.json?';
const Venue_base_url = 'https://app.ticketmaster.com/discovery/v2/venues?';
const cat_dic = {
  "Music": "KZFzniwnSyZfZ7v7nJ",
  "Sports": "KZFzniwnSyZfZ7v7nE",
  "ArtsTheatre": "KZFzniwnSyZfZ7v7na",
  "Film": "KZFzniwnSyZfZ7v7nn",
  "Miscellaneous": "KZFzniwnSyZfZ7v7n1",
  "Default": ["KZFzniwnSyZfZ7v7nJ", "KZFzniwnSyZfZ7v7nE", "KZFzniwnSyZfZ7v7na", "KZFzniwnSyZfZ7v7nn", "KZFzniwnSyZfZ7v7n1"]
}

app.get('/autocomplete', (req, res) => {
  let AutoData = [];
  axios.get('https://app.ticketmaster.com/discovery/v2/suggest?apikey=' + Ticket_API + '&keyword=' + req.query['keyword'])
    .then(response => {
      for (let i = 0; i < response.data['_embedded']['attractions'].length; i++) {
        AutoData.push(response.data['_embedded']['attractions'][i]['name']);
      }
      res.send(JSON.stringify(AutoData));
      console.log("Auto-complete send finished!")
    })
    .catch(error => {
      console.log("Error for autocomplete", error);
    });
});

app.get('/search', (req, res) => {
  let kwData = req.query['input_kw'];
  let distData = req.query['input_dist'];
  let catData = req.query['input_cat'];
  let locData = req.query['input_location'];

  let Event_Data = [];
  let Event_detail = {};

  // console.log(locData);
  let GeoQueryURL = Geocoding_base_url + 'address=' + locData + '&key=' + Geocoding_API;
  axios.get(GeoQueryURL)
    .then(GeoRes => {
      // console.log(GeoRes);
      if (GeoRes.data['status'] == 'OK') {
        geom = GeoRes.data.results[0].geometry;
        lat = geom['location']['lat'];
        lon = geom['location']['lng'];
        Geo_Hash = geohash.encode(lat, lon, 7);

        let TicketQuery = Ticket_base_url + 'apikey=' + Ticket_API + '&keyword=' + kwData + '&segmentId=' +
          cat_dic[catData] + '&radius=' + distData + '&unit=miles&geoPoint=' + Geo_Hash;
        // console.log(TicketQuery);

        axios.get(TicketQuery)
          .then(TicketData => {
            // console.log(TicketData.data);
            if (TicketData.data['page']['totalElements'] == 0) {
              res.send(JSON.stringify("No Records found"));
            } else {
              let Ticket_Data = TicketData.data["_embedded"]['events'];
              if (Ticket_Data.length < 20) {
                let rowIndex = 0;
                for (let i = 0; i < Ticket_Data.length; i++) {
                  if (Ticket_Data[i]['type'] == 'event') {
                    if ('localTime' in Ticket_Data[i]['dates']['start']) {
                      Event_detail['Date'] = Ticket_Data[i]['dates']['start']['localDate'] + ' ' + Ticket_Data[i]['dates']['start']['localTime'];
                    } else {
                      Event_detail['Date'] = Ticket_Data[i]['dates']['start']['localDate'];
                    }
                    Event_detail['Icon'] = Ticket_Data[i]['images'][0]['url'];
                    Event_detail['Event'] = Ticket_Data[i]['name'];
                    Event_detail['Genre'] = Ticket_Data[i]['classifications'][0]['segment']['name'];
                    Event_detail['Venue'] = Ticket_Data[i]['_embedded']['venues'][0]['name'];
                  }
                  Event_Data.push({
                    'index': rowIndex++,
                    'Date': Event_detail['Date'],
                    'Icon': Event_detail['Icon'],
                    'Event': Event_detail['Event'],
                    'Genre': Event_detail['Genre'],
                    'Venue': Event_detail['Venue']
                  });
                }
              } else {
                let rowIndex = 0;
                for (let i = 0; i < 20; i++) {
                  if (Ticket_Data[i]['type'] == 'event') {
                    if ('localTime' in Ticket_Data[i]['dates']['start']) {
                      Event_detail['Date'] = Ticket_Data[i]['dates']['start']['localDate'] + ' ' + Ticket_Data[i]['dates']['start']['localTime'];
                    } else {
                      Event_detail['Date'] = Ticket_Data[i]['dates']['start']['localDate'];
                    }
                    Event_detail['Icon'] = Ticket_Data[i]['images'][0]['url'];
                    Event_detail['Event'] = Ticket_Data[i]['name'];
                    Event_detail['Genre'] = Ticket_Data[i]['classifications'][0]['segment']['name'];
                    Event_detail['Venue'] = Ticket_Data[i]['_embedded']['venues'][0]['name'];
                  }
                  Event_Data.push({
                    'index': rowIndex++,
                    'Date': Event_detail['Date'],
                    'Icon': Event_detail['Icon'],
                    'Event': Event_detail['Event'],
                    'Genre': Event_detail['Genre'],
                    'Venue': Event_detail['Venue']
                  });
                }
              }
              Event_Data = Event_Data.sort(function (a, b) {
                return a.Date.localeCompare(b.Date);
              });
              res.send(JSON.stringify(Event_Data));
            }
            console.log("Search data send finished!")
          })
          .catch(error => {
            console.log("Error for TicketMaster", error);
          })
      } else {
        res.send(JSON.stringify("No Records found"));
        console.log("Search data send finished!")
      }
    })
    .catch(error => {
      console.log("Error for Geocoding", error);
    })
});

app.get('/event', (req, res) => {
  let kwData = req.query['input_kw'];
  let distData = req.query['input_dist'];
  let catData = req.query['input_cat'];
  let locData = req.query['input_location'];
  let row = req.query['Row'];

  let Event_Data = [];
  let Event_detail = {};

  // console.log(locData);
  let GeoQueryURL = Geocoding_base_url + 'address=' + locData + '&key=' + Geocoding_API;
  axios.get(GeoQueryURL)
    .then(GeoRes => {
      if (GeoRes.data['status'] == 'OK') {
        geom = GeoRes.data.results[0].geometry;
        lat = geom['location']['lat'];
        lon = geom['location']['lng'];
        Geo_Hash = geohash.encode(lat, lon, 7);

        let TicketQuery = Ticket_base_url + 'apikey=' + Ticket_API + '&keyword=' + kwData + '&segmentId=' +
          cat_dic[catData] + '&radius=' + distData + '&unit=miles&geoPoint=' + Geo_Hash;
        // console.log(TicketQuery);

        axios.get(TicketQuery)
          .then(TicketRes => {
            let Ticket_Data = TicketRes.data["_embedded"]['events'];
            Ticket_event_id = Ticket_Data[row]['id'];
            let EventQuery = Ticket_base_url + 'apikey=' + Ticket_API + '&id=' + Ticket_event_id;
            axios.get(EventQuery)
              .then(Event_result_response => {
                if (Event_result_response.data['page']['totalElements'] == 0) {
                  res.send(JSON.stringify("No Records found"));
                } else {
                  let Event_result = Event_result_response.data["_embedded"]['events'];
                  if (Event_result[0]['dates']['status']['code'] == "onsale")
                    Event_result[0]['dates']['status']['code'] = "On Sale";
                  if (Event_result[0]['dates']['status']['code'] == "offsale")
                    Event_result[0]['dates']['status']['code'] = "Off Sale";
                  if (Event_result[0]['dates']['status']['code'] == "cancelled")
                    Event_result[0]['dates']['status']['code'] = "Cancelled";
                  if (Event_result[0]['dates']['status']['code'] == "postponed")
                    Event_result[0]['dates']['status']['code'] = "Postponed";
                  if (Event_result[0]['dates']['status']['code'] == "rescheduled")
                    Event_result[0]['dates']['status']['code'] = "Rescheduled";

                  if ('name' in Ticket_Data[row]) {
                    Event_detail["Event Name"] = Ticket_Data[row]['name'];
                  } else {
                    Event_detail["Event Name"] = "Undefined";
                  }

                  if ('localDate' in Event_result[0]['dates']['start']) {
                    Event_detail['localDate'] = Event_result[0]['dates']['start']['localDate'];
                  } else {
                    Event_detail['localDate'] = "Undefined";
                  }

                  if ('localTime' in Event_result[0]['dates']['start']) {
                    Event_detail["localTime"] = Event_result[0]['dates']['start']['localTime'];
                  } else {
                    Event_detail["localTime"] = "Undefined";
                  }

                  let Artist_Team = [];

                  if ('attractions' in Event_result[0]['_embedded']) {
                    if ('name' in Event_result[0]['_embedded']['attractions'][0]) {
                      for (let item = 0; item < Event_result[0]['_embedded']['attractions'].length; item++) {
                        Artist_Team.push(Event_result[0]['_embedded']['attractions'][item]['name']);
                        Event_detail["Artist/Team"] = Artist_Team;
                      }
                    }

                  } else {
                    Event_detail["Artist/Team"] = ["Undefined"];
                  }

                  if ('venues' in Event_result[0]['_embedded']) {
                    if ('name' in Event_result[0]['_embedded']['venues'][0]) {
                      Event_detail["Venue"] = Event_result[0]['_embedded']['venues'][0]['name'];
                    }
                  } else {
                    Event_detail["Venue"] = "Undefined";
                  }

                  if ('classifications' in Event_result[0]) {
                    if ('segment' in Event_result[0]['classifications'][0]) {
                      Event_detail["Genres_segment"] = Event_result[0]['classifications'][0]['segment']['name'];
                    } else {
                      Event_detail["Genres_segment"] = "Undefined";
                    }
                    if ('genre' in Event_result[0]['classifications'][0]) {
                      Event_detail["Genres_genre"] = Event_result[0]['classifications'][0]['genre']['name'];
                    } else {
                      Event_detail["Genres_genre"] = "Undefined";
                    }
                    if ('subGenre' in Event_result[0]['classifications'][0]) {
                      Event_detail["Genres_subGenre"] = Event_result[0]['classifications'][0]['subGenre']['name'];
                    } else {
                      Event_detail["Genres_subGenre"] = "Undefined";
                    }
                    if ('type' in Event_result[0]['classifications'][0]) {
                      Event_detail["Genres_type"] = Event_result[0]['classifications'][0]['type']['name'];
                    } else {
                      Event_detail["Genres_type"] = "Undefined";
                    }
                    if ('subType' in Event_result[0]['classifications'][0]) {
                      Event_detail["Genres_subType"] = Event_result[0]['classifications'][0]['subType']['name'];
                    } else {
                      Event_detail["Genres_subType"] = "Undefined";
                    }
                  } else {
                    Event_detail["Genres_segment"] = "Undefined";
                    Event_detail["Genres_genre"] = "Undefined";
                    Event_detail["Genres_subGenre"] = "Undefined";
                    Event_detail["Genres_type"] = "Undefined";
                    Event_detail["Genres_subType"] = "Undefined";
                  }

                  if ('priceRanges' in Event_result[0]) {
                    Event_detail["Price Ranges"] = String(Event_result[0]['priceRanges'][0]['min']) + ' ' + String(Event_result[0]['priceRanges'][0]['max']);
                  } else {
                    Event_detail["Price Ranges"] = "Undefined";
                  }
                  if ('dates' in Event_result[0]) {
                    if ('status' in Event_result[0]['dates']) {
                      Event_detail["Ticket Status"] = Event_result[0]['dates']['status']['code'];
                    }
                  } else {
                    Event_detail["Ticket Status"] = "Undefined";
                  }

                  if ('url' in Event_result[0]) {
                    Event_detail["Ticket URL"] = Event_result[0]['url'];
                  } else {
                    Event_detail["Ticket URL"] = "Undefined";
                  }

                  if ('seatmap' in Event_result[0]) {
                    Event_detail["SeatMap_URL"] = Event_result[0]['seatmap']['staticUrl'];
                  } else {
                    Event_detail["SeatMap_URL"] = "Undefined";
                  }

                  Event_Data.push(Event_detail);
                  res.send(JSON.stringify(Event_Data));
                }
              }).catch(error => {
                console.log("Error for event", error);
              })

            console.log("Event data send finished!")
          })
          .catch(error => {
            console.log("Error for TicketMaster", error);
          })
      }
    })
    .catch(error => {
      console.log("Error for Geocoding", error);
    })
});

app.get('/spotify', (req, res) => {
  let MusicArtists = {};
  let AlbumList = [];

  var spotifyApi = new SpotifyWebApi({
    clientId: '110f5e3b7a4441e7992ec44f824d0c10',
    clientSecret: 'ec354ee767b641dc8d528c014583ef49',
  });

  spotifyApi.clientCredentialsGrant().then(
    function (data) {
      // console.log('The access token is ' + data.body['access_token']);
      // Save the access token so that it's used in future calls
      spotifyApi.setAccessToken(data.body['access_token']);

      spotifyApi.searchArtists(req.query['Artist']).then(
        function (data) {
          // console.log("Artist:", data.body);

          if (data.body['artists']['total'] != 0) {
            MusicArtists['Name'] = data.body['artists']['items'][0]['name'];
            MusicArtists['Name_Img'] = data.body['artists']['items'][0]['images'][0]['url'];
            MusicArtists['Popularity'] = data.body['artists']['items'][0]['popularity'];
            MusicArtists['Followers'] = data.body['artists']['items'][0]['followers']['total'].toLocaleString('en', { useGrouping: true });
            MusicArtists['Spotify_Link'] = data.body['artists']['items'][0]['external_urls']['spotify'];
            MusicArtists['ID'] = data.body['artists']['items'][0]['id'];

            spotifyApi.getArtistAlbums(MusicArtists['ID'], { limit: 3 }).then(
              function (data) {
                for (let i = 0; i < data.body['items'].length; i++) {
                  if ('images' in data.body['items'][i]) {
                    AlbumList.push(data.body['items'][i]['images'][0]['url']);
                  }
                }

                MusicArtists['Albums'] = AlbumList;

                res.send(JSON.stringify(MusicArtists));
                console.log("Artists & Albums send finished!");
              },
              function (err) {
                console.log('Artist Albums Something went wrong!', err);
              }
            );
          }
        },
        function (err) {
          console.log('Artists Something went wrong!', err);
        }
      );

    },
    function (err) {
      console.log('Something went wrong when retrieving an access token', err);
    }
  );
});

app.get('/venue', (req, res) => {
  let kwData = req.query['input_kw'];
  let distData = req.query['input_dist'];
  let catData = req.query['input_cat'];
  let locData = req.query['input_location'];
  let row = req.query['Row'];

  let Venue_detail = {};

  // console.log(locData);
  let GeoQueryURL = Geocoding_base_url + 'address=' + locData + '&key=' + Geocoding_API;
  axios.get(GeoQueryURL)
    .then(GeoRes => {
      if (GeoRes.data['status'] == 'OK') {
        geom = GeoRes.data.results[0].geometry;
        lat = geom['location']['lat'];
        lon = geom['location']['lng'];
        Geo_Hash = geohash.encode(lat, lon, 7);

        let TicketQuery = Ticket_base_url + 'apikey=' + Ticket_API + '&keyword=' + kwData + '&segmentId=' +
          cat_dic[catData] + '&radius=' + distData + '&unit=miles&geoPoint=' + Geo_Hash;
        // console.log(TicketQuery);

        axios.get(TicketQuery)
          .then(TicketRes => {
            let Ticket_Data = TicketRes.data["_embedded"]['events'];
            let Ticket_venue_name = Ticket_Data[row]["_embedded"]['venues'][0]['name'];
            let VenueQuery = Venue_base_url + 'apikey=' + Ticket_API + '&keyword=' + Ticket_venue_name;
            // console.log(VenueQuery);

            axios.get(VenueQuery)
              .then(Venue_response => {
                if (Venue_response.data['page']['totalElements'] == 0) {
                  res.send(JSON.stringify("No Records found"));
                } else {
                  // console.log(Venue_response.data);

                  let Venue_result = Venue_response.data["_embedded"]['venues'];
                  if ('name' in Venue_result[0]) {
                    Venue_detail['Venue_Name'] = Venue_result[0]["name"];
                  } else {
                    Venue_detail['Venue_Name'] = "Undefined";
                  }

                  if ('address' in Venue_result[0]) {
                    Venue_detail['Venue_Address'] = Venue_result[0]['address']['line1'];
                  } else {
                    Venue_detail['Venue_Address'] = "Undefined";
                  }

                  if ('city' in Venue_result[0] && 'state' in Venue_result[0]) {
                    Venue_detail['Venue City & State'] = Venue_result[0]['city']['name'] + ", " + Venue_result[0]['state']['name'];
                  } else if ('city' in Venue_result[0]) {
                    Venue_detail['Venue City & State'] = Venue_result[0]['city']['name'];
                  } else if ('state' in Venue_result[0]) {
                    Venue_detail['Venue City & State'] = Venue_result[0]['state']['name'];
                  } else {
                    Venue_detail['Venue City & State'] = "Undefined";
                  }

                  if ('location' in Venue_result[0]) {
                    Venue_detail['Venue_lat'] = Venue_result[0]['location']['latitude'];
                    Venue_detail['Venue_lon'] = Venue_result[0]['location']['longitude'];
                  } else {
                    Venue_detail['Venue_lat'] = "Undefined";
                    Venue_detail['Venue_lon'] = "Undefined";
                  }

                  if ('boxOfficeInfo' in Venue_result[0]) {
                    if ('phoneNumberDetail' in Venue_result[0]['boxOfficeInfo']) {
                      Venue_detail['Venue_Phone'] = Venue_result[0]['boxOfficeInfo']['phoneNumberDetail'];
                    } else {
                      Venue_detail['Venue_Phone'] = "Undefined";
                    }
                    if ('openHoursDetail' in Venue_result[0]['boxOfficeInfo']) {
                      Venue_detail['Open_hour'] = Venue_result[0]['boxOfficeInfo']['openHoursDetail'];
                    } else {
                      Venue_detail['Open_hour'] = "Undefined";
                    }
                  } else {
                    Venue_detail['Venue_Phone'] = "Undefined";
                    Venue_detail['Open_hour'] = "Undefined";
                  }

                  if ('generalInfo' in Venue_result[0]) {
                    if ('generalRule' in Venue_result[0]['generalInfo']) {
                      Venue_detail['General_rule'] = Venue_result[0]['generalInfo']['generalRule'];
                    } else {
                      Venue_detail['General_rule'] = "Undefined";
                    }
                    if ('childRule' in Venue_result[0]['generalInfo']) {
                      Venue_detail['Child_rule'] = Venue_result[0]['generalInfo']['childRule'];
                    } else {
                      Venue_detail['Child_rule'] = "Undefined";
                    }
                  } else {
                    Venue_detail['General_rule'] = "Undefined";
                    Venue_detail['Child_rule'] = "Undefined";
                  }

                  res.send(JSON.stringify(Venue_detail));
                  console.log("Venue data send finished!")
                }
              }).catch(error => {
                console.log("Error for venue", error);
              })
          })
          .catch(error => {
            console.log("Error for TicketMaster", error);
          })
      }
    })
    .catch(error => {
      console.log("Error for Geocoding", error);
    })
});

app.use(express.static(path.join(__dirname, 'dist/frontend')));
app.get('/*', function (req, res) {
  res.sendFile(path.join(__dirname + '/dist/frontend/index.html'));
})

// Start the server
const PORT = parseInt(process.env.PORT) || 8080;
app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
  console.log('Press Ctrl+C to quit.');
});
// [END gae_node_request_example]

