# Trail Monitor AWS Cloud Service
---
## Summary

This project comprises the back end data service for the Polaris 2018 Senior Design project.
The service collects, processes, stores, and retrieves trail data corresponding to trail conditions around the world. Computing is accomplished using AWS (Amazon Web Services) Lambda functions.
Data collection is done by the on-vehicle device designed as part of the same project.

---

## Data format

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
Method: `POST`

URL (Development): `https://s71x34ids1.execute-api.us-east-2.amazonaws.com/TrailMonitor_Beta/trail-data`

URL (Production): `https://s71x34ids1.execute-api.us-east-2.amazonaws.com/TrailMonitor/trail-data`

Header(s): 
- `Content-Type: application/json`

---

### Output for Visualization (GeoJSON):

Point Data: 
```
{
	"type": "geojson",
	"data": {
		"type": "FeatureCollection",
		"features": [
			{
				"type": "Feature",
				"geometry": {
					"type": "Point",
					"coordinates": [-92.958210, 45.363131]
				},
				"properties": {
					"value": 5, //range: 0 - 10
					"deviceId": 1010
					"timeStamp": 1521426922532 //milliseconds since epoch
				}
			},
			.
			.
			.
		]
	}
}
```

Line Data:

To come.

---

## Dependencies

Required:
- [Java SE 8](https://docs.oracle.com/javase/8/)

Recommended:
- [IDE - Eclipse Oxygen](http://www.eclipse.org/downloads/)
- [AWS Toolkit for Eclipse](https://aws.amazon.com/eclipse/) (Retreive access keys by creating user in IAM console)


## Deployment and Testing

The two lambda code execution entry points in this project are:
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
and
```
	public class ProcessGeoJsonRequest implements RequestHandler<GetDataRequest, GetDataResponse<GeoJson>> {

		public GetDataResponse<GeoJson> handleRequest(GetDataRequest request, Context context) {
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

To Invoke Lambda Function in cloud (from Eclipes):

- Right-click on your project again and select __Amazon Web Services -> Run on AWS Lambda__
- In the input dialog, enter the JSON input for your function, or select one of the JSON files in your project
- You can add new JSON input files in your project, and they will show up in this dialog as long as the file name ends with ".json"
- Click __Invoke__ and check the output of your function in the Eclipse Console View


__Note:__ Function should also be tested via the __API Gateway__ interface, and final testing should be done using the __API URL__ above.

---

## API Gateway Configuration

To come.

---

## Resources

- [AWS Web Console Login](https://640567404774.signin.aws.amazon.com/console)
- [AWS Lambda Developer Guide](http://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [Getting started with AWS API Gateway](https://docs.aws.amazon.com/apigateway/latest/developerguide/getting-started.html#getting-started-prerequisites)
- [AWS API Gateway Developer Guide](https://docs.aws.amazon.com/apigateway/latest/developerguide/welcome.html)
- [AWS API Gateway Template Mapping](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html)
- [AWS DynamoDB Java Data Mapping](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBMapper.html)
- [GeoJson](http://geojson.org/)
- [GeoJson Wiki](http://wiki.geojson.org/GeoJSON_draft_version_6)