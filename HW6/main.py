# Geocoding API: AIzaSyA2TcTDTJYIZCQVxHk1mY3mASAsO_Tvp2s
# Ticketmaster API: 027XqtGGJmSaJSTQuO1abywiqPGKCnkO
# IP API: 5f3978d3111782

from flask import Flask, jsonify, request, send_from_directory
import requests
from geolib import geohash
import json

# If `entrypoint` is not defined in app.yaml, App Engine will look for an app
# called `app` in `main.py`.
app = Flask(__name__, static_url_path='', static_folder="static/")

Geocoding_API = 'AIzaSyA2TcTDTJYIZCQVxHk1mY3mASAsO_Tvp2s'
Ticket_API = '027XqtGGJmSaJSTQuO1abywiqPGKCnkO'
Geocoding_base_url = 'https://maps.googleapis.com/maps/api/geocode/json?'  
Ticket_base_url = 'https://app.ticketmaster.com/discovery/v2/events.json?' 
Venue_base_url = 'https://app.ticketmaster.com/discovery/v2/venues?'

cat_dic = {
    "Music": "KZFzniwnSyZfZ7v7nJ",
    "Sports": "KZFzniwnSyZfZ7v7nE",
    "ArtsTheatre": "KZFzniwnSyZfZ7v7na",
    "Film": "KZFzniwnSyZfZ7v7nn",
    "Miscellaneous": "KZFzniwnSyZfZ7v7n1",
    "Default": ["KZFzniwnSyZfZ7v7nJ", "KZFzniwnSyZfZ7v7nE", "KZFzniwnSyZfZ7v7na", "KZFzniwnSyZfZ7v7nn", "KZFzniwnSyZfZ7v7n1"]
}

@app.route('/')
def index():
    return app.send_static_file('index.html')

@app.route('/search', methods=["GET"])
def search():
    
    kwData = request.args.get('input_kw')
    distData = request.args.get('input_dist')
    catData = request.args.get('input_cat')
    locData = request.args.get('input_location')

    # print(request.args)

    # print(locData)

    Event_Date = []
    Event_Icon = []
    Event_Name = []
    Event_Genre = []
    Event_Venue = []

    payload = {
        'key': Geocoding_API,
        'address': locData
    }

    response = requests.get(Geocoding_base_url, params=payload).json()
    # print(response)
    
    if response['status'] == 'OK':
        geom = response['results'][0]['geometry']
        lat = geom['location']['lat']
        lon = geom['location']['lng']
        # print(lat)
        # print(lon)
        Geo_Hash = geohash.encode(lat, lon, 7)
        print(Geo_Hash)

        Ticket_API_payload = {
            'apikey': Ticket_API,
            'keyword': kwData,
            'segmentId': cat_dic[catData],
            'radius': distData,
            'unit': "miles",
            'geoPoint': Geo_Hash
        }

        Ticket_response = requests.get(Ticket_base_url, params=Ticket_API_payload).json()

        # print("Resposne:", Ticket_response)
        if(Ticket_response['page']['totalElements'] == 0):
            return jsonify("No Records found")
        else:
            Ticket_Data = Ticket_response["_embedded"]['events']
            if len(Ticket_Data) < 20:
                for i in range(len(Ticket_Data)):
                    if(Ticket_Data[i]['type'] == 'event'):
                        # print("1", Ticket_Data[i]['dates']['start']['localDate'])
                        # print("1", Ticket_Data[i]['dates']['start']['localTime'])
                        if(Ticket_Data[i]['dates']['start'].get('localTime') != None):
                            Event_Date.append(Ticket_Data[i]['dates']['start']['localDate'] + ' ' + Ticket_Data[i]['dates']['start']['localTime']) 
                        else:
                            Event_Date.append(Ticket_Data[i]['dates']['start']['localDate'] + " Undefined") 
                        Event_Icon.append(Ticket_Data[i]['images'][0]['url'])
                        Event_Name.append(Ticket_Data[i]['name'])
                        Event_Genre.append(Ticket_Data[i]['classifications'][0]['segment']['name'])
                        Event_Venue.append(Ticket_Data[i]['_embedded']['venues'][0]['name'])
            else:
                for i in range(20):
                    if(Ticket_Data[i]['type'] == 'event'):
                        if(Ticket_Data[i]['dates']['start'].get('localTime') != None):
                            Event_Date.append(Ticket_Data[i]['dates']['start']['localDate'] + ' ' + Ticket_Data[i]['dates']['start']['localTime']) 
                        else:
                            Event_Date.append(Ticket_Data[i]['dates']['start']['localDate'] + " Undefined") 
                        Event_Icon.append(Ticket_Data[i]['images'][0]['url'])
                        Event_Name.append(Ticket_Data[i]['name'])
                        Event_Genre.append(Ticket_Data[i]['classifications'][0]['segment']['name'])
                        Event_Venue.append(Ticket_Data[i]['_embedded']['venues'][0]['name'])

    final_resp = jsonify({'Date': Event_Date, 'Icon': Event_Icon, 'Event': Event_Name, 'Genre': Event_Genre, 'Venue': Event_Venue})
    final_resp.headers.add('Access-Control-Allow-Origin', '*')
        
    return final_resp

