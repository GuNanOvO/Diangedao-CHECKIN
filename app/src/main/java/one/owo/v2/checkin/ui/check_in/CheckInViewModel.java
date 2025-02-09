package one.owo.v2.checkin.ui.check_in;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import one.owo.v2.checkin.sercices.Person;

public class CheckInViewModel extends ViewModel {
    private ArrayList<String> names;
    private ArrayList<String> titleRowItem;
    private MutableLiveData<List<Person>> personList = new MutableLiveData<>(new ArrayList<>());


    // 获取共享的 Person 列表
    public LiveData<List<Person>> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList.setValue(personList);
    }

    public ArrayList<String> getNames() {
        return names;
    }


    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public ArrayList<String> getTitleRowItem() {
        return titleRowItem;
    }

    public void setTitleRowItem(ArrayList<String> titleRowItem) {
        this.titleRowItem = titleRowItem;
    }

    public Person getPersonByName(String name) {
        if (personList.getValue() != null) {
            for (Person person : personList.getValue()) {
                if (person.getName().equals(name)) {
                    return person;
                }
            }
        }
        return null;
    }

    public void notifyDataUpdate() {
        personList.setValue(personList.getValue());
    }
}
