package one.owo.v2.checkin.ui.check_in;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import one.owo.v2.checkin.R;

public class NameAdapter extends RecyclerView.Adapter<NameAdapter.NameViewHolder> {

    private final List<String> names;
    private float fontSize = 10;
    private int itemHeight = 0;

    public NameAdapter(List<String> names) {
        this.names = names;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        notifyDataSetChanged();
    }

    public void setItemHeight(int height) {
        this.itemHeight = height;
        notifyDataSetChanged(); // 通知更新
    }

    @NonNull
    @Override
    public NameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name, parent, false);
        return new NameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NameViewHolder holder, int position) {
        holder.nameTextView.setText(names.get(position));
        holder.nameTextView.setTextSize(fontSize);

        if (itemHeight > 0) {
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height = itemHeight;
            holder.itemView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
    static class NameViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        NameViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
    public int calculateOptimalFontSize(RecyclerView nameRecyclerView) {
        float width = nameRecyclerView.getWidth();

        System.out.println(width);
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
