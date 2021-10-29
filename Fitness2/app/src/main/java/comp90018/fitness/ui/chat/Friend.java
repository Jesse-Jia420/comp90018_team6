package comp90018.fitness.ui.chat;

public class Friend {
    private int id;
    private String name;

    Friend(int id, String name){
        this.id =id;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    public int getId(){
        return this.id;
    }
}
