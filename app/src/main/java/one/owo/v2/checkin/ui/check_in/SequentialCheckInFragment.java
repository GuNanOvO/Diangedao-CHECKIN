package one.owo.v2.checkin.ui.check_in;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import one.owo.v2.checkin.R;
import one.owo.v2.checkin.sercices.Person;
import one.owo.v2.checkin.unit.CenterLayoutManager;

public class SequentialCheckInFragment extends Fragment {
    private RecyclerView nameRecyclerView;
    private RadioGroup radioGroup;

    private NameAdapter nameAdapter;
    private CheckInViewModel checkInViewModel;
    private ArrayList<String> names;
    private List<Person> personList;
    private int focusedPosition = 0;
    private boolean isUpdatingButtonSate = false;

    public SequentialCheckInFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sequential_check_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkInViewModel = new ViewModelProvider(requireActivity()).get(CheckInViewModel.class);
        names = checkInViewModel.getNames();
        checkInViewModel.getPersonList().observe(getViewLifecycleOwner(), data -> {
            personList = data;
            updateButtonState();
        });
        initCheckInProcess(view);
    }

    private void initCheckInProcess(View view) {
        nameAdapter = new NameAdapter(names);
        LinearLayoutManager layoutManager = new CenterLayoutManager(requireContext());

        nameRecyclerView = view.findViewById(R.id.nameRecyclerView);
        nameRecyclerView.setAdapter(nameAdapter);
        nameRecyclerView.setLayoutManager(layoutManager);

        nameRecyclerView.post(() -> {
            int itemHeight = nameRecyclerView.getHeight() / 3;
            nameAdapter.setItemHeight(itemHeight);
            //依照itemHeight重新设置RecyclerView的高度
            ViewGroup.LayoutParams params = nameRecyclerView.getLayoutParams();
            params.height = itemHeight * 3;
            nameRecyclerView.setLayoutParams(params);
            //设置字体大小
            nameAdapter.setFontSize(nameAdapter.calculateOptimalFontSize(nameRecyclerView));
            //给第一项和最后一项增加一个decoration修饰第一项和最后一项的顶部或底部的间距margin
            nameRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                    int itemPosition = parent.getChildAdapterPosition(view);
                    if (itemPosition == 0) {
                        params.setMargins(0, itemHeight, 0, 0);
                    } else if (itemPosition == names.size() - 1) {
                        params.setMargins(0, 0, 0, itemHeight);
                    } else {
                        params.setMargins(0, 0, 0, 0);
                    }
                    view.setLayoutParams(params);
                    super.getItemOffsets(outRect, view, parent, state);
                }
            });
        });

        //设置一个scroll监听器，在滚动后更新focusedPosition，保持聚焦到中间项
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            private final Handler scrollHandler = new Handler();

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollHandler.postDelayed(() -> {
                    int firstPosition = layoutManager.findFirstVisibleItemPosition();
                    System.out.println(firstPosition);
                    int lastPosition = layoutManager.findLastVisibleItemPosition();
                    System.out.println(lastPosition);
                    //判断是第一项在中间就设置focusedPosition为第一项的position为
                    if (firstPosition == 0 && lastPosition == 1) {
                        focusedPosition = firstPosition;
                    } else if (firstPosition == names.size() - 2 && lastPosition == names.size() - 1) {
                        //判断是最后一项在中间就设置focusedPosition为最后一项的position
                        focusedPosition = lastPosition;
                    } else {
                        //其余情况就设置focusedPosition为当前第一个可见项的position
                        focusedPosition = firstPosition + 1;
                    }
                    updateButtonState();
                    }, 100); // 限制为每 100ms 更新一次
            }

        };
        // 滚动监听，更新按钮状态
        nameRecyclerView.addOnScrollListener(onScrollListener);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(nameRecyclerView);

        // 初始化 RadioGroup
        radioGroup = view.findViewById(R.id.buttonGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!isUpdatingButtonSate) {
                if (checkedId == R.id.btnPresent) {
                    personList.get(focusedPosition).setStatus("Present");
                }
                else if (checkedId == R.id.btnAbsent) {
                    personList.get(focusedPosition).setStatus("Absent");
                }
                else if (checkedId == R.id.btnLeave) {
                    personList.get(focusedPosition).setStatus("Leave");
                }
                // 滚动到下一个姓名
                if (focusedPosition < names.size() - 1) {
                    nameRecyclerView.smoothScrollToPosition(layoutManager.findLastVisibleItemPosition());
                }
            }
        });

        Button lastButton = view.findViewById(R.id.last);
        lastButton.setOnClickListener(v -> {
            if (focusedPosition > 0) {
                nameRecyclerView.smoothScrollToPosition(layoutManager.findFirstVisibleItemPosition());
                updateButtonState();
            }
        });

        Button nextButton = view.findViewById(R.id.next);
        nextButton.setOnClickListener(v -> {
            if (focusedPosition < names.size() - 1) {
                nameRecyclerView.smoothScrollToPosition(layoutManager.findLastVisibleItemPosition());
                updateButtonState();
            }
        });
    }

    private void updateButtonState() {
        isUpdatingButtonSate = true;
        switch (personList.get(focusedPosition).getStatus()) {
            case "Present":
                radioGroup.check(R.id.btnPresent);
                break;
            case "Absent":
                radioGroup.check(R.id.btnAbsent);
                break;
            case "Leave":
                radioGroup.check(R.id.btnLeave);
                break;
            default:
                radioGroup.clearCheck();
        }
        isUpdatingButtonSate = false;
    }

    public void onPageSelected() {
        checkInViewModel.notifyDataUpdate();
        System.out.println("changed");
    }
}