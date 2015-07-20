import java.io.*;
import java.net.*;
import java.util.*;

public final class proj1 {
	public static void main(String argv[]) throws Exception {
		int port = 5000;
		ServerSocket serverSocket = new ServerSocket(port);
		while (true) {
			Socket server = serverSocket.accept();

			HttpRequest request = new HttpRequest(server);
			Thread thread = new Thread(request);
			thread.start();
		}
	}

}

final class HttpRequest implements Runnable {

	final static String CRLF = "\r\n";
	Socket socket;

	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception {
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String requestLine = br.readLine();
		System.out.println();
		System.out.println(requestLine);

		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}

		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		String fileName = tokens.nextToken();
		fileName = "." + fileName;

		FileInputStream fileInputStream = null;
		boolean fileExists = true;
		try {
			fileInputStream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}
		
		String statusLine = null;
		String contentTypeLine = null; 
		String entityBody = null;
		
		if (fileExists) { 
			statusLine = "HTTP/1.0 200 OK" + CRLF;
			contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF;
		} else {
			statusLine = "​HTTP/1.0 200 OK 404 Not Found" + CRLF; 
			contentTypeLine = "Content-type: text/html" + CRLF; 
			entityBody = "<HTML>" +
					"<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
		}
		
		os.writeBytes(statusLine);
		
		os.writeBytes(contentTypeLine);
		
		os.writeBytes(CRLF);
		
		if (fileExists){
			sendBytes(fileInputStream, os); 
			fileInputStream.close();
		} else {
			os.writeBytes(entityBody);
		} 
		
		os.close();
		br.close();
		socket.close();
	}
	
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception { 
		byte[] buffer = new byte[1024];
		int bytes = 0;
		while((bytes = fis.read(buffer)) != -1 ) { 
			os.write(buffer, 0, bytes);
		} 
	} 
	
	private static String contentType(String fileName) {
		if(fileName.endsWith(".htm") || fileName.endsWith(".html")) { 
			return "text/html";
		} 
		if(fileName.endsWith(".gif") || fileName.endsWith(".GIF")) { 
			return "image/gif"; 
		} 
		if (fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		if(fileName.endsWith(".png")) {
			return "image/png";
		}
		if(fileName.endsWith(".pdf")) {
			return "application/pdf";
		}
		if(fileName.endsWith(".zip")) {
			return "application/zip";
		}
		return "application/octet-stream";
	}
}
