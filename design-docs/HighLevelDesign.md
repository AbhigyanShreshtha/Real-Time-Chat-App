# High-Level Design Document for Chat Application

## Overview
This document outlines the high-level design of a chat application, detailing its primary components, their interactions, and the data flow. The architecture leverages MongoDB for database management, Kafka for message brokering, Redis for session handling, and AWS S3 for file storage.

## Components

### 1. Sign Up and Login
This module handles user registration and authentication.

#### Sign Up
- **User Sign Up**: Collects user information such as email, name, password, and profile photo. Upon registration, a unique UUID is assigned to each user.
- **Modify User Info**: Allows users to update their information except for the UUID, which is immutable.
- **Fetch User Info**: Retrieves user details from the MongoDB database using the user’s email.
- **Delete User**: Deletes a user from the system along with their associated data.

#### Login
- **Login**: Authenticates user credentials by matching the hashed password with the stored hash in the database. Upon success, a session is generated and stored in Redis, and a JWT is returned.
- **Logout**: Terminates the user's session by deleting the UUID and JWT pair from Redis.

### 2. MongoDB Database
The database stores user and group information.

#### UserInfo Collection
- **Fields**:
  1. EmailId
  2. Name
  3. Hashed Password
  4. Profile Photo (base64 encoded)
  5. Status
  6. Activity (last seen timestamp)
  7. Unique Id (UUID)
  8. Group Ids (array of UUIDs)

#### Groups Collection
- **Fields**:
  1. Group Id (UUID)
  2. Participants (array of UUIDs)
  3. Group Photo (base64 encoded)
  4. Group Info

### 3. Auth
Handles authentication and authorization.
- **Validate JWT for user**: Validates JSON Web Tokens to ensure secure access. This validation occurs in middleware before processing any requests.
- **On Failure deny access**: Denies access if JWT validation fails.

### 4. Session Handling
Manages user sessions using Redis.
- **Redis**: Used for maintaining session states and ensuring user authentication status.

### 5. File Handling
Handles file uploads and storage.
- **Upload to S3**: Stores files in AWS S3.
- **Send File**: Facilitates file sending functionality by generating and returning download URLs.

### 6. Messaging
Handles real-time messaging using Kafka.
- **Message Body**:
  1. Sender Id (UUID)
  2. Recipient Id (UUID)
  3. Topic (Kafka topic)
  4. Message Body
  5. File Download Link
  6. Sent Timestamp
  7. Delivery Status (sent/delivered/read)

#### Group Chat
- Users participate in group chats.
- Kafka topics are used to manage message streams.
- Messages persist even if recipients are offline, leveraging Kafka’s persistence capabilities.

#### Private Chat
- One-to-one messaging.
- Kafka topics are used for private message streams.
- Messages persist even if recipients are offline, leveraging Kafka’s persistence capabilities.

## Data Flow

1. **User Sign Up and Login**:
   - Users sign up and log in, interacting with the MongoDB database to store and retrieve user details.
   - Authentication is managed using JWTs validated by the Auth module.

2. **Session Management**:
   - Upon successful login, session information is handled by Redis, ensuring authenticated access to services.

3. **File Handling**:
   - Users can send files, which are uploaded to AWS S3.
   - The file handling module generates download URLs that are sent back to the user.

4. **Messaging**:
   - Messages are published and subscribed to via Kafka topics.
   - Both group and private chats utilize Kafka for real-time communication.
   - Kafka stores message metadata and delivery statuses.

5. **User Activity**:
   - Any user activity updates their "last seen" timestamp in the MongoDB database.

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
4. On success, a session is generated and stored in Redis, and a JWT is returned to the user.

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

## Conclusion
This high-level design ensures a robust and scalable chat application, leveraging modern technologies for real-time messaging, secure authentication, efficient session management, and reliable file handling. The use of MongoDB, Kafka, Redis, and AWS S3 provides a solid foundation for building a responsive and user-friendly chat platform.
