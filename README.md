# Chat Application - High Level Design

## Overview
This document outlines the high-level design of a chat application that leverages modern technologies including WebSockets, Kafka, Redis, MongoDB, and AWS S3. The application supports user profiles, group profiles, private messages, group messages, and file sharing with reliable message delivery using Kafka.

## Key Components

1. **WebSockets**: Enables real-time communication for chat messages.
2. **Kafka**: Ensures reliable delivery of messages to both online and offline users.
3. **Redis**: Manages session states and ensures user authentication status.
4. **MongoDB**: Stores user and group information.
5. **AWS S3**: Handles file uploads and storage.

## Features

### User Profiles
- **Sign Up**: Users can create an account by providing an email, name, password, and profile photo.
- **Login**: Users can authenticate themselves to access the chat application.
- **Modify User Info**: Users can update their profile information.
- **Fetch User Info**: Retrieves user details from the database.
- **Delete User**: Deletes a user from the system.

### Group Profiles
- **Create Group**: Users can create a group by providing a group name and profile photo.
- **Group Info**: Stores group-related information including participants and group photo.
- **Modify Group Info**: Allows updating group details.
- **Fetch Group Info**: Retrieves group details from the database.

### Messaging
- **Private Messages**: One-to-one messaging using WebSockets and Kafka for real-time communication.
- **Group Messages**: Group chat functionality using WebSockets and Kafka for real-time communication.
- **Message Persistence**: Kafka ensures messages are persisted and delivered even if recipients are offline.

### File Sharing
- **Upload Files**: Users can upload files which are stored in AWS S3.
- **Download Links**: Generates and returns download URLs for shared files.

## Data Flow

1. **User Sign Up and Login**: Users sign up and log in, interacting with MongoDB for storing and retrieving user details. Authentication is managed using JWTs validated by the Auth module.
2. **Session Management**: Upon successful login, session information is managed by Redis.
3. **File Handling**: Files are uploaded to AWS S3, and download URLs are generated and shared with users.
4. **Messaging**: Messages are published and subscribed to via Kafka topics. Both group and private chats utilize Kafka for reliable message delivery. MongoDB stores message metadata and delivery statuses.
5. **User Activity**: Any user activity updates their "last seen" timestamp in MongoDB.

## Conclusion
This high-level design ensures a robust and scalable chat application, leveraging WebSockets for real-time messaging, Kafka for reliable message delivery, Redis for efficient session management, MongoDB for data storage, and AWS S3 for file handling. This combination of technologies provides a solid foundation for building a responsive and user-friendly chat platform.

