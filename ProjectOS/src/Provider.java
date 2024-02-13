import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Provider {

	public static void main(String[] args) {
		
		// Declare ServerSocket for handling incoming connections and a reference to a shared Library object
		ServerSocket providerSocket;
		Library sharedList;

		try {
			// Create a ServerSocket, binding it to port 2004, with a backlog of 10 connections
			providerSocket = new ServerSocket(2004, 10);
			// Create an instance of the Library class to be shared among threads
			sharedList = new Library();

			// Infinite loop to continuously wait for and handle incoming connections
			while (true) {
				// Print a message indicating that the server is waiting for a connection
				System.out.println("Waiting for connection");

				// Accept a connection from a client, blocking until a connection is established
				Socket connection = providerSocket.accept();
				// Create a new ServerThread to handle the communication with the connected client
				ServerThread serverThread = new ServerThread(connection, sharedList);
				// Start the thread, which will execute the run() method in a separate thread of execution
				serverThread.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}