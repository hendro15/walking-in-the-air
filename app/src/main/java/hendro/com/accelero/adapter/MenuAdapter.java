package hendro.com.accelero.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import hendro.com.accelero.R;

import java.util.List;

import hendro.com.accelero.model.MenuElement;
import me.grantland.widget.AutofitTextView;

/**
 * Created by Hendro E. Prabowo on 02/04/2017.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private Context mContext;
    private List<MenuElement> menuElements;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public AutofitTextView tv_title;
        public ImageView iv_icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_title = (AutofitTextView) itemView.findViewById(R.id.tv_title);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
        }
    }

    public MenuAdapter(Context mContext, List<MenuElement> menuElements){
        this.mContext = mContext;
        this.menuElements = menuElements;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemVIew = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new MyViewHolder(itemVIew);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MenuElement myMenu = menuElements.get(position);
        holder.tv_title.setText(myMenu.getTitle());
        Glide.with(mContext).load(myMenu.getIcon()).into(holder.iv_icon);
    }

    @Override
    public int getItemCount() {
        return menuElements.size();
    }

}
