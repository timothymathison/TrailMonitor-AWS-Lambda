# Trail Monitor AWS Cloud Service
---
## Summary

This project comprises the back end data service for the Polaris 2018 Senior Design project.
The service collects, processes, stores, and retrieves trail data corresponding to trail conditions around the world. Computing is accomplished using AWS (Amazon Web Services) Lambda functions.
Data collection is done by the on-vehicle device designed as part of the same project.

---

## Data Formats

### Input Trail Data (JSON):
```
{"deviceId" : <unique-id>,
	"data" :
	[
		{"timeStamp" : 1500654919, "latitude" : 44.777555666, "longitude" : -90.555666777, "value" : 5},
		{"timeStamp" : <epoch-milliseconds>, "latitude" : <valid-latitude>, "longitude" : <valid-longitude>, "value" : <roughness-value>}
		{<trail-point-3>},
		{<trail-point-4>},
		.
		.
		.
	]
}
```

#### To Call

Method: `POST`

URL (Development): `https://s71x34ids1.execute-api.us-east-2.amazonaws.com/TrailMonitor_Beta/trail-data`

URL (Production): `https://s71x34ids1.execute-api.us-east-2.amazonaws.com/TrailMonitor/trail-data`

Header(s): 
- `Content-Type: application/json`

---

### Output for Visualization (GeoTrailInfo using GeoJSON):
```
{
	type: "geotrailinfo",
	message: "<message from server>",
	status: "<status-code> <status-text>",
	data: {
		availableZoomRanges: [ "4-6", "6-10", "10-50" ],
		featureCount: <total-num-GeoJson-features>,
		zoomRange: "<corresponding-zoom-range-for-this-request-and-data>",
		tiles: [
			{
				type: "<data-type>",
				zoomRange: "<corresponding-zoom-range-for-which-data-is-valid>"
				cornerCoordinate: { lng: <integer-longitude>, lat: <integer-latitude> },
				totalTraffic: <total-num-raw-points-for-this-tile>,
				pointData: [
					{
						"type": "Feature",
						"geometry": {
							"type": "Point",
							"coordinates": [<longiduted-value>, <latitude-value>]
						},
						"properties": {
							"value": <roughness-value>,
							"traffic": <num-raw-points>,
							"deviceIds": ["<id-of-one-originating-device>", "<id-of-second-originating-device>", ...],
							"timeStamp": <milliseconds-since-epoch>
						}
					},
					.
					.
					.
				],
				lineData: [
					{
						"type": "Feature",
						"geometry": {
							"type": "LineString",
							"coordinates": [[<start-longiduted-value>, <start-latitude-value>], [<end-longiduted-value>, <end-latitude-value>]]
						},
						"properties": {
							"value": <roughness-value>,
							"traffic": <num-raw-points>,
							"deviceIds": ["<id-of-one-originating-device>", "<id-of-second-originating-device>", ...],
							"timeStamp": <milliseconds-since-epoch>
						}
					},
					.
					.
					.
				]
			},
			{
				type: "FeatureCollection",
				zoomRange: "10-50"
				cornerCoordinate: { lng: -93, lat: 45 },
				totalTraffic: 615,
				pointData: [
					{
						"type": "Feature",
						"geometry": {
							"type": "Point",
							"coordinates": [-92.958210, 45.363131]
						},
						"properties": {
							"value": 4,
							"traffic": 3,
							"deviceIds": ["240057000b51343334363138", "240057000a41343334363138", ...],
							"timeStamp": 1522431342473
						}
					},
					.
					.
					.
				],
				lineData: [
					{
						"type": "Feature",
						"geometry": {
							"type": "LineString",
							"coordinates": [[-92.958210, 45.363131], [-92.958210, 45.363231]]
						},
						"properties": {
							"value": 3,
							"traffic": 5,
							"deviceIds": ["240057000b51343334363138", "240057000a41343334363138", ...],
							"timeStamp": 1522431342473
						}
					},
					.
					.
					.
				]
			},
			.
			.
			.
		]
	}
}
```

#### To Call

Method: `GET`

URL (Development): `https://s71x34ids1.execute-api.us-east-2.amazonaws.com/TrailMonitor_Beta/trail-data`

URL (Production): `https://s71x34ids1.execute-api.us-east-2.amazonaws.com/TrailMonitor/trail-data`

Header(s): 
- `Accept: application/json ...`

---

## Dependencies

