package com.emerline.ijl01_10;

import com.emerline.ijl01_10.utils.Sender;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    final static int PORT = 8885;
    final static String INET_ADDRES = "224.2.2.5";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner in = new Scanner(System.in);
        String inputString;
        boolean infinity = true;

        System.out.print("Enter you name: ");
        inputString = in.nextLine();
        Sender sender = new Sender(PORT, INET_ADDRES, inputString);

        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(sender);

        while (infinity) {
            if (in.hasNext()) {
                inputString = in.nextLine();
                sender.sendMessage(inputString);
            }
        }

    }
}
