package navigate.inside.Logic;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import navigate.inside.Activities.PlaceViewActivity;
import navigate.inside.Objects.Node;
import navigate.inside.R;


public class PageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<Pair<Node,Integer>> itemList;

    public PageAdapter(Context context, ArrayList<Pair<Node,Integer>> itemlist)  {
        mContext = context;
        this.itemList = itemlist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.place_list_line_item, parent, false);
        return new PageAdapter.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder)holder).bind(position);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public ListViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.node_name);
        }

        public void bind(final int position) {

           name.setText(String.valueOf(itemList.get(position).first.get_id()));
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PlaceViewActivity)mContext).setPage(position);
                }
            });
        }
    }

}




