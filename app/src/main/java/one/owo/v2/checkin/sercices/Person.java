package one.owo.v2.checkin.sercices;

import java.io.Serializable;
import java.util.ArrayList;

public class Person implements Serializable {
    private String name;
    private String id;
    //private String dorm; // 新增寝室号
    private ArrayList<String> allInfo; // 其他信息
    private String status;

    public Person(String name, String id, ArrayList<String> allInfo) {
        this.name = name;
        this.id = id;
        this.allInfo = allInfo;
        this.status = "Unchecked"; // Present Absent Leave 默认Unchecked
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

  /*  public String getDorm() {
        return dorm;
    }*/
    public ArrayList<String> getAllInfo() {
        return allInfo;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}