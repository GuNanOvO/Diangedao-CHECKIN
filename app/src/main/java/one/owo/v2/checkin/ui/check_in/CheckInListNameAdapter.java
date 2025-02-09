package one.owo.v2.checkin.ui.check_in;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import one.owo.v2.checkin.R;
import one.owo.v2.checkin.sercices.Person;

public class CheckInListNameAdapter extends RecyclerView.Adapter<CheckInListNameAdapter.CheckInNameViewHolder> {

    private List<String> names;
    private final CheckInViewModel checkInViewModel;
    private float fontSize = 10;


    public CheckInListNameAdapter(List<String> names,CheckInViewModel checkInViewModel) {
        this.names = names;
        this.checkInViewModel = checkInViewModel;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CheckInNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_in_name_recycler, parent, false);
        return new CheckInNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckInNameViewHolder holder, int position) {
        int currentPosition = holder.getAdapterPosition();
        if (currentPosition == RecyclerView.NO_POSITION) return; // 防止无效位置

        String name = names.get(currentPosition);
        Person currentPerson = checkInViewModel.getPersonByName(name);
        boolean isUpdateButton = false;

        holder.nameView.setText(name);
        holder.nameView.setTextSize(fontSize);

        RadioGroup radioGroup = holder.radioGroup;
        RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.checkInPresentButton) currentPerson.setStatus("Present");
                else if (checkedId == R.id.checkInAbsentButton) currentPerson.setStatus("Absent");
                else if (checkedId == R.id.checkInLeaveButton) currentPerson.setStatus("Leave");
                System.out.println(currentPerson.getName() + checkedId);
            }
        };
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(checkedChangeListener);

        if (!currentPerson.getStatus().equals("Unchecked")) {
            radioGroup.setOnCheckedChangeListener(null);
            switch (currentPerson.getStatus()) {
                case "Present":
                    radioGroup.check(R.id.checkInPresentButton);
                    break;
                case "Absent":
                    radioGroup.check(R.id.checkInAbsentButton);
                    break;
                case "Leave":
                    radioGroup.check(R.id.checkInLeaveButton);
                    break;
            }
            isUpdateButton = true;
            radioGroup.setOnCheckedChangeListener(checkedChangeListener);
        }

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    static class CheckInNameViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        RadioGroup radioGroup;
        CheckInNameViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.checkInNameTextView);
            radioGroup = itemView.findViewById(R.id.checkInButtonGroup);
        }
    }

    public int calculateOptimalFontSize(RecyclerView nameRecyclerView) {
        float width = nameRecyclerView.getWidth();

        // 找到最长的名字
        String longestName = "";
        for (String name : names) {
            if (name.length() > longestName.length()) {
                longestName = name;
            }
        }

        // 测试字体大小的 TextView
        Paint paint = new Paint();
        int textSize = 50; // 起始字体大小

        // 动态调整字体大小，直到文字宽度接近屏幕宽度
        while (paint.measureText(longestName) > width) {
            textSize -= 1; // 缩小字体
            paint.setTextSize(textSize);
        }

        return textSize - 1; // 返回最优字体大小
    }
}
