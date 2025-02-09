package one.owo.v2.checkin.ui.check_in;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import one.owo.v2.checkin.R;
import one.owo.v2.checkin.sercices.Person;

public class FilterCheckInFragment extends Fragment {
    private Spinner spinner;
    private RecyclerView filterGroupRecyclerView;
    private FilterCheckInAdapter filterGroupAdapter;

    private CheckInViewModel checkInViewModel;
    private List<Person> personList;

    private List<String> itemList;
    private boolean isFirstLoad = true;

    public FilterCheckInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter_check_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemList = new ArrayList<>();
        spinner = view.findViewById(R.id.filterSpinner);

        checkInViewModel = new ViewModelProvider(requireActivity()).get(CheckInViewModel.class);
        checkInViewModel.getPersonList().observe(getViewLifecycleOwner(), data -> {
            personList = data;

            ArrayList<String> items = checkInViewModel.getTitleRowItem();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items);
            spinner.setAdapter(adapter);

            // 监听 Spinner 的选择事件
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // 获取选中的内容
                    String selectedItem = items.get(position);
                    Toast.makeText(requireContext(), "你选择了: " + selectedItem, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 没有选中时的回调
                }
            });
            System.out.println(personList);
            setItemList(spinner);
            if (isFirstLoad) {
                initCheckIn(view);
                isFirstLoad = false;
            }
            filterGroupAdapter.notifyDataSetChanged();
        });
        if (!isFirstLoad) {
           initCheckIn(view);
        }
    }

    public void initCheckIn(View view) {

        System.out.println(personList);


        Toast.makeText(requireContext(), "默认选择：" + spinner.getSelectedItem(), Toast.LENGTH_SHORT).show();

        filterGroupAdapter = new FilterCheckInAdapter(personList, itemList);
        filterGroupRecyclerView = view.findViewById(R.id.filterGroup);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        filterGroupRecyclerView.setLayoutManager(layoutManager);
        filterGroupRecyclerView.setAdapter(filterGroupAdapter);
        System.out.println("设置了adapter" + filterGroupAdapter);
    }

    //根据 spinner 选择项进行筛选然后设置 adapter
    public void setItemList(Spinner spinner) {
        // 根据 selectedItem 进行筛选
        System.out.println("默认：" + spinner.getSelectedItemPosition());
        int selectedItemPosition = spinner.getSelectedItemPosition();

        for (Person person : personList) {
            System.out.println(person.getAllInfo());
            itemList.add(person.getAllInfo().get(selectedItemPosition));
        }

    }
}