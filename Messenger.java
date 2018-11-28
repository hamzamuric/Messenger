/*===========================================================================\
 * Copyright 2018. WANAT                                                     *
 *                                                                           *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 * http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 \==========================================================================*/

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Messenger {

    public static void main(String[] args) {

        Socket socket = null;
        String ip;

        if (args.length < 1) {
            System.out.println("Too few arguments");
            System.exit(0);
        }

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-c":
                    if (i < args.length - 1) {
                        ip = args[++i];
                    } else {
                        ip = "127.0.0.1";
                    }

                    try {
                        socket = new Socket(ip, 4444);
                    } catch (IOException e) {
                        System.out.println("Connection failed");
                        System.exit(0);
                    }
                    break;
                case "-s":
                    try {
                        ServerSocket serverSocket = new ServerSocket(4444);
                        socket = serverSocket.accept();
			System.out.println("Connection established");
                    } catch (IOException e) {
                        System.out.println("Connection failed");
                        System.exit(0);
                    }
                    break;
            }
        }

        if (socket == null) {
            System.out.println("Error: Socket not initialized");
            System.exit(0);
        }

        try {
            final PrintStream out = new PrintStream(socket.getOutputStream());
            final Scanner in = new Scanner(socket.getInputStream());

            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String input = scanner.nextLine();
                    out.println(input);
                }
            }).start();

            new Thread(() -> {
                while (true) {
                    if (in.hasNextLine()) {
                        System.out.println(in.nextLine());
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
