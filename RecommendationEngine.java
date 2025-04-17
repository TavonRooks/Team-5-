import java.sql.*;

// AI algorithm and data matching logic.
public class RecommendationEngine {

    private Connection connection;

    // Database connection
    public RecommendationEngine(Connection connection) {
        this.connection = connection;
    }

    // This method generates recommendations for a specific user based on their preferences
    public String generateRecommendations(int userId) {
        StringBuilder result = new StringBuilder(); // Stores final recommendation result text

        try {
            // Get the user's profile using their ID
            PreparedStatement userStmt = connection.prepareStatement("SELECT * FROM UserProfiles WHERE UserID = ?");
            userStmt.setInt(1, userId); // Sets the user ID in the query
            ResultSet userRs = userStmt.executeQuery(); // Executes the query

            // If the user doesn't exist, return an error message
            if (!userRs.next()) return "User not found.";

            // Pull values from the user's profile
            String interests = userRs.getString("Interests").toLowerCase(); // Interests (ex: beaches, hiking)
            String concerns = userRs.getString("TravelConcerns").toLowerCase(); // Concerns (ex: safety)
            double budget = Double.parseDouble(userRs.getString("Budget")); // Budget as a number

            // Match Destinations
            result.append("Suggested Destinations:\n");
            PreparedStatement destStmt = connection.prepareStatement("SELECT * FROM Destinations");
            ResultSet destRs = destStmt.executeQuery();
            while (destRs.next()) {
                String tags = destRs.getString("Tags").toLowerCase();
                double cost = destRs.getDouble("AverageCost");

                // Check if tags match interests, do not include concerns, and are within budget
                if (tags.contains(interests) && !tags.contains(concerns) && cost <= budget) {
                    result.append("- ").append(destRs.getString("City")).append(", ")
                          .append(destRs.getString("Country")).append(" ($").append(cost).append(")\n");
                }
            }

            // Match Hotels
            result.append("\nSuggested Hotels:\n");
            PreparedStatement hotelStmt = connection.prepareStatement("SELECT * FROM Hotels");
            ResultSet hotelRs = hotelStmt.executeQuery();
            while (hotelRs.next()) {
                String location = hotelRs.getString("Location").toLowerCase();
                double price = hotelRs.getDouble("PricePerNight");
                String amenities = hotelRs.getString("Amenities").toLowerCase();

                // Match by amenities or location, exclude concerns, and make sure it's affordable
                if ((amenities.contains(interests) || location.contains(interests)) && !amenities.contains(concerns) && price * 5 <= budget) {
                    result.append("- ").append(hotelRs.getString("HotelName")).append(" in ")
                          .append(hotelRs.getString("Location")).append(" ($").append(price).append("/night)\n");
                }
            }

            // Step 4: Match Activities
            result.append("\nSuggested Activities:\n");
            PreparedStatement activityStmt = connection.prepareStatement("SELECT * FROM Activities");
            ResultSet actRs = activityStmt.executeQuery();
            while (actRs.next()) {
                String tags = actRs.getString("Tags").toLowerCase();
                double cost = actRs.getDouble("Cost");

                // Activity must match interest, exclude concern, and cost less than 20% of budget
                if (tags.contains(interests) && !tags.contains(concerns) && cost <= (budget * 0.2)) {
                    result.append("- ").append(actRs.getString("Name")).append(" in ")
                          .append(actRs.getString("Location")).append(" ($").append(cost).append(")\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating recommendations.";
        }

        return result.toString(); // Return the complete recommendation list
    }
}
