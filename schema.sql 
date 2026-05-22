-- 1. users 테이블

CREATE TABLE `users` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `email` varchar(100) NOT NULL,
 `oauth_id` varchar(255) NOT NULL,
 `oauth_provider` enum('GOOGLE','APPLE') NOT NULL,
 `username` varchar(20) DEFAULT NULL,
 `nickname` varchar(20) DEFAULT NULL,
 `bio` varchar(255) DEFAULT NULL,
 `profile_image_url` varchar(255) DEFAULT NULL,
 `refresh_token` varchar(255) DEFAULT NULL,
 `refresh_expiration` datetime(6) DEFAULT NULL,
 `created_at` datetime(6) NOT NULL,
 `deleted` bit(1) NOT NULL,
 `deleted_at` datetime(6) DEFAULT NULL,
 PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  UNIQUE KEY `uk_users_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- 2. friends 테이블

 CREATE TABLE `friends` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `friend_user_id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_friends_user_id_friend_user_id` (`user_id`,`friend_user_id`),
  KEY `idx_friends_friend_user_id` (`friend_user_id`),
  CONSTRAINT `fk_friends_friend_user_id` FOREIGN KEY (`friend_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_friends_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- 3. chat_rooms 테이블

 CREATE TABLE `chat_rooms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `room_type` enum('DIRECT','GROUP') NOT NULL,
  `room_name` varchar(100) DEFAULT NULL,
  `direct_room_key` varchar(255) DEFAULT NULL,
  `last_message_content` varchar(255) NOT NULL,
  `last_message_at` datetime(6) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_chat_rooms_direct_room_key` (`direct_room_key`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- 4. chat_members 테이블
CREATE TABLE `chat_members` (
 `chat_room_id` bigint(20) NOT NULL,
 `user_id` bigint(20) NOT NULL,
 `active` bit(1) NOT NULL,
 `joined_at` datetime(6) NOT NULL,
 `created_at` datetime(6) NOT NULL,
 PRIMARY KEY (`chat_room_id`,`user_id`),
 UNIQUE KEY `uk_chat_members_chat_room_id_user_id` (`chat_room_id`,`user_id`),
 KEY `idx_chat_members_user_id_active` (`user_id`,`active`),
 CONSTRAINT `fk_chat_members_chat_room_id` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_rooms` (`id`),
 CONSTRAINT `fk_chat_members_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


-- 5. chat_messages 테이블

CREATE TABLE `chat_messages` (
 `chat_room_id` bigint(20) NOT NULL,
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `sender_id` bigint(20) NOT NULL,
 `sent_at` datetime(6) NOT NULL,
 `message_content` varchar(255) NOT NULL,
 PRIMARY KEY (`id`),
 KEY `idx_chat_messages_chat_room_sent_at` (`chat_room_id`,`sent_at`),
 KEY `idx_chat_messages_sender_id` (`sender_id`),
 CONSTRAINT `fk_chat_messages_chat_room_id` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_rooms` (`id`),
 CONSTRAINT `fk_chat_messages_sender_id` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
