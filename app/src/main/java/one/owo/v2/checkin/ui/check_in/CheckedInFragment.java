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

import java.util.ArrayList;
import java.util.List;

import one.owo.v2.checkin.R;
import one.owo.v2.checkin.sercices.Person;

public class CheckedInFragment extends Fragment {
    private RecyclerView checkedInNameRecyclerView;
    private CheckInViewModel checkInViewModel;
    private CheckInListNameAdapter nameAdapter;
    private ArrayList<String> names;
    private List<Person> personList;


    public CheckedInFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checked_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 ViewModel
        checkInViewModel = new ViewModelProvider(requireActivity()).get(CheckInViewModel.class);
        checkInViewModel.getPersonList().observe(getViewLifecycleOwner(), data -> {
            System.out.println("checkedin observe被调用");
            personList = data;
            updateNames();
        });
        startCheckIn(view);

        // 观察 ViewModel 数据
//        checkInViewModel.getSharedData().observe(getViewLifecycleOwner(), data -> {
//            if (data != null) {
//                personList = (List<Person>) data;
//                updateNames();
//            }
//        });
    }

    private void startCheckIn(View view) {
        names = new ArrayList<>();
        updateNames();
        // 初始化 RecyclerView
        checkedInNameRecyclerView = view.findViewById(R.id.checkedInNameRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        checkedInNameRecyclerView.setLayoutManager(layoutManager);

        // 占位数据
        //names = new ArrayList<>();
        nameAdapter = new CheckInListNameAdapter(names, checkInViewModel);
        checkedInNameRecyclerView.setAdapter(nameAdapter);

        checkedInNameRecyclerView.post(() -> {
            int fontSize = nameAdapter.calculateOptimalFontSize(checkedInNameRecyclerView);
            nameAdapter.setFontSize(fontSize);
        });


    }
    private void updateNames() {
        // 清空旧数据
        names.clear();

        // 更新数据
        if (personList != null) {
            for (Person person : personList) {
                if (!"Unchecked".equals(person.getStatus())) {
                    names.add(person.getName());
                }
            }
        }

        // 通知适配器更新
        if (nameAdapter != null) {
            nameAdapter.notifyDataSetChanged();
        }
    }

    public void onPageSelected() {
        checkInViewModel.notifyDataUpdate();
        System.out.println("now is in CheckedInFragment");
    }
}