package org.sid.controller;

import org.sid.entity.Notification;
import org.sid.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    // Récupérer toutes les notifications
    @GetMapping
    public List<Notification> getAllNotifications() {
        logger.info("GET request to fetch all notifications");
        return notificationService.getAllNotifications();
    }
    
    // Récupérer les notifications non lues
    @GetMapping("/unseen")
    public List<Notification> getUnseenNotifications() {
        logger.info("GET request to fetch unread notifications");
        return notificationService.getUnseenNotifications();
    }
    
    // Marquer une notification comme lue
    @PutMapping("/{id}/seen")
    public ResponseEntity<?> markAsSeen(@PathVariable Long id) {
        logger.info("PUT request to mark notification {} as read", id);
        notificationService.markAsSeen(id);
        return ResponseEntity.ok().build();
    }
    
    // Marquer toutes les notifications comme lues
    @PutMapping("/seen-all")
    public ResponseEntity<?> markAllAsSeen() {
        logger.info("PUT request to mark all notifications as read");
        notificationService.markAllAsSeen();
        return ResponseEntity.ok().build();
    }
}