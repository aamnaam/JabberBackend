## Backend for the Jabber Desktop Application
Includes a Multithreaded Server class, JabberDatabase that communicates with the PostgreSQL server, ClientConnection that handles each client in their own thread, and a pseudo-client Client class.

The Backend and Frontend communicate using JabberMessage objects, which contain a message and possibly some accompanying data.  

The multithreaded server creates a clientConnection object for each client. Requests are made and dealt with adhering to the following protocol:  
