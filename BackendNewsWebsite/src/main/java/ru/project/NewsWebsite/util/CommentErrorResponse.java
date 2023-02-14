package ru.project.NewsWebsite.util;

public class CommentErrorResponse {
        private String message;
        private long timestamp;

        public CommentErrorResponse(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
}
