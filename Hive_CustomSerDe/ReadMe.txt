Utilizes the Hive serde2 API (org.apache.hadoop.hive.serde2). 
This API should be used in favor of the older serde API, which has been deprecated.

In the JSONSerDe, the serialize() method converts the object into a JSON string 
represented by a Text object. To do the serialization from Java into JSON, I’ve 
opted to use the Jackson JSON library, which allows me to convert a Java object 
to a JSON string with just a small amount of code:

ObjectMapper mapper = new ObjectMapper();
// Let Jackson do the work of serializing the object
return new Text(mapper.writeValueAsString(deparsedObj));

Jackson understands how to convert basic Java objects like Maps, Lists, and 
primitives into JSON strings. However, the Java object that is passed into
the serialize() method is an internal Hive representation of a row, which 
Jackson can’t work with. The goal here is to use the ObjectInspector to interpret
the Hive object, and convert it into a more basic Java representation.

In the JSONSerDe code, this process is broken up into a number of methods. 
The control flow is fairly simple, so let’s just examine some of the interesting pieces:

private Object deparseObject(Object obj, ObjectInspector oi) {
 switch (oi.getCategory()) {
 case LIST:
   return deparseList(obj, (ListObjectInspector)oi);
 case MAP:
   return deparseMap(obj, (MapObjectInspector)oi);
 case PRIMITIVE:
   return deparsePrimitive(obj, (PrimitiveObjectInspector)oi);
 case STRUCT:
   return deparseStruct(obj, (StructObjectInspector)oi, false);
 case UNION:
   // Unsupported by JSON
 default:
   return null;
 }
}

The deparseObject()method is nothing more than a fork in the road. ObjectInspectors
have a category, which will identify the underlying subtype of the inspector.

private Object deparseList(Object obj, ListObjectInspector listOI) {
 List<Object> list = new ArrayList<Object>();
 List<?> field = listOI.getList(obj);
 ObjectInspector elemOI = listOI.getListElementObjectInspector();
 for (Object elem : field) {
   list.add(deparseObject(elem, elemOI));
 }
 return list;
}

In the deparseList() method, your goal is to translate a Hive list field into a Java 
array. In order to do this properly, you need to also deparse each of the list elements. 
Fortunately, you can obtain an ObjectInspector specifically for the list elements from 
a ListObjectInspector. You can follow this same pattern with all the other Hive data 
types to fully translate the object, and then let Jackson do the work of writing out
a JSON object.

The opposite of serialize() is deserialize(). The deserialize() method takes a JSON 
string, and converts it into a Java object that Hive can process. Again, you can use 
Jackson to do most of the heavy lifting. Jackson will convert a JSON record into a 
Java Map with just a couple lines of code:

ObjectMapper mapper = new ObjectMapper();
  // This is really a Map<String, Object>. For more information about how
  // Jackson parses JSON in this example, see
  // http://wiki.fasterxml.com/JacksonDataBinding
  Map<?,?> root = mapper.readValue(blob.toString(), Map.class);
  

When deserializing, you need information from Hive about what type of data each field 
contains. You can use the TypeInfo API similarly to how we used ObjectInspectors 
while serializing. Looking again at handling a list type:

private Object parseList(Object field, ListTypeInfo fieldTypeInfo) {
   ArrayList<Object> list = (ArrayList<Object>) field;
   TypeInfo elemTypeInfo = fieldTypeInfo.getListElementTypeInfo();
   
   for (int i = 0; i < list.size(); i++) {
     list.set(i, parseField(list.get(i), elemTypeInfo));
   }

   return list.toArray();
  }

Also like ObjectInspectors, TypeInfos have subtypes. For a Hive list field, the 
TypeInfo is actually a ListTypeInfo, which we can use to also determine the type 
of the list elements. You can parse each list element one-by-one, and return the
necessary array.
Using the SerDe

Tables can be configured to process data using a SerDe by specifying the SerDe 
to use at table creation time, or through the use of an ALTER TABLE statement. 
For example:

