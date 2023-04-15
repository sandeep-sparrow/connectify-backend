package com.videopostingsystem.videopostingsystem.videos;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "videos")
public class Video {
    private String title;

    @Id
    @SequenceGenerator(
            name = "video_id_sequence",
            sequenceName = "video_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "video_id_sequence"
    )
    private Long id;

    @Column(name = "user_id")
    private String user;

    private String link;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private Date creationDate = new Date();

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    private Date lastModifiedDate = new Date();

    @Column(nullable = true)
    private String tag;

    private String summary;
    private String status;

    public Video( String user, String title, String link, String tag) {
        this.user = user;
        this.title = title;
        this.link = link;
        this.tag = tag;
    }

    public Video(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(title, video.title) && Objects.equals(id, video.id) && Objects.equals(user, video.user) && Objects.equals(link, video.link) && Objects.equals(creationDate, video.creationDate) && Objects.equals(lastModifiedDate, video.lastModifiedDate) && Objects.equals(tag, video.tag) && Objects.equals(summary, video.summary) && Objects.equals(status, video.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, id, user, link, creationDate, lastModifiedDate, tag, summary, status);
    }

    @Override
    public String toString() {
        return "Video{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", user=" + user +
                ", link='" + link + '\'' +
                ", creationDate=" + creationDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", tag='" + tag + '\'' +
                ", summary='" + summary + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
