#!/bin/bash

#both input and output tables must have hash key: CompositeCoordinates and sort key: TimeStamp
if [ $# -lt 3 ]
then
	echo "Not enough arguments..."
	echo "Usage: copy-table-data <input-table> <output-table>"
	echo "both input and output tables must have hash key: CompositeCoordinates and sort key: TimeStamp"
	exit 1
fi

inputTable=$1
outputTable=$2

echo "reading data from table: $1 ..."
aws dynamodb scan --table-name $1 > table-data.json
count="$(cat table-data.json | jq '.Count')"
echo "$count items read"

lastKey="$(cat table-data.json | jq '.LastEvaluatedKey')"
if [ $lastKey != "null" ]
then
	echo "Table contains more data than can be read in single scan"
	echo "Need to modify this script to continue scanning at item with key: $lastKey"
fi

items="$(cat table-data.json | jq '.Items')"
echo "updating table: $2 ..."

echo "{ 
\"#Lo\": \"Longitude\", \"#La\": \"Latitude\", \"#DI\": \"DeviceId\", \"#Co\": \"Coordinate\", \"#Va\": \"Value\" 
}" > expression-attribute-names.json

put="SET #Lo = :lo, #La = :la, #DI = :di, #Co = :co, #Va = :v"

i=0
end=$count
progress=0
while [ $i -lt $end ]
do
	# key of item to update
	key="$(echo $items | jq ".[$i] | {CompositeCoordinates: .CompositeCoordinates, TimeStamp: .TimeStamp}")"
	echo $key > key.json

	CompCoord="$(echo $items | jq ".[$i].CompositeCoordinates")"
	TimeStamp="$(echo $items | jq ".[$i].TimeStamp")"
	Longitude="$(echo $items | jq ".[$i].Longitude")"
	Latitude="$(echo $items | jq ".[$i].Latitude")"
	DeviceId="$(echo $items | jq ".[$i].DeviceId")"
	Coordinate="$(echo $items | jq ".[$i].Coordinate")"
	Value="$(echo $items | jq ".[$i].Value")"

	echo "{
		\":lo\": $Longitude,
		\":la\": $Latitude,
		\":di\": $DeviceId,
		\":co\": $Coordinate,
		\":v\": $Value
	}" > expression-attribute-values.json

	# show progresss
	((progress = i * 100 / end))
	echo -ne "($progress%)\r"

	aws dynamodb update-item --table-name $2 --key file://key.json --update-expression "$put" --expression-attribute-names file://expression-attribute-names.json --expression-attribute-values file://expression-attribute-values.json
	((i++))
done
echo "($progress%)"

rm table-data.json
rm expression-attribute-values.json
rm expression-attribute-names.json
rm key.json

echo "done updating table"
echo "$i items updated"
