package com.packsendme.api.google.component;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.packsendme.api.google.utility.SeparationElementTools;
import com.packsendme.lib.common.response.dto.api.GoogleAPIDistanceResponse_Dto;
import com.packsendme.lib.simulation.http.SimulationRequest_Dto;

@Component
public class DistanceAPIData_Component {
	
	
	private final String ANALYSE_NODE_ROWS = "rows";
	private final String ANALYSE_NODE_ELEMENTS = "elements";
	private final String ANALYSE_ELEMENTS_STATUS = "status";
	private final String ANALYSE_ELEMENTS_DISTANCE = "distance";
	private final String ANALYSE_ELEMENTS_TEXT = "text";
	private final String ANALYSE_ELEMENTS_VALUE = "value";

	
	public GoogleAPIDistanceResponse_Dto getDistanceDataByJson (JSONObject jsonObject, SimulationRequest_Dto simulation) {
		GoogleAPIDistanceResponse_Dto distanceResponse_dto = new GoogleAPIDistanceResponse_Dto();
		SeparationElementTools separationElementObj = new SeparationElementTools();

		
		try {
			//create ObjectMapper instance
			ObjectMapper objectMapper = new ObjectMapper();
			//read JSON like DOM Parser
			JsonNode rootNode = objectMapper.readTree(jsonObject.toString());
			JsonNode rowsNode = rootNode.path(ANALYSE_NODE_ROWS);
			for (JsonNode rowObj : rowsNode) {
				JsonNode elementsNode = rowObj.path(ANALYSE_NODE_ELEMENTS);
				for (JsonNode elementObj : elementsNode) {
					String status = elementObj.path(ANALYSE_ELEMENTS_STATUS).asText();
					
					if(status.equals("OK")) {
			            JsonNode distanceNode = elementObj.path(ANALYSE_ELEMENTS_DISTANCE);
			            distanceResponse_dto.origin = simulation.address_origin;
			            distanceResponse_dto.destination = simulation.address_destination;
			            String distanceF = distanceNode.path(ANALYSE_ELEMENTS_TEXT).asText();
			            String distanceM = distanceNode.path(ANALYSE_ELEMENTS_VALUE).asText();
			            distanceResponse_dto.distanceM = Double.parseDouble(distanceM);
			            distanceResponse_dto.distanceF = separationElementObj.getDistanceParse(distanceF);
			            distanceResponse_dto.measureUnit = simulation.unity_measurement_distance_txt;
			            distanceResponse_dto.status = status;
					}
					else {
			            distanceResponse_dto.origin = simulation.address_origin;
			            distanceResponse_dto.destination = simulation.address_destination;
			            distanceResponse_dto.measureUnit = simulation.unity_measurement_distance_txt;
			            distanceResponse_dto.distanceM = 0;
			            distanceResponse_dto.distanceF = 0;
			            distanceResponse_dto.status = status;
					}
				}	
			}
            return distanceResponse_dto;
		}
		catch (Exception e ) {
			e.printStackTrace();
			return null;
		}
	}
	
}
