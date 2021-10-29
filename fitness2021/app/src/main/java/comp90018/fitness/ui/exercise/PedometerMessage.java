package comp90018.fitness.ui.exercise;

public class PedometerMessage {

    public int getStepCount(){return stepCount;}
    public int getStepDetect(){return stepDetect;}
    public Double getOutDistance(){return odistance;}

    private int stepCount;
    private int stepDetect;
    private Double odistance;

    public PedometerMessage(int stepCount, int stepDetect, Double odistance){
        this.stepCount = stepCount;
        this.stepDetect = stepDetect;
        this.odistance = odistance;

    }


}
