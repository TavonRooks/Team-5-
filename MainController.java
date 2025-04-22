package com.team5.travelassistant.controller;

import com.team5.travelassistant.database.DBManager;
import com.team5.travelassistant.model.RecommendationEngine;
import com.team5.travelassistant.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.regex.Pattern;

public class MainController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField budgetField;
    @FXML private TextField interestsField;
    @FXML private TextField concernsField;
    @FXML private Button saveButton;
    @FXML private Button recommendButton;
    @FXML private TextArea resultArea;
    @FXML private ComboBox<String> feedbackField;
    @FXML private Button submitFeedbackButton;
    @FXML private TextArea historyArea;

    private DBManager dbManager;
    private RecommendationEngine recommendationEngine;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.\\w+$");

    @FXML
    public void initialize() {
        dbManager = new DBManager();
        recommendationEngine = new RecommendationEngine();

        feedbackField.getItems().addAll("1", "2", "3", "4", "5");
    }

    @FXML
    private void handleSaveUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String budgetText = budgetField.getText().trim();
        String interests = interestsField.getText().trim();
        String concerns = concernsField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || budgetText.isEmpty() || interests.isEmpty() || concerns.isEmpty()) {
            showMessage("All fields must be filled.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showMessage("Please enter a valid email address.");
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(budgetText);
        } catch (NumberFormatException e) {
            showMessage("Budget must be a valid number.");
            return;
        }

        User user = new User(name, email, budget, interests, concerns);
        boolean success = dbManager.insertUser(user);
        if (success) {
            showMessage("User saved successfully!");
        } else {
            showMessage("Failed to save user.");
        }
    }

    @FXML
    private void handleRecommend() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showMessage("Please enter your email to get recommendations.");
            return;
        }

        User user = dbManager.getUserByEmail(email);
        if (user == null) {
            showMessage("No user found with this email. Please save user first.");
            return;
        }

        String recommendation;
        try {
            recommendation = recommendationEngine.getRecommendation(user);
        } catch (Exception e) {
            recommendation = "⚠️ Recommendation engine failed.\nShowing sample plan instead:\n" +
                             "✈️ Destination: Hawaii\n🏨 Hotel: Beachside Resort\n🛫 Flight: Hawaiian Airlines\n🎯 Activities: Beach, Surfing\n💰 Estimated Cost: $950.00";
            e.printStackTrace();
        }
        resultArea.setText(recommendation);

        dbManager.saveRecommendation(email, recommendation);

        List<String> history = dbManager.getRecommendationsHistory(email);
        StringBuilder sb = new StringBuilder();
        for (String rec : history) {
            sb.append("- ").append(rec).append("\n\n");
        }
        historyArea.setText(sb.toString());
    }

    @FXML
    private void handleSubmitFeedback() {
        String email = emailField.getText().trim();
        String feedbackValue = feedbackField.getValue();

        if (email.isEmpty() || feedbackValue == null) {
            showMessage("Please enter your email and select a feedback rating.");
            return;
        }

        try {
            int rating = Integer.parseInt(feedbackValue);
            boolean success = dbManager.saveFeedback(email, String.valueOf(rating));
            if (success) {
                showMessage("Thank you for your feedback!");
            } else {
                showMessage("Failed to save feedback.");
            }
        } catch (NumberFormatException e) {
            showMessage("Invalid feedback rating selected.");
        }
    }

    private void showMessage(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
