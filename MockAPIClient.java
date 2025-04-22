package com.team5.travelassistant.api;

import com.team5.travelassistant.model.TravelPlan;
import com.team5.travelassistant.model.User;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MockAPIClient {

    public Map<String, TravelPlan> fetchTravelOptions(User user) {
        try {
            return loadFromJson("src/main/resources/data/mock_travel_data.json");
        } catch (Exception e) {
            System.out.println("JSON load failed, using hardcoded fallback.");
            return getHardcodedOptions();
        }
    }

    private Map<String, TravelPlan> getHardcodedOptions() {
        Map<String, TravelPlan> options = new HashMap<>();

        options.put("Hawaii", new TravelPlan(
            "Hawaii",
            "Beachside Resort",
            "Hawaiian Airlines - Economy",
            "Surfing, Beach parties, Volcano tour",
            950.0
        ));

        options.put("Rome", new TravelPlan(
            "Rome",
            "Historic Inn",
            "Alitalia - Economy",
            "Colosseum tour, Museums, Italian cuisine",
            820.0
        ));

        options.put("Kyoto", new TravelPlan(
            "Kyoto",
            "Traditional Ryokan",
            "ANA Airlines - Economy",
            "Temple visits, Zen gardens, Sushi making",
            880.0
        ));

        options.put("Maldives", new TravelPlan(
            "Maldives",
            "Overwater Bungalow",
            "Maldivian Airways - Business",
            "Snorkeling, Relaxing, Luxury spa",
            1200.0
        ));

        options.put("Generic City", new TravelPlan(
            "Generic City",
            "Budget Hotel",
            "Any Airline - Economy",
            "Sightseeing, Local food, Shopping",
            500.0
        ));

        return options;
    }

    private Map<String, TravelPlan> loadFromJson(String path) throws Exception {
        Map<String, TravelPlan> options = new HashMap<>();

        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(new FileReader(path));

        for (Object obj : array) {
            JSONObject json = (JSONObject) obj;

            String destination = (String) json.get("destination");
            String hotel = (String) json.get("hotel");
            String flight = (String) json.get("flight");
            String activities = (String) json.get("activities");
            double cost = Double.parseDouble(json.get("cost").toString());

            options.put(destination, new TravelPlan(destination, hotel, flight, activities, cost));
        }

        return options;
    }
}
