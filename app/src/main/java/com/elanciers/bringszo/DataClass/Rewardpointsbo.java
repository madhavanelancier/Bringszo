package com.elanciers.bringszo.DataClass;

public class Rewardpointsbo
{
    String id, date, visual_time, username, name,uname, whome, whomename, points, type;

    public Rewardpointsbo(String id, String date, String visual_time, String username, String name, String uname, String whome, String whomename, String points, String type) {
        this.id = id;
        this.date = date;
        this.visual_time = visual_time;
        this.username = username;
        this.name = name;
        this.uname = uname;

        this.whome = whome;
        this.whomename = whomename;
        this.points = points;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVisual_time() {
        return visual_time;
    }

    public void setVisual_time(String visual_time) {
        this.visual_time = visual_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }
    public String getUName() {
        return uname;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getWhome() {
        return whome;
    }

    public void setWhome(String whome) {
        this.whome = whome;
    }

    public String getWhomename() {
        return whomename;
    }

    public void setWhomename(String whomename) {
        this.whomename = whomename;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
