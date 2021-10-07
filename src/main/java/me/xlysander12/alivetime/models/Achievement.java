package me.xlysander12.alivetime.models;

public class Achievement {
    private int id;
    private String groupname;
    private double alivetime;
    private boolean sendMessage;

    public Achievement(int id, String groupname, double alivetime, boolean sendMessage) {
        this.id = id;
        this.groupname = groupname;
        this.alivetime = alivetime;
        this.sendMessage = sendMessage;
    }

    public int getId() {
        return id;
    }

    public String getGroupname() {
        return groupname;
    }

    public double getAlivetime() {
        return alivetime;
    }

    public boolean isSendMessage() {
        return sendMessage;
    }
}
