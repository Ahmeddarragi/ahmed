package org.sid.service;

import java.util.List;

import org.sid.entity.Notification;
import org.sid.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private NotificationRepository notificationRepository;

    public Notification sendNotification(String message) {
        logger.info("Creating new notification: {}", message);
        Notification notification = new Notification(message);
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        logger.info("Fetching all notifications");
        return notificationRepository.findAll();
    }
    
    public List<Notification> getUnseenNotifications() {
        logger.info("Fetching unread notifications");
        return notificationRepository.findBySeenFalse();
    }
    
    @Transactional
    public void markAsSeen(Long id) {
        logger.info("Marking notification {} as read", id);
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setSeen(true);
            notificationRepository.save(notification);
        });
    }
    
    @Transactional
    public void markAllAsSeen() {
        logger.info("Marking all notifications as read");
        List<Notification> unseenNotifications = notificationRepository.findBySeenFalse();
        unseenNotifications.forEach(notification -> notification.setSeen(true));
        notificationRepository.saveAll(unseenNotifications);
    }
}