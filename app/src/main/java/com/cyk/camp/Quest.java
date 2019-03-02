package com.cyk.camp;

public class Quest {
    public int num;
    public String question;
    public String answer;
    public String hint;
    public double latitude;
    public double longitude;

    public Quest(){}

    public Quest(int n, String q, String s, double la, double lo, String h){
        this.num = n;
        this.question = q;
        this.answer = s;
        this.latitude = la;
        this.longitude = lo;
        this.hint = h;
    }
}