@app.route('/event', methods=["GET"])
def event():
    
    kwData = request.args.get('input_kw')
    distData = request.args.get('input_dist')
    catData = request.args.get('input_cat')
    locData = request.args.get('input_location')
    row = int(request.args.get('Row')) 

    payload = {
        'key': Geocoding_API,
        'address': locData
    }

    response = requests.get(Geocoding_base_url, params=payload).json()
    
    if response['status'] == 'OK':
        geom = response['results'][0]['geometry']
        lat = geom['location']['lat']
        lon = geom['location']['lng']
        # print(lat)
        # print(lon)
        Geo_Hash = geohash.encode(lat, lon, 7)
        print(Geo_Hash)

        Ticket_API_payload = {
            'apikey': Ticket_API,
            'keyword': kwData,
            'segmentId': cat_dic[catData],
            'radius': distData,
            'unit': "miles",
            'geoPoint': Geo_Hash
        }

        Ticket_response = requests.get(Ticket_base_url, params=Ticket_API_payload).json()
        Ticket_Data = Ticket_response["_embedded"]['events']
        Ticket_event_id = Ticket_Data[row]['id']

        print(Ticket_event_id)

        Event_result_payload = {
            'id': Ticket_event_id,
            'apikey': Ticket_API
        }

        Event_result_response = requests.get(Ticket_base_url, params=Event_result_payload).json()
        # print(Event_result_response)
        if(Event_result_response['page']['totalElements'] == 0):
            return jsonify("No Records found")
        else:
            Event_result = Event_result_response["_embedded"]['events']

            if(Event_result[0]['dates']['status']['code'] == "onsale"):
                Event_result[0]['dates']['status']['code'] = "On Sale"
            if(Event_result[0]['dates']['status']['code'] == "offsale"):
                Event_result[0]['dates']['status']['code'] = "Off Sale"
            if(Event_result[0]['dates']['status']['code'] == "cancelled"):
                Event_result[0]['dates']['status']['code'] = "Cancelled"
            if(Event_result[0]['dates']['status']['code'] == "postponed"):
                Event_result[0]['dates']['status']['code'] = "Postponed"
            if(Event_result[0]['dates']['status']['code'] == "rescheduled"):
                Event_result[0]['dates']['status']['code'] = "Rescheduled"

            final_dict = {}
            if(Ticket_Data[row].get('name') != None):
                final_dict["Event Name"] = Ticket_Data[row]['name']
            else:
                final_dict["Event Name"] = "Undefined"
            if(Event_result[0]['dates']['start'].get('localDate') != None):
                final_dict["localDate"] = Event_result[0]['dates']['start']['localDate']
            else:
                final_dict["localDate"] = "Undefined"
            if(Event_result[0]['dates']['start'].get('localTime') != None):
                final_dict["localTime"] = Event_result[0]['dates']['start']['localTime']
            else:
                final_dict["localTime"] = "Undefined"

            Artist_Team = []
            Artist_Team_URL = []
            if(Event_result[0]['_embedded'].get('attractions') != None):
                if(Event_result[0]['_embedded']['attractions'][0].get('name') != None):
                    for item in range(len(Event_result[0]['_embedded']['attractions'])):
                        Artist_Team.append(Event_result[0]['_embedded']['attractions'][item]['name'])
                        final_dict["Artist/Team"] = Artist_Team
                if(Event_result[0]['_embedded']['attractions'][0].get('url') != None):
                    for item in range(len(Event_result[0]['_embedded']['attractions'])):
                        if(Event_result[0]['_embedded']['attractions'][item].get('url') != None):
                            Artist_Team_URL.append(Event_result[0]['_embedded']['attractions'][item]['url'])
                            final_dict["Artist/Team URL"] = Artist_Team_URL
                        else:
                            Artist_Team_URL.append("Undefined")
                            final_dict["Artist/Team URL"] = Artist_Team_URL
                else:
                    final_dict["Artist/Team URL"] = ["Undefined"]
            else:
                final_dict["Artist/Team"] = ["Undefined"]
                final_dict["Artist/Team URL"] = ["Undefined"]
            if(Event_result[0]['_embedded'].get('venues') != None):
                if(Event_result[0]['_embedded']['venues'][0].get('name') != None):
                    final_dict["Venue"] = Event_result[0]['_embedded']['venues'][0]['name']
            else:
                final_dict["Venue"] = "Undefined"
            if(Event_result[0].get('classifications') != None):
                if(Event_result[0]['classifications'][0].get('segment') != None):
                    final_dict["Genres_segment"] = Event_result[0]['classifications'][0]['segment']['name']
                else:
                    final_dict["Genres_segment"] = "Undefined"
                if(Event_result[0]['classifications'][0].get('genre') != None):
                    final_dict["Genres_genre"] = Event_result[0]['classifications'][0]['genre']['name']
                else:
                    final_dict["Genres_genre"] = "Undefined"
                if(Event_result[0]['classifications'][0].get('subGenre') != None):
                    final_dict["Genres_subGenre"] = Event_result[0]['classifications'][0]['subGenre']['name']
                else:
                    final_dict["Genres_subGenre"] = "Undefined"
                if(Event_result[0]['classifications'][0].get('type') != None):
                    final_dict["Genres_type"] = Event_result[0]['classifications'][0]['type']['name']
                else:
                    final_dict["Genres_type"] = "Undefined"
                if(Event_result[0]['classifications'][0].get('subType') != None):
                    final_dict["Genres_subType"] = Event_result[0]['classifications'][0]['subType']['name']
                else:
                    final_dict["Genres_subType"] = "Undefined"
            else:
                final_dict["Genres_segment"] = "Undefined"
                final_dict["Genres_genre"] = "Undefined"
                final_dict["Genres_subGenre"] = "Undefined"
                final_dict["Genres_type"] = "Undefined"
                final_dict["Genres_subType"] = "Undefined"
            
            if(Event_result[0].get('priceRanges') != None):
                final_dict["Price Ranges"] = str(Event_result[0]['priceRanges'][0]['min']) + ' ' + str(Event_result[0]['priceRanges'][0]['max'])
            else:
                final_dict["Price Ranges"] = "Undefined"
            if(Event_result[0].get('dates') != None):
                if(Event_result[0]['dates'].get('status') != None):
                    final_dict["Ticket Status"] = Event_result[0]['dates']['status']['code']
            else:
                final_dict["Ticket Status"] = "Undefined"
            
            if(Event_result[0].get('url') != None):
                final_dict["Ticket URL"] = Event_result[0]['url']
            else:
                final_dict["Ticket URL"] = "Undefined"
            
            if(Event_result[0].get('seatmap') != None):
                final_dict["SeatMap_URL"] = Event_result[0]['seatmap']['staticUrl']
            else:
                final_dict["SeatMap_URL"] = "Undefined"

            # print(final_dict)

            final_resp = jsonify(final_dict)
            final_resp.headers.add('Access-Control-Allow-Origin', '*')
        
    return final_resp