ADD JAR /tmp/hive-serdes-1.0-SNAPSHOT.jar

CREATE EXTERNAL TABLE tweets (
   ...
    retweeted_status STRUCT<
      text:STRING,
      user:STRUCT<screen_name:STRING,name:STRING>>,
    entities STRUCT<
      urls:ARRAY<STRUCT<expanded_url:STRING>>,
      user_mentions:ARRAY<STRUCT<screen_name:STRING,name:STRING>>,
      hashtags:ARRAY<STRUCT<text:STRING>>>,
    text STRING,
   ...
  )
  PARTITIONED BY (datehour INT)
  ROW FORMAT SERDE 'com.cloudera.hive.serde.JSONSerDe'
  LOCATION '/user/flume/tweets';

The bolded section of the above CREATE TABLE statement shows how a table is
configured to use a SerDe. If the SerDe is not on the Hive classpath, it must
be added at runtime using the ADD JARcommand. It should be noted that one limitation
of the JSONSerDe is that the field names must match the JSON field names. JSON fields
that are not present in the table will be ignored, and records that don’t have certain
fields will return NULLs for any missing fields. As an example, the raw data that the
above fields refer to looks like this:

{
     "retweeted_status": {
        "contributors": null,
        "text": "#Crowdsourcing – drivers already generate traffic data for your smartphone to suggest alternative routes when a road is clogged. #bigdata",
        "geo": null,
        "retweeted": false,
        "in_reply_to_screen_name": null,
        "truncated": false,
        "entities": {
           "urls": [],
           "hashtags": [
              {
                 "text": "Crowdsourcing",
                 "indices": [
                    0,
                    14
                 ]
              },
              {
                 "text": "bigdata",
                 "indices": [
                    129,
                    137
                 ]
              }
           ],
           "user_mentions": []
        },
        "in_reply_to_status_id_str": null,
        "id": 245255511388336128,
        "in_reply_to_user_id_str": null,
        "source": "SocialOomph",
        "favorited": false,
        "in_reply_to_status_id": null,
        "in_reply_to_user_id": null,
        "retweet_count": 0,
        "created_at": "Mon Sep 10 20:20:45 +0000 2012",
        "id_str": "245255511388336128",
        "place": null,
        "user": {
           "location": "Oregon, ",
           "default_profile": false,
           "statuses_count": 5289,
           "profile_background_tile": false,
           "lang": "en",
           "profile_link_color": "627E91",
           "id": 347471575,
           "following": null,
           "protected": false,
           "favourites_count": 17,
           "profile_text_color": "D4B020",
           "verified": false,
           "description": "Dad, Innovator, Sales Professional. Project Management Professional (PMP).  Soccer Coach,  Little League Coach  #Agile #PMOT - views are my own -",
           "contributors_enabled": false,
           "name": "Scott Ostby",
           "profile_sidebar_border_color": "404040",
           "profile_background_color": "0F0F0F",
           "created_at": "Tue Aug 02 21:10:39 +0000 2011",
           "default_profile_image": false,
           "followers_count": 19005,
           "profile_image_url_https": "https://si0.twimg.com/profile_images/1928022765/scott_normal.jpg",
           "geo_enabled": true,
           "profile_background_image_url": "http://a0.twimg.com/profile_background_images/327807929/xce5b8c5dfff3dc3bbfbdef5ca2a62b4.jpg",
           "profile_background_image_url_https": "https://si0.twimg.com/profile_background_images/327807929/xce5b8c5dfff3dc3bbfbdef5ca2a62b4.jpg",
           "follow_request_sent": null,
           "url": "http://facebook.com/ostby",
           "utc_offset": -28800,
           "time_zone": "Pacific Time (US & Canada)",
           "notifications": null,
           "friends_count": 13172,
           "profile_use_background_image": true,
           "profile_sidebar_fill_color": "1C1C1C",
           "screen_name": "ScottOstby",
           "id_str": "347471575",
           "profile_image_url": "http://a0.twimg.com/profile_images/1928022765/scott_normal.jpg",
           "show_all_inline_media": true,
           "is_translator": false,
           "listed_count": 45
        },
        "coordinates": null
     },
     "contributors": null,
     "text": "RT @ScottOstby: #Crowdsourcing – drivers already generate traffic data for your smartphone to suggest alternative routes when a road is  ...",
     "geo": null,
     "retweeted": false,
     "in_reply_to_screen_name": null,
     "truncated": false,
     "entities": {
        "urls": [],
        "hashtags": [
           {
              "text": "Crowdsourcing",
              "indices": [
                 16,
                 30
              ]
           }
        ],
        "user_mentions": [
           {
              "id": 347471575,
              "name": "Scott Ostby",
              "indices": [
                 3,
                 14
              ],
              "screen_name": "ScottOstby",
              "id_str": "347471575"
           }
        ]
     },
     "in_reply_to_status_id_str": null,
     "id": 245270269525123072,
     "in_reply_to_user_id_str": null,
     "source": "web",
     "favorited": false,
     "in_reply_to_status_id": null,
     "in_reply_to_user_id": null,
     "retweet_count": 0,
     "created_at": "Mon Sep 10 21:19:23 +0000 2012",
     "id_str": "245270269525123072",
     "place": null,
     "user": {
        "location": "",
        "default_profile": true,
        "statuses_count": 1294,
        "profile_background_tile": false,
        "lang": "en",
        "profile_link_color": "0084B4",
        "id": 21804678,
        "following": null,
        "protected": false,
        "favourites_count": 11,
        "profile_text_color": "333333",
        "verified": false,
        "description": "",
        "contributors_enabled": false,
        "name": "Parvez Jugon",
        "profile_sidebar_border_color": "C0DEED",
        "profile_background_color": "C0DEED",
        "created_at": "Tue Feb 24 22:10:43 +0000 2009",
        "default_profile_image": false,
        "followers_count": 70,
        "profile_image_url_https": "https://si0.twimg.com/profile_images/2280737846/ni91dkogtgwp1or5rwp4_normal.gif",
        "geo_enabled": false,
        "profile_background_image_url": "http://a0.twimg.com/images/themes/theme1/bg.png",
        "profile_background_image_url_https": "https://si0.twimg.com/images/themes/theme1/bg.png",
        "follow_request_sent": null,
        "url": null,
        "utc_offset": null,
        "time_zone": null,
        "notifications": null,
        "friends_count": 299,
        "profile_use_background_image": true,
        "profile_sidebar_fill_color": "DDEEF6",
        "screen_name": "ParvezJugon",
        "id_str": "21804678",
        "profile_image_url": "http://a0.twimg.com/profile_images/2280737846/ni91dkogtgwp1or5rwp4_normal.gif",
        "show_all_inline_media": false,
        "is_translator": false,
        "listed_count": 7
     },
     "coordinates": null
  }
 

Once the table is set up, querying complex data is as simple as SQL:

hive> SELECT * FROM tweets;
OK
id	created_at	source	favorited	retweet_count	retweeted_status	entities	text	user	in_reply_to_screen_name
245270269525123072	Mon Sep 10 21:19:23 +0000 2012	web	false	0	{"text":"#Crowdsourcing – drivers already generate traffic data for yoursmartphone to suggest alternative routes when a road is clogged. #bigdata","user":{"screen_name":"ScottOstby","name":"Scott Ostby"}}	{"urls":[],"user_mentions":[{"screen_name":"ScottOstby","name":"Scott Ostby"}],"hashtags":[{"text":"Crowdsourcing"}]}	RT @ScottOstby: #Crowdsourcing – drivers already generate trafficdata for your smartphone to suggest alternative routes when a road is  ...	{"screen_name":"ParvezJugon","name":"Parvez Jugon","friends_count":299,"followers_count":70,"statuses_count":1294,"verified":false,"utc_offset":null,"time_zone":null}	NULL
Time taken: 3.07 seconds
