package test;

import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException {
		SimpleIRCServer server = new SimpleIRCServer(6667);
		server.run();
		while (true) {
			String message = server.getMessage();
			server.write(message);
		}
	}

}
