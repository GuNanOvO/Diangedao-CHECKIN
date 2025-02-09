package one.owo.v2.checkin.ui.check_in;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalDecoration extends RecyclerView.ItemDecoration {
    private int space = 0;
    private int distance = 0;
    private static final String TAG = "VerticalDecoration";

    public VerticalDecoration(int space) {
        this.space = space;
    }

    /**
    * @param view 子视图
    * @param parent RecyclerView
    * */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

        if (distance == 0) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    setDistance(parent, view);
                }
            });
        }

        //params.setMarginStart();
    }

    public void setDistance(RecyclerView recyclerView, View view) {
        recyclerView.post(() -> {
            distance = recyclerView.getHeight() / 2 - view.getHeight() / 2;
        });
    }
}