@app.route('/venue', methods=["GET"])
def venue():
    
    kwData = request.args.get('input_kw')
    distData = request.args.get('input_dist')
    catData = request.args.get('input_cat')
    locData = request.args.get('input_location')
    row = int(request.args.get('Row')) 

    payload = {
        'key': Geocoding_API,
        'address': locData
    }

    response = requests.get(Geocoding_base_url, params=payload).json()
    
    if response['status'] == 'OK':
        geom = response['results'][0]['geometry']
        lat = geom['location']['lat']
        lon = geom['location']['lng']
        # print(lat)
        # print(lon)
        Geo_Hash = geohash.encode(lat, lon, 7)
        print(Geo_Hash)

        Ticket_API_payload = {
            'apikey': Ticket_API,
            'keyword': kwData,
            'segmentId': cat_dic[catData],
            'radius': distData,
            'unit': "miles",
            'geoPoint': Geo_Hash
        }

        Ticket_response = requests.get(Ticket_base_url, params=Ticket_API_payload).json()
        Ticket_Data = Ticket_response["_embedded"]['events']
        Ticket_venue_name = Ticket_Data[row]["_embedded"]['venues'][0]['name']

        Venue_result_payload = {
            'keyword': Ticket_venue_name,
            'apikey': Ticket_API
        }

        Event_result_response = requests.get(Venue_base_url, params=Venue_result_payload).json()
        # print(Event_result_response)
        if(Event_result_response['page']['totalElements'] == 0):
            return jsonify("No Records found")
        else:
            Event_result = Event_result_response["_embedded"]['venues']

            final_dict = {}

            final_dict["Venue Name"] = Event_result[0]["name"]
            if(Event_result[0].get("images") != None):
                final_dict["Venue Img"] = Event_result[0]["images"][0]["url"]
            else:
                final_dict["Venue Img"] = "Undefined"

            if(Event_result[0].get("address") != None):
                final_dict["Venue Address"] = Event_result[0]["address"]["line1"]
            else:
                final_dict["Venue Address"] = "Undefined"

            if(Event_result[0].get("city") != None and Event_result[0].get("state") != None):
                final_dict["Venue City & State"] = Event_result[0]["city"]["name"] + ", " + Event_result[0]["state"]["stateCode"]
            elif(Event_result[0].get("city") != None):
                final_dict["Venue City & State"] = Event_result[0]["city"]["name"]
            elif(Event_result[0].get("state") != None):
                final_dict["Venue City & State"] = Event_result[0]["state"]["stateCode"]
            else:
                final_dict["Venue City & State"] = "Undefined"

            if(Event_result[0].get("postalCode") != None):
                final_dict["Venue PostalCode"] = Event_result[0]["postalCode"]
            else:
                final_dict["Venue PostalCode"] = "Undefined"

            if(Event_result[0].get("url") != None):
                final_dict["Upcoming Events"] = Event_result[0]["url"]
            else:
                final_dict["Upcoming Events"] = "Undefined"

            final_resp = jsonify(final_dict)
            final_resp.headers.add('Access-Control-Allow-Origin', '*')
        
    return final_resp

if __name__ == '__main__':
    # This is used when running locally only. When deploying to Google App
    # Engine, a webserver process such as Gunicorn will serve the app. You
    # can configure startup instructions by adding `entrypoint` to app.yaml.
    app.run(host='127.0.0.1', port=8080, debug=True)
