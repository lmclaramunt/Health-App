package com.example.health;

/**
 * Class used to process data collected by the SQLite database into the RecyclerView
 * @author Luis Claramunt
 * June 2021
 */
public class HealthData {
    private String date;
    private int bpm, respRate, nausea, headache, diarrhea, soreThroat, fever, muscleAche, smellTaste,
            cough, shortnessBreath, tired;

    public HealthData(String date, int bpm, int respRate, int nausea, int headache, int diarrhea,
                      int soreThroat, int fever, int muscleAche, int smellTaste, int cough, int shortnessBreath,
                      int tired){
        this.date=date;
        this.bpm=bpm;
        this.respRate =respRate;
        this.nausea=nausea;
        this.headache=headache;
        this.diarrhea=diarrhea;
        this.soreThroat=soreThroat;
        this.fever=fever;
        this.muscleAche=muscleAche;
        this.smellTaste=smellTaste;
        this.cough=cough;
        this.shortnessBreath=shortnessBreath;
        this.tired=tired;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getRespRate() {
        return respRate;
    }

    public void setRespRate(int respRate) {
        this.respRate = respRate;
    }

    public int getNausea() {
        return nausea;
    }

    public void setNausea(int nausea) {
        this.nausea = nausea;
    }

    public int getHeadache() {
        return headache;
    }

    public void setHeadache(int headache) {
        this.headache = headache;
    }

    public int getDiarrhea() {
        return diarrhea;
    }

    public void setDiarrhea(int diarrhea) {
        this.diarrhea = diarrhea;
    }

    public int getSoreThroat() {
        return soreThroat;
    }

    public void setSoreThroat(int soreThroat) {
        this.soreThroat = soreThroat;
    }

    public int getFever() {
        return fever;
    }

    public void setFever(int fever) {
        this.fever = fever;
    }

    public int getMuscleAche() {
        return muscleAche;
    }

    public void setMuscleAche(int muscleAche) {
        this.muscleAche = muscleAche;
    }

    public int getSmellTaste() {
        return smellTaste;
    }

    public void setSmellTaste(int smellTaste) {
        this.smellTaste = smellTaste;
    }

    public int getCough() {
        return cough;
    }

    public void setCough(int cough) {
        this.cough = cough;
    }

    public int getShortnessBreath() {
        return shortnessBreath;
    }

    public void setShortnessBreath(int shortnessBreath) {
        this.shortnessBreath = shortnessBreath;
    }

    public int getTired() {
        return tired;
    }

    public void setTired(int tired) {
        this.tired = tired;
    }
}
