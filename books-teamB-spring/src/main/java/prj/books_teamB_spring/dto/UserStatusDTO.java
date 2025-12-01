package prj.books_teamB_spring.dto;

import java.time.LocalDateTime;

public class UserStatusDTO {
    private String username;
    private boolean online;
    private LocalDateTime lastSeen;
    private String status; // "online", "away", "busy", "offline"

    public UserStatusDTO() {}

    public UserStatusDTO(String username, boolean online) {
        this.username = username;
        this.online = online;
        this.lastSeen = LocalDateTime.now();
        this.status = online ? "online" : "offline";
    }

    // Getters et Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
        this.status = online ? "online" : "offline";
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
