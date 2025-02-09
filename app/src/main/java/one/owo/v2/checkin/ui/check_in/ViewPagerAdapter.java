package one.owo.v2.checkin.ui.check_in;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<Fragment> fragments;
    public ViewPagerAdapter(@NonNull Fragment fragment, List<Fragment> fragments) {
        super(fragment);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
