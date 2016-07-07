package com.mysaasa.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Adam on 1/5/2015.
 */
public class BlogComment implements Serializable{
    private long id;
    private long parent_id;
    private String content;
    private User author;
    private Date dateCreated;
    private int score=0;

    private Boolean visible = true;
    public Boolean client_visible = true;

    @Override
    public String toString() {
        return "BlogComment{" +
                "id=" + id +
                ", parent_id=" + parent_id +
                ", content='" + content + '\'' +
                ", author=" + author +
                ", dateCreated=" + dateCreated +
                ", score=" + score +
                ", visible=" + visible +
                '}';
    }

    public long getId() {
        return id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public String getContent() {
        return content;
    }

    public User getAuthor() {
        return author;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public int getScore() {
        return score;
    }

    public Boolean getVisible() {
        return visible;
    }

    public BlogComment getParent(com.mysaasa.api.CommentManager commentManager) {
        if (parent_id == 0) return null;
        BlogComment parent = commentManager.lookupCommentById(parent_id);
        return parent;
    }

    public int calculateDepth(com.mysaasa.api.CommentManager commentManager) {
        int depth = 0;
        BlogComment parent = getParent(commentManager);
        while (parent != null) {
            depth++;
            parent = parent.getParent(commentManager);
        }
        return depth;
    }

    public List<BlogComment> getChildren(com.mysaasa.api.CommentManager manager) {
        return manager.getChildren(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogComment that = (BlogComment) o;

        if (id != that.id) return false;
        if (parent_id != that.parent_id) return false;
        if (score != that.score) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        if (dateCreated != null ? !dateCreated.equals(that.dateCreated) : that.dateCreated != null)
            return false;
        return !(visible != null ? !visible.equals(that.visible) : that.visible != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (parent_id ^ (parent_id >>> 32));
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (dateCreated != null ? dateCreated.hashCode() : 0);
        result = 31 * result + score;
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        return result;
    }
}
