
package com.velora.backend.util;

import java.util.Locale;

public final class CategoryUtils {

    private CategoryUtils() {
    }

    public static String normalize(String category) {

        if (category == null || category.isBlank()) {
            return "";
        }

        String value = category
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace("&", " and ")
                .replaceAll("\\s+", " ");

        switch (value) {

            case "food":
            case "food and dining":
                return "Food & Dining";

            case "shopping":
                return "Shopping";

            case "entertainment":
                return "Entertainment";

            case "transport":
            case "transportation":
                return "Transport";

            case "health":
            case "healthcare":
                return "Healthcare";

            case "education":
                return "Education";

            case "bills and utilities":
                return "Bills & Utilities";

            case "subscriptions":
                return "Subscriptions";

            case "travel":
                return "Travel";

            case "other":
            case "others":
                return "Others";

            default:
                return category.trim();
        }
    }

    public static boolean isSame(
            String first,
            String second
    ) {
        return normalize(first)
                .equalsIgnoreCase(normalize(second));
    }
}
