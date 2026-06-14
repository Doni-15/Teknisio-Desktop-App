package com.teknisio.service;

import com.teknisio.dto.ServiceRequestDto;
import com.teknisio.util.NmeaParser;
import java.util.List;

/**
 * Manages background GPS tracking lifecycle for logged-in technicians.
 * Reads coordinates from GPS Hardware (COM5) and updates active service requests.
 */
public class GpsTrackerManager {

    private static GpsTrackerManager instance;
    private GpsHardwareService gpsService;
    private Thread trackerThread;
    private volatile boolean running = false;
    private double lastLat = 0.0;
    private double lastLon = 0.0;
    private boolean hasLastCoords = false;

    private GpsTrackerManager() {
        // Private constructor for singleton
    }

    public static synchronized GpsTrackerManager getInstance() {
        if (instance == null) {
            instance = new GpsTrackerManager();
        }
        return instance;
    }

    /**
     * Starts the background tracking thread.
     */
    public synchronized void start() {
        if (running) return;
        running = true;
        trackerThread = new Thread(this::runTracker, "Gps-Tracker-Manager-Thread");
        trackerThread.setDaemon(true);
        trackerThread.start();
        System.out.println("[GpsTrackerManager] Background GPS tracker thread started.");
    }

    /**
     * Stops the background tracking thread and GPS hardware listener.
     */
    public synchronized void stop() {
        running = false;
        if (trackerThread != null) {
            trackerThread.interrupt();
            trackerThread = null;
        }
        if (gpsService != null) {
            gpsService.stop();
            gpsService = null;
        }
        System.out.println("[GpsTrackerManager] Background GPS tracker stopped.");
    }

    private void runTracker() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Sleep for 10 seconds between checks
                Thread.sleep(10000);

                if (!SessionManager.isLoggedIn() || !SessionManager.isTechnician()) {
                    // If not logged in as technician, ensure gpsService is stopped
                    if (gpsService != null) {
                        gpsService.stop();
                        gpsService = null;
                    }
                    continue;
                }

                // If technician is logged in, start GPS hardware service if not already running
                if (gpsService == null) {
                    gpsService = new GpsHardwareService("COM5", 9600);
                    gpsService.addLocationListener(pos -> {
                        if (pos.hasFix()) {
                            lastLat = pos.getLatitude();
                            lastLon = pos.getLongitude();
                            hasLastCoords = true;
                            SessionManager.setCoordinates(lastLat, lastLon);
                            System.out.println("[GpsTrackerManager] GPS Fix updated: " + lastLat + ", " + lastLon);
                        }
                    });
                    boolean opened = gpsService.start();
                    if (!opened) {
                        // Failed to open COM5, set gpsService to null to retry in the next iteration
                        gpsService = null;
                    }
                }

                // If we have a valid coordinate fix, update the backend for any active requests
                if (hasLastCoords && (Math.abs(lastLat) > 0.0001 || Math.abs(lastLon) > 0.0001)) {
                    List<ServiceRequestDto> requests = TechnicianRequestService.getMyRequests(null);
                    for (ServiceRequestDto req : requests) {
                        String status = req.getStatus();
                        if ("ACCEPTED".equalsIgnoreCase(status) || "IN_PROGRESS".equalsIgnoreCase(status) || "ON_PROGRESS".equalsIgnoreCase(status)) {
                            System.out.println("[GpsTrackerManager] Updating active request " + req.getServiceRequestCode() + " with location: " + lastLat + ", " + lastLon);
                            TechnicianRequestService.updateRequestLocation(req.getServiceRequestId(), lastLat, lastLon);
                        }
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[GpsTrackerManager] Error in background tracker loop: " + e.getMessage());
            }
        }
    }
}
