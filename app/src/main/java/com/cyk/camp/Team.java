package com.cyk.camp;


public class Team {
    public String name;
    public int quest_number; //number of quests completed
    public int current_quest; //current quest number

    public Team(){}

    public Team(String s){
        this.name = s;
        this.quest_number = 0;
        this.current_quest = -1;
    }
}
