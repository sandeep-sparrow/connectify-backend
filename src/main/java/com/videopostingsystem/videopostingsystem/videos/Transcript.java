package com.videopostingsystem.videopostingsystem.videos;

public class Transcript {
    private String audio_url;
    private String id;
    private String text;
    private String status;
    private String upload_url;
    private boolean summarization;
    private String summary;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isSummarization() {
        return summarization;
    }

    public void setSummarization(boolean summarization) {
        this.summarization = summarization;
    }

    public String getSummary_type() {
        return summary_type;
    }

    public void setSummary_type(String summary_type) {
        this.summary_type = summary_type;
    }

    private String summary_type;

    public String getUpload_url() {
        return upload_url;
    }

    public void setUpload_url(String upload_url) {
        this.upload_url = upload_url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}