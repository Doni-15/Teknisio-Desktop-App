package com.teknisio.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeoLocationUtil {

    public static class LocationResult {
        public double lat = -6.2088; // default to Jakarta
        public double lon = 106.8456;
        public String city = "Medan";
        public String region = "North Sumatra";
        public String country = "Indonesia";

        public LocationResult() {}

        public LocationResult(double lat, double lon, String city, String region, String country) {
            this.lat = lat;
            this.lon = lon;
            this.city = city;
            this.region = region;
            this.country = country;
        }

        @Override
        public String toString() {
            return city + ", " + region + ", " + country;
        }
    }

    /**
     * Fetches current location details based on client public IP.
     */
    public static LocationResult fetchLocation() {
        // Try https://ipapi.co/json/ first
        LocationResult res = fetchFromIpapi();
        if (res != null) {
            return res;
        }
        // Fallback to http://ip-api.com/json/
        res = fetchFromIpApiOrg();
        if (res != null) {
            return res;
        }
        // Return default fallback if both fail
        return new LocationResult();
    }

    private static LocationResult fetchFromIpapi() {
        try {
            URL url = new URL("https://ipapi.co/json/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (conn.getResponseCode() == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
                    double lat = json.has("latitude") ? json.get("latitude").getAsDouble() : -6.2088;
                    double lon = json.has("longitude") ? json.get("longitude").getAsDouble() : 106.8456;
                    String city = json.has("city") && !json.get("city").isJsonNull() ? json.get("city").getAsString() : "Medan";
                    String region = json.has("region") && !json.get("region").isJsonNull() ? json.get("region").getAsString() : "North Sumatra";
                    String country = json.has("country_name") && !json.get("country_name").isJsonNull() ? json.get("country_name").getAsString() : "Indonesia";
                    return new LocationResult(lat, lon, city, region, country);
                }
            }
        } catch (Exception e) {
            System.err.println("[GeoLocationUtil] Failed to fetch from ipapi.co: " + e.getMessage());
        }
        return null;
    }

    private static LocationResult fetchFromIpApiOrg() {
        try {
            URL url = new URL("http://ip-api.com/json/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (conn.getResponseCode() == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
                    if (json.has("status") && "success".equals(json.get("status").getAsString())) {
                        double lat = json.has("lat") ? json.get("lat").getAsDouble() : -6.2088;
                        double lon = json.has("lon") ? json.get("lon").getAsDouble() : 106.8456;
                        String city = json.has("city") && !json.get("city").isJsonNull() ? json.get("city").getAsString() : "Medan";
                        String region = json.has("regionName") && !json.get("regionName").isJsonNull() ? json.get("regionName").getAsString() : "North Sumatra";
                        String country = json.has("country") && !json.get("country").isJsonNull() ? json.get("country").getAsString() : "Indonesia";
                        return new LocationResult(lat, lon, city, region, country);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[GeoLocationUtil] Failed to fetch from ip-api.com: " + e.getMessage());
        }
        return null;
    }

    /**
     * Resolves a text address into a LocationResult with coordinates.
     */
    public static LocationResult geocodeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return null;
        }
        try {
            String query = java.net.URLEncoder.encode(address, "UTF-8");
            URL url = new URL("https://nominatim.openstreetmap.org/search?q=" + query + "&format=json&limit=1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) TeknisioApp/1.0");

            if (conn.getResponseCode() == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    com.google.gson.JsonArray arr = JsonParser.parseString(sb.toString()).getAsJsonArray();
                    if (arr.size() > 0) {
                        JsonObject obj = arr.get(0).getAsJsonObject();
                        double lat = obj.get("lat").getAsDouble();
                        double lon = obj.get("lon").getAsDouble();
                        String displayName = obj.get("display_name").getAsString();
                        
                        String[] parts = displayName.split(",");
                        String city = parts.length > 0 ? parts[0].trim() : "Medan";
                        String region = parts.length > 1 ? parts[1].trim() : "North Sumatra";
                        String country = parts.length > parts.length - 1 ? parts[parts.length - 1].trim() : "Indonesia";
                        
                        return new LocationResult(lat, lon, city, region, country);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[GeoLocationUtil] Geocoding failed for: " + address + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * Calculates the distance in kilometers between two coordinates using the Haversine formula.
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
