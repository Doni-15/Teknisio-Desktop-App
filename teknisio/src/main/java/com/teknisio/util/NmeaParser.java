package com.teknisio.util;

/**
 * Utility to parse NMEA sentences ($GPRMC and $GPGGA) from GPS hardware.
 */
public class NmeaParser {

    public static class GpsPosition {
        private final double latitude;
        private final double longitude;
        private final boolean hasFix;
        private final String timestamp;

        public GpsPosition(double latitude, double longitude, boolean hasFix, String timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.hasFix = hasFix;
            this.timestamp = timestamp;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public boolean hasFix() {
            return hasFix;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Parse NMEA sentence. Returns null if invalid or unsupported sentence.
     */
    public static GpsPosition parse(String sentence) {
        if (sentence == null || !sentence.startsWith("$")) {
            return null;
        }

        // Remove checksum if present
        int asteriskIndex = sentence.indexOf('*');
        if (asteriskIndex != -1) {
            sentence = sentence.substring(0, asteriskIndex);
        }

        String[] parts = sentence.split(",", -1);
        String header = parts[0];

        try {
            if (header.contains("RMC")) {
                return parseGPRMC(parts);
            } else if (header.contains("GGA")) {
                return parseGPGGA(parts);
            }
        } catch (Exception e) {
            System.err.println("[NmeaParser] Error parsing sentence: " + sentence + " | " + e.getMessage());
        }
        return null;
    }

    private static GpsPosition parseGPRMC(String[] parts) {
        // Format: $GPRMC,time,status,lat,N/S,lon,E/W,speed,track,date,mag_var,dir*chk
        if (parts.length < 7)
            return null;

        String timestamp = parts[1];
        String status = parts[2]; // 'A' = Active/Valid, 'V' = Void/Invalid
        boolean hasFix = "A".equalsIgnoreCase(status);

        if (!hasFix) {
            return new GpsPosition(0.0, 0.0, false, timestamp);
        }

        double lat = convertToDecimalDegrees(parts[3], parts[4]);
        double lon = convertToDecimalDegrees(parts[5], parts[6]);

        return new GpsPosition(lat, lon, true, timestamp);
    }

    private static GpsPosition parseGPGGA(String[] parts) {
        // Format:
        // $GPGGA,time,lat,N/S,lon,E/W,fix_quality,num_satellites,hdop,altitude,M,geoidal_height,M,dgps_age,dgps_id*chk
        if (parts.length < 7)
            return null;

        String timestamp = parts[1];
        int quality = parts[6].isEmpty() ? 0 : Integer.parseInt(parts[6]);
        boolean hasFix = quality > 0; // Quality: 0 = Fix not available, 1 = GPS fix, 2 = DGPS fix

        if (!hasFix) {
            return new GpsPosition(0.0, 0.0, false, timestamp);
        }

        double lat = convertToDecimalDegrees(parts[2], parts[3]);
        double lon = convertToDecimalDegrees(parts[4], parts[5]);

        return new GpsPosition(lat, lon, true, timestamp);
    }

    /**
     * Converts NMEA coordinate formats DDMM.MMMM or DDDMM.MMMM to Decimal Degrees
     * (DD.DDDDD)
     */
    private static double convertToDecimalDegrees(String rawValue, String direction) {
        if (rawValue == null || rawValue.isEmpty() || direction == null || direction.isEmpty()) {
            return 0.0;
        }

        double degrees = 0.0;
        double minutes = 0.0;

        int dotIndex = rawValue.indexOf('.');
        if (dotIndex >= 2) {
            String degStr = rawValue.substring(0, dotIndex - 2);
            String minStr = rawValue.substring(dotIndex - 2);
            if (!degStr.isEmpty()) {
                degrees = Double.parseDouble(degStr);
            }
            minutes = Double.parseDouble(minStr);
        } else {
            // Fallback to absolute index parsing if no decimal point is found
            int degreeLength = (direction.equalsIgnoreCase("N") || direction.equalsIgnoreCase("S")) ? 2 : 3;
            if (rawValue.length() < degreeLength) {
                return 0.0;
            }
            String degStr = rawValue.substring(0, degreeLength);
            String minStr = rawValue.substring(degreeLength);
            degrees = Double.parseDouble(degStr);
            minutes = Double.parseDouble(minStr);
        }

        double decimalDegrees = degrees + (minutes / 60.0);

        if (direction.equalsIgnoreCase("S") || direction.equalsIgnoreCase("W")) {
            decimalDegrees = -decimalDegrees;
        }

        return decimalDegrees;
    }
}