Required:
- [Java SE 8](https://docs.oracle.com/javase/8/)

Recommended:
- [IDE - Eclipse Oxygen](http://www.eclipse.org/downloads/)
- [AWS Toolkit for Eclipse](https://aws.amazon.com/eclipse/) (Retreive access keys by creating user in AWS IAM console)


## Deployment and Testing

The two lambda code execution entry points in this project are a POST handler:
```
	public class ProcessDataFromPost implements RequestHandler<PostDataRequest, PostDataResponse> {

		public PostDataResponse handleRequest(PostDataRequest request, Context context) {
			.
			.
			.
		}
		.
		.
		.
	}
```
and a GET handler:
```
	public class ProcessGeoJsonRequest implements RequestHandler<GetDataRequest, GetDataResponse<GeoTrailInfo>> {

		public GetDataResponse<GeoTrailInfo> handleRequest(GetDataRequest request, Context context) {
			.
			.
			.
		}
		.
		.
		.
	}
```
The handler methods are the entry points for each Lambda function, and will be invoked by Lambda in response to input from the event sources of each function.

---

Each Lambda function code can be tested localy using these steps (example for `ProcessDataFromPost` handler):

- Open up __ProcessDataFromPostTest.java__
- Fill in your test logic to validate the input and output of your function handler
- Then run it locally as a normal JUnit test.
- The unit test provides a sample JSON input file if you have chosen a predefined event type as your function input
- You can modify the JSON file, or create new ones based on it

---

To deploy/upload project code to cloud:

- Under Project or Package Explorer View, right-click on your project and select __Amazon Web Services -> Upload Function to AWS Lambda__
- Then follow the steps to create a new Lambda function or upload code to an existing function.

---

To Invoke Lambda Function in cloud (from Eclipse):

- Right-click on your project again and select __Amazon Web Services -> Run on AWS Lambda__
- In the input dialog, enter the JSON input for your function, or select one of the JSON files in your project
- You can add new JSON input files in your project, and they will show up in this dialog as long as the file name ends with ".json"
- Click __Invoke__ and check the output of your function in the Eclipse Console View


__Note:__ Function should also be tested via the __API Gateway__ interface, and final testing should be done using the __API URL__ above.

---

## Design - Algorithms and Logic

The algorithms and logic within this project can be categorized primarily within two main categories:
Saving and retreiving trail data to and from the database; and processing trail data points into GeoJson visualization features.

Processing the trail data into an accurate visualization of trail conditions is the more complicated and computationally intense task.
Currently, this processing takes place each and every time visualization data is requested.
In the future, depending on performance considerations, it may be preferable to perform these calculations at certain intervals and store the resulting JSON in files, which can be retrieved by visualization requests.

Since the the use of the system will quickly and inevitably result in the accumulation of points over the top of each other in certain areas, the visualization algorithm uses a 2-dim bucket sort stratagy to geographically organize trail condition points.
Points falling in the same bucket are combined into a single point. The grid resolution will adjust depending on the requested zoom detail.

In addition, the processing algorithm attempts to draw lines connecting points believed to have been created by the same device, along its path of movement.
This feature is the least developed, due to its complex nature and time limitations when designing the visualization algorithm.
Future improvements to the algorithm would most likely make use of point timestamps and the list order of points (points are automatically sorted by timestamp in the database) to calculate direction and speed of vehicle movements, from which to determine the correct line connections.
Such an approach differs from the current algorithm which creates lines connecting any points that are within a specific radius and share the same vehicle device id.
Meanwhile care must be taken to preserve processing speed performance by staying within the self-imposed O(n) runtime constraint.

---

## API Gateway Configuration

The API for this data service (TrailMonitor) has two methods (GET, and POST) and is deployed to two stages (TrailMonitor and TrailMonitor_Beta).
Currently, both stages are from deployments with identical configurations except for the Lambda functions to invoke: the `*_Beta` stage will invoke the pair of `*_Beta` Lambda functions.

Custom configurations are primarily set in relation to response status codes and body content mapping.

Below is the body mapping template for requests to the GET method (configured under: `API/Resources/<method>/Integration Response/Body Mapping Templates`):

Content-Type: `application/json`
```
#set($params = $input.params().get("querystring"))
{
    "params" : {
        #foreach($paramName in $params.keySet())
        "$paramName" : "$util.escapeJavaScript($params.get($paramName))"
        #if($foreach.hasNext),#end
        #end
    },
    "sourceIp" : "$context.identity.sourceIp"
}
```
This template passes all query params through to the Lambda function plus the IP address of the requesting entity.
The body for any request to the POST method is passed through unchanged.

Both API methods implement Regex (configured under: `API/Resources/<method>/Integration Response`) to determine what status code to return based on the content returned by the Lambda function.
Each Lambda function returns Json data (AWS automatically converts returned Java objects to Json form) containing the key value pair `"status": "<valid-http-status-code>"` indicating the correct status code of the response.

The API Regex patterns are:
- 400 - `.*"status": "400 Bad Request".*`
- 500 - `.*"status": "500 Internal Server Error".*`
- 200 - default for GET, no pattern
- 204 - default for POST, no pattern (__Note:__ 204 corresponds to not content; therefore empty body is returned)

For more detailed information and instructions on configuring API Gateway, see the API references below.

---

## References/Resources

- [AWS Web Console Login](https://640567404774.signin.aws.amazon.com/console)
- [AWS Lambda Developer Guide](http://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Getting started with AWS API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/getting-started.html#getting-started-prerequisites)
- [AWS API Gateway Developer Guide](https://docs.aws.amazon.com/apigateway/latest/developerguide/welcome.html)
- [AWS API Gateway Template Mapping](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html)
- [AWS DynamoDB Java Data Mapping](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBMapper.html)
- [GeoJson](http://geojson.org/)
- [GeoJson Wiki](http://wiki.geojson.org/GeoJSON_draft_version_6)