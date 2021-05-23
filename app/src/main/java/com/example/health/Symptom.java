package com.example.health;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Luis M. Claramunt
 * User will select a symptom and assign a rating of how bad he/she feels it
 */
public class Symptom implements Parcelable {

    public static final Creator<Symptom> CREATOR = new Creator<Symptom>() {
        @Override
        public Symptom createFromParcel(Parcel in) {
            return new Symptom(in);
        }

        @Override
        public Symptom[] newArray(int size) {
            return new Symptom[size];
        }
    };

    private String name;
    private int rating;

    public Symptom(String name, int rating){
        this.name = name;
        this.rating = rating;
    }



    public String getName(){ return name; }

    public int getRating(){ return rating; }

    public void setName(String name){this.name = name;}

    public void setRating(int rating){this.rating = rating;}

    @Override
    public String toString(){
        return name + " -\t" + Integer.toString(rating);
    }

    //Parcelling
    protected Symptom(Parcel in) {
        name = in.readString();
        rating = in.readInt();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.rating);
    }
}
