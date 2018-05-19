package h2test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {

    public Person() { }

    public Person(String name, int age) {

        setName(name);
        setAge(age);
    }

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "FULL_NAME")
    private String name;

    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "name=" + getName() + ", age=" + getAge();
    }

}