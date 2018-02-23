# Trail Monitor AWS Service

## Summary
--
This project comprises the back end data service for the Polaris 2018 Senior Design project.
The service collects, processes, stores, and retrieves trail data corresponding to trail conditions around the world. Computing is accomplished using AWS (Amazon Web Services) Lambda functions.
Data collection is done by the on-vehicle device designed as part of the same project.

## Deployment

To come.

## Data format
---

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
Post: `https://s71x34ids1.execute-api.us-east-2.amazonaws.com/TrailMonitor_Beta/trail-data`

---

### Output for visualization (GeoJSON):
To come.

---

## Resources

- [AWS Web Console Login](https://640567404774.signin.aws.amazon.com/console)
- [Java IDE Eclipse](http://www.eclipse.org/downloads/)
- [Getting started with AWS-Lambda](https://docs.aws.amazon.com/apigateway/latest/developerguide/getting-started.html#getting-started-prerequisites)
- [AWS API Gateway Template Mapping](https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-mapping-template-reference.html)
- [AWS DynamoDB Java Data Mapping](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBMapper.html)