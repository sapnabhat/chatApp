1. A simple chatApp where clients exchange chat messages through a Web service. Given is a simple chat server (ChatServer.jar) that apps communicate with via HTTP. Used the HttpURLConnection class to implement your Web service client.
2. The main user interface for the app is a screen with a text box for entering a message to be sent, and a “send” button. The rest of the screen is a list view that shows the messages posted by this app. There is a settings screen where the server URI and this chat instance’s client name can be specified, which is saved in shared preferences.
3. First screen is the registration which generates a unique identifier (using the Java UUID class) to identify the app installation.
4. Chat messages are stored in a content provider for the app
5. The service helper class uses an intent service (RequestService, subclassing IntentService) to submit request messages to the chat server. This ensures that communication with the chat server is done on a background thread.
6. Whenever a client communicates with the web service, it always includes its current latitude and longitude coordinates as HTTP request headers. In turn, the service returns the current latitude and longitude of each peer with which the client is in communication (through the Web service).
7. Furthermore, each message is tagged with the latitude and longitude of the sender when they posted the message and this location information is included as metadata in the messages that are downloaded from the server. This is achieved using Fused Location API.
8. For the list of clients, also displayed is the geocoded address for each peer
9. 'Peers' button displays a list of peers registered with the web service.
