package com.teknisio.service;

import com.fazecast.jSerialComm.SerialPort;
import com.teknisio.util.NmeaParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service to manage port connection lifecycle and read GPS NMEA sentences using
 * jSerialComm.
 */
public class GpsHardwareService {

    private final String portName;
    private final int baudRate;

    private SerialPort serialPort;
    private Thread readThread;
    private volatile boolean isRunning = false;

    private final List<Consumer<NmeaParser.GpsPosition>> listeners = new ArrayList<>();

    public GpsHardwareService(String portName, int baudRate) {
        this.portName = portName;
        this.baudRate = baudRate;
    }

    public synchronized void addLocationListener(Consumer<NmeaParser.GpsPosition> listener) {
        listeners.add(listener);
    }

    /**
     * Attempts to open the serial port and start reading on a background daemon
     * thread.
     */
    public synchronized boolean start() {
        if (isRunning)
            return true;

        try {
            serialPort = SerialPort.getCommPort(portName);
            serialPort.setBaudRate(baudRate);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

            if (!serialPort.openPort()) {
                System.err.println("[GpsHardwareService] Failed to open port: " + portName);
                return false;
            }

            isRunning = true;
            readThread = new Thread(this::readSerialPort, "GPS-Serial-Read-Thread");
            readThread.setDaemon(true);
            readThread.start();
            System.out.println("[GpsHardwareService] Port " + portName + " opened successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("[GpsHardwareService] Error starting GPS hardware service: " + e.getMessage());
            return false;
        }
    }

    /**
     * Stops reading and safely closes the port.
     */
    public synchronized void stop() {
        if (!isRunning)
            return;
        isRunning = false;

        if (readThread != null) {
            readThread.interrupt();
        }

        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("[GpsHardwareService] Port " + portName + " closed.");
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void readSerialPort() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()))) {
            String line;
            while (isRunning && !Thread.currentThread().isInterrupted()) {
                line = reader.readLine();
                if (line == null)
                    break;

                String trimmed = line.trim();
                if (trimmed.startsWith("$")) {
                    NmeaParser.GpsPosition pos = NmeaParser.parse(trimmed);
                    if (pos != null) {
                        notifyListeners(pos);
                    }
                }
            }
        } catch (Exception e) {
            if (isRunning) {
                System.err.println("[GpsHardwareService] Read error: " + e.getMessage());
            }
        } finally {
            stop();
        }
    }

    private void notifyListeners(NmeaParser.GpsPosition pos) {
        synchronized (listeners) {
            for (Consumer<NmeaParser.GpsPosition> listener : listeners) {
                listener.accept(pos);
            }
        }
    }
}
