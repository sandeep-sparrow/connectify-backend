package com.videopostingsystem.videopostingsystem;


public class RequestBody {
    private String model;
    private Messages[] messages;
    private double temperature;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Messages[] getMessages() {
        return messages;
    }

    public void setMessages(Messages[] messages) {
        this.messages = messages;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    static class Messages{
        private String role;
        private String content;

        public Messages(String role, String content){
            this.role = role;
            this.content = content;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }
}
