package es.trapasoft.android.personaclient.modelo;

import java.io.Serializable;

/**
 * Esto es un POJO clásico para un modelo
 * La exigencia de que implemente Serializable es para poder pasar el objeto como parámetro
 * de una actividad a otra mediante intents
 */
public class Persona implements Serializable{
    int ID;
    String City;
    String FirstName;
    String LastName;
    String PhoneNumber;
    String Street;
    String ZipCode;

    int DeptID;


    // GETTERS Y SETTERS
    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public int getDeptID() {
        return DeptID;
    }

    public void setDeptID(int deptID) {
        DeptID = deptID;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "ID=" + ID +
                ", City='" + City + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", DeptID=" + DeptID +
                '}';
    }


    public String toStringShort() {
        return FirstName + " " + LastName + "; " + Street + ", " + ZipCode + " " + City;
    }

}
