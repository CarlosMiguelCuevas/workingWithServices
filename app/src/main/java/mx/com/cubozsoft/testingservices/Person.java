package mx.com.cubozsoft.testingservices;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by carlos on 27/06/16.
 */
public class Person implements Parcelable{
    public static final String DATAPARCELABLE = "dataPareclable";

    private String name;
    private String address;
    private String email;
    private String phone;
    private int idPicture;

    public Person(String email, String address, int idPicture, String name, String phone) {
        this.email = email;
        this.address = address;
        this.idPicture = idPicture;
        this.name = name;
        this.phone = phone;
    }


    protected Person(Parcel in) {
        name = in.readString();
        address = in.readString();
        email = in.readString();
        phone = in.readString();
        idPicture = in.readInt();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public int getIdPicture() {
        return idPicture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeInt(idPicture);
    }
}
