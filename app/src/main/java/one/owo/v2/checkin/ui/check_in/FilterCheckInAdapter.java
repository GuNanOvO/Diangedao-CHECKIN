package one.owo.v2.checkin.ui.check_in;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import one.owo.v2.checkin.R;
import one.owo.v2.checkin.sercices.Person;

public class FilterCheckInAdapter extends RecyclerView.Adapter<FilterCheckInAdapter.FilterCheckInViewHolder> {
    private List<Person> personList;
    private List<String> items;


    public FilterCheckInAdapter(List<Person> personList, List<String> items) {
        this.personList = personList;
        this.items = items;
    }

    @NonNull
    @Override
    public FilterCheckInViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_name_group, parent, false);
        return new FilterCheckInViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterCheckInViewHolder holder, int position) {
        int currentPosition = holder.getAdapterPosition();
        if (currentPosition == RecyclerView.NO_POSITION) return; // 防止无效位置

        holder.filterGroupName.setText(items.get(currentPosition));

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class FilterCheckInViewHolder extends RecyclerView.ViewHolder{
        TextView filterGroupName;
        Button btnSignAll;
        Button btnSignReverse;
        Button btnClear;

        public FilterCheckInViewHolder(@NonNull View itemView) {
            super(itemView);

            filterGroupName = itemView.findViewById(R.id.filterGroupName);
            btnSignAll = itemView.findViewById(R.id.btnSignAll);
            btnSignReverse = itemView.findViewById(R.id.btnSignReverse);
            btnClear = itemView.findViewById(R.id.btnClear);
        }
    }
}
