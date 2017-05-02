package com.emerline.ijl01_10.utils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by dzmitry.karayedau on 30-Apr-17.
 */
public class Sender implements Runnable {
    private MulticastSocket multicastSocket;
    private int port;
    private InetAddress group;

    private boolean infinite = true;

    private String ID;
    private String name;

    public Sender(int port, String inetAddress, String name) throws IOException, ClassNotFoundException {
        this.multicastSocket = new MulticastSocket(port);
        this.group = InetAddress.getByName(inetAddress);
        this.name = name;
        this.port = port;
        ID = calculateId(name);
        multicastSocket.joinGroup(group);
    }

    private String calculateId(String name) {
        Double d = new Double(name.hashCode() * Math.random());
        return d.toString();
    }

    private void requestTenLastMessages() throws IOException {
        Message fMes = new Message("getLastMessages", ID, "", name, "");
        sendMessage(fMes);
    }

    private void sendMessage(Message message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        byte[] sdata = baos.toByteArray();
        DatagramPacket data = new DatagramPacket(sdata, 0, sdata.length, group, port);
        multicastSocket.send(data);
    }

    public void sendMessage(String messageBody) throws IOException {
        Message mes = new Message("send", ID, "All", name, messageBody);
        sendMessage(mes);
    }

    private void receiveMessages() throws IOException, ClassNotFoundException {
        byte buf[] = new byte[1024];
        DatagramPacket data = new DatagramPacket(buf, buf.length);
        while (infinite) {
            this.multicastSocket.receive(data);
            ByteArrayInputStream bais = new ByteArrayInputStream(data.getData());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Message receiveMessage = (Message) ois.readObject();
            processMessage(receiveMessage);
            Thread.yield();
        }
        this.multicastSocket.close();
    }

    private void processMessage(Message message) throws IOException {
        if (((message.getDestination().equals(ID)) | (message.getDestination().equals("All"))) & !(message.getSender().equals(ID))) {
            System.out.println(message.getAuthor() + ": " + message.getMessageBody());
        }
    }

    @Override
    public void run() {
        try {
            requestTenLastMessages();
            receiveMessages();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
