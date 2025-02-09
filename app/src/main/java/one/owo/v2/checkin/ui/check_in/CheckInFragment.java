package one.owo.v2.checkin.ui.check_in;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import one.owo.v2.checkin.R;
import one.owo.v2.checkin.sercices.Person;

public class CheckInFragment extends Fragment {
    private CheckInViewModel checkInViewModel;
    private ArrayList<String> names;
    private List<Person> personList;
    private boolean isInitialLoad = true;
    private boolean isSwipingPage = false;
    private RadioGroup radioGroup;

    public CheckInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in, container, false);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showConfirmationDialog();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle("check in");
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == android.R.id.home) {
                    showConfirmationDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        initPersonList();
        checkInViewModel = new ViewModelProvider(requireActivity()).get(CheckInViewModel.class);
        checkInViewModel.setNames(names);
        checkInViewModel.setPersonList(personList);
        checkInViewModel.setTitleRowItem((ArrayList<String>) getArguments().getSerializable("titleRowItem"));
        radioGroup = view.findViewById(R.id.turn);

        /*
        SequentialCheckInFragment（依次签到的页面 Fragment）
        FilterCheckInFragment（筛选签到的页面 Fragment）
        UncheckedFragment（未签到的页面 Fragment）
        CheckedInFragment（已签到的页面 Fragment）
         */
        ViewPager2 viewPager = view.findViewById(R.id.viewPages);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new SequentialCheckInFragment());
        fragments.add(new FilterCheckInFragment());
        fragments.add(new UncheckedFragment());
        fragments.add(new CheckedInFragment());
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (isInitialLoad) {
                    isInitialLoad = false;
                    return;
                }
                notifyChildFragment(position);
                updateRadioButton(position);
            }
        });

        radioGroup.check(R.id.sequentialCheckIn);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isSwipingPage) {
                if (checkedId == R.id.sequentialCheckIn) {
                    viewPager.setCurrentItem(0);
                } else if (checkedId == R.id.filterCheckIn) {
                    viewPager.setCurrentItem(1);
                } else if (checkedId == R.id.unchecked) {
                    viewPager.setCurrentItem(2);
                } else if (checkedId == R.id.checkedIn) {
                    viewPager.setCurrentItem(3);
                }
            }
        });
    }

    private void initPersonList() {
        personList = (ArrayList<Person>) getArguments().getSerializable("personList");
        names = new ArrayList<>();
        for (Person person : personList) {
            names.add(person.getName());
        }
    }

    private void saveAttendanceResults(List<Person> personList) {
        // 将签到结果保存到 SharedPreferences 或其他存储位置
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AttendanceApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(personList);
        editor.putString("attendanceResults", json);
        editor.apply();
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("确认返回")
            .setMessage("当前内容尚未保存，确认返回吗？")
            .setPositiveButton("确认", (dialog, which) -> {
                // 确认返回，调用 NavController 的 navigateUp()
                NavController navController = Navigation.findNavController(requireView());
                navController.navigateUp();
            })
            .setNegativeButton("取消", (dialog, which) -> {
                // 取消返回，不执行任何操作
                dialog.dismiss();
            })
            .create()
            .show();
    }

    private void updateRadioButton(int position) {
        isSwipingPage = true;
        switch (position) {
            case 0:
                radioGroup.check(R.id.sequentialCheckIn);
                break;
            case 1:
                radioGroup.check(R.id.filterCheckIn);
                break;
            case 2:
                radioGroup.check(R.id.unchecked);
                break;
            case 3:
                radioGroup.check(R.id.checkedIn);
                break;
        }
        isSwipingPage = false;
    }

    private void notifyChildFragment(int position) {
        // 这里可以通过适配器获取 Fragment 并调用其方法
        Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + position);

        if (fragment instanceof SequentialCheckInFragment) {
            ((SequentialCheckInFragment) fragment).onPageSelected();
        } else if (fragment instanceof UncheckedFragment) {
            ((UncheckedFragment) fragment).onPageSelected();
        } else if (fragment instanceof CheckedInFragment) {
            ((CheckedInFragment) fragment).onPageSelected();
        }
    }
}