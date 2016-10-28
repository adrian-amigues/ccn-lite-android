package ch.unibas.ccn_lite_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 *
 * Created by adrian on 2016-10-18.
 */

public class AreasAdapter extends RecyclerView.Adapter<AreasAdapter.AreaViewHolder>{

    private List<Area> areas;
    private int mExpandedPosition = -1;
    private Context context;

    private final String lowerColor = "#18DB52";
    private final String middleColor = "#EDD20B";
    private final String higherColor = "#E21414";
    private final int lowerBound = 15;
    private final int upperBound = 25;

    AreasAdapter(List<Area> areas, Context context){
        this.areas = areas;
        this.context = context;
    }

    public class AreaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView areaName;
        TextView areaDescription;
        ImageView areaPhoto;
        ImageView areaSmiley;
        TextView hidden;
        RecyclerView rv;
        Boolean isExpanded;

        AreaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            areaName = (TextView)itemView.findViewById(R.id.area_name);
            areaDescription = (TextView)itemView.findViewById(R.id.area_description);
            areaPhoto = (ImageView)itemView.findViewById(R.id.area_photo);
            areaSmiley = (ImageView)itemView.findViewById(R.id.area_smiley);
            hidden = (TextView)itemView.findViewById(R.id.hidden);
            rv = (RecyclerView) itemView.findViewById(R.id.rv);
            isExpanded = false;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Go to graph", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, ChartTabsActivity_main.class);
            context.startActivity(intent);
        }
    }

    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new AreaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AreaViewHolder holder, int position) {
        Area area = areas.get(position);
        holder.areaName.setText(area.getName());
        holder.areaDescription.setText(area.getDescription());
        holder.areaDescription.setTextColor(getTextColor(area.getDescription()));
        holder.areaPhoto.setImageResource(area.getPhotoId());
        holder.areaSmiley.setImageResource(area.getSmileyId());

//        final boolean isExpanded = position==mExpandedPosition;
//        holder.hidden.setVisibility(isExpanded?View.VISIBLE:View.GONE);
//        holder.cv.setActivated(isExpanded);
//        holder.cv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
////                RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);
//                RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv);
//                mExpandedPosition = isExpanded ? -1:holder.getAdapterPosition();
//                TransitionManager.beginDelayedTransition(rv);
//                notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }

    public String getURI(int i) {
        Area area = areas.get(i);
        String uri = area.getUri();
        area.increaseValueCounter();
        return uri;
    }

    public void updateValue(int i, String newValue) {
        Area area= areas.get(i);
        area.setDescription(newValue);
//       TODO: if too slow move line to taskFinished in WorkCounter
        notifyDataSetChanged();
    }

    private int getTextColor(String desc) {
        try {
            String colorString;
            int value = Integer.parseInt(desc);
            if (value < lowerBound) {
                colorString = lowerColor;
            } else if (value > upperBound) {
                colorString = higherColor;
            } else {
                colorString = middleColor;
            }
            return Color.parseColor(colorString);
        } catch(Exception e) {
            return Color.BLACK;
        }
    }
}
