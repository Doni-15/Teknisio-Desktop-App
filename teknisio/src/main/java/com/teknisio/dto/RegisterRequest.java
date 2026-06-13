package com.teknisio.dto;

/**
 * Request payloads for Customer and Technician registration.
 * Field names MUST match backend DTOs:
 *   - RegisterCustomerRequest: name, email, phoneNumber, password, address
 *   - RegisterTechnicianRequest: name, email, phoneNumber, password, address, description
 */
public class RegisterRequest {

    public static class CustomerPayload {
        private String name;
        private String email;
        private String phoneNumber;
        private String password;
        private String address;

        public CustomerPayload(String name, String email, String phoneNumber, String password, String address) {
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.password = password;
            this.address = address;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getPassword() { return password; }
        public String getAddress() { return address; }
    }

    public static class TechnicianPayload {
        private String name;
        private String email;
        private String phoneNumber;
        private String password;
        private String address;
        private String description;

        public TechnicianPayload(String name, String email, String phoneNumber, String password, String address, String description) {
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.password = password;
            this.address = address;
            this.description = description;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getPassword() { return password; }
        public String getAddress() { return address; }
        public String getDescription() { return description; }
    }
}