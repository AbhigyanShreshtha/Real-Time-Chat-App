# High-Level Design Document for Chat Application

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
- **Sign Up**: Users can create an account by providing an email, name, password, and profile photo. Upon registration, a unique UUID is assigned to each user.
- **Login**: Users can authenticate themselves to access the chat application.
- **Modify User Info**: Users can update their profile information except for the UUID, which is immutable.
- **Fetch User Info**: Retrieves user details from the MongoDB database using the user’s email.
- **Delete User**: Deletes a user from the system along with their associated data.

### Group Profiles
- **Create Group**: Users can create a group by providing a group name and profile photo.
- **Group Info**: Stores group-related information including participants, group photo, and admins.
- **Modify Group Info**: Allows updating group details except for the group UUID.
- **Fetch Group Info**: Retrieves group details from the database using the group UUID.
- **Delete Group**: Deletes a group from the system using the group UUID.

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
4. **Messaging**: Messages are published and subscribed to via Kafka topics. Both group and private chats utilize Kafka for reliable message delivery. Kafka stores message metadata and delivery statuses.
5. **User Activity**: Any user activity updates their "last seen" timestamp in MongoDB.

## Detailed Component Interactions

### Sign Up Process
1. User submits the sign-up form.
2. The system assigns a unique UUID to the new user.
3. User information is stored in the MongoDB UserInfo collection.
4. The user can later modify their information except for the UUID.

### Login Process
1. User submits login credentials.
2. The system retrieves user info linked to the email from MongoDB.
3. The system matches the provided password hash with the stored hash.
4. On success, a jwt session token is generated and stored in Redis, and is returned to the user.

### Session Handling
1. Redis manages session states.
2. JWTs are validated by the Auth module for every request in middleware before processing.

### File Upload and Handling
1. User sends a file.
2. The file is uploaded to AWS S3.
3. The system generates a download URL and returns it to the user.

### Messaging
1. User sends a message in a group or private chat.
2. The message, along with metadata, is published to a Kafka topic.
3. Other users subscribe to the topic and receive messages.
4. Messages persist in Kafka even if recipients are offline.

### Activity Tracking
1. On any user activity, the "last seen" timestamp is updated in MongoDB.

### Group APIs
1. **Create Group**:
   - User submits a request to create a group with a name and optional profile photo.
   - The system assigns a unique UUID to the new group.
   - Group information, including participants and admins, is stored in the MongoDB Groups collection.
   
2. **Fetch Group Info**:
   - Retrieves group details from the MongoDB Groups collection using the group UUID.
   
3. **Modify Group Info**:
   - Allows updating group details such as name, profile photo, and participants, but the group UUID cannot be modified.
   
4. **Delete Group**:
   - Deletes a group from the system using the group UUID.

## Conclusion
This high-level design ensures a robust and scalable chat application, leveraging WebSockets for real-time messaging, Kafka for reliable message delivery, Redis for efficient session management, MongoDB for data storage, and AWS S3 for file handling. This combination of technologies provides a solid foundation for building a responsive and user-friendly chat platform.

