package com.connectify.openapi;

public class OpenAPIResponseBody {

    private Choices[] choices;

    public Choices[] getChoices() {
        return choices;
    }

    public void setChoices(Choices[] choices) {
        this.choices = choices;
    }

    class Choices{

        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        class Message{
            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }

    }

}
