package com.umn.seniordesign.trailmonitor.entities.geojson;

import java.util.Arrays;
import java.util.List;

/**
 * @param <coordinateType> - Can be Double or List<Double>
 */
public class Geometry<coordinateType> {
	private GeometryTypes type;
	private List<coordinateType> coordinates;
	
	public enum GeometryTypes {
		Point,
		LineString
		//Incomplete list; can add more
	}
	
	/**
	 * <h1>Instantiates a new GeoJson.Geometry object if coordinates are a valid class type</h1>
	 * @param coordinates - List containing class type Double or nested List<Double>. Innermost List should be length 2, element1: <longitude>, element2: <latitude>
	 * @throws Exception Thrown if coordinates are empty or don't contain one of the two supported types
	 */
	@SuppressWarnings("rawtypes")
	public Geometry(List<coordinateType> coordinates) throws Exception {
		if(coordinates.size() == 0) {
			throw new Exception("Empty geometry coordinates list is prohibited");
		}
		this.coordinates = coordinates;
		coordinateType c = coordinates.get(0);
		if(c.getClass() == Double.class) {
			this.type = GeometryTypes.Point;
		}
		else if((c.getClass() == Arrays.asList("").getClass() || c.getClass().isInstance(List.class)) && ((List)coordinates.get(0)).get(0).getClass() == Double.class) {
			this.type = GeometryTypes.LineString;
		}
		else {
			throw new Exception("Un-supported type for GeoJson.Geometry object. " + coordinates.get(0).getClass().getName() + ", " 
					+ ((List)coordinates.get(0)).get(0).getClass().getName());
		}
	}
	
	public GeometryTypes getType() {
		return this.type;
	}
	
	public List<coordinateType> getCoordinates() {
		return this.coordinates;
	}
	
	
}