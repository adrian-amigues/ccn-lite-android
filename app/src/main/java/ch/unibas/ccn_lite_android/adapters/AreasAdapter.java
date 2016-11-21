package ch.unibas.ccn_lite_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.unibas.ccn_lite_android.models.Area;
import ch.unibas.ccn_lite_android.R;
import ch.unibas.ccn_lite_android.models.AreaManager;
import ch.unibas.ccn_lite_android.models.SensorReading;
import ch.unibas.ccn_lite_android.activities.ChartTabsActivity_main;

/**
 *
 * Created by adrian on 2016-10-18.
 */

public class AreasAdapter extends RecyclerView.Adapter<AreasAdapter.AreaViewHolder>{

    private AreaManager areaManager;
    private List<Area> areas;
    private int mExpandedPosition = -1;
    private Context context;
    private RecyclerView rv;


//    private final String lowerColor = "#18DB52";
//    private final String middleColor = "#EDD20B";
//    private final String higherColor = "#E21414";
    private final List<Integer> bounds = new ArrayList<>(Arrays.asList(15, 20, 25, 30));

    public AreasAdapter(List<Area> areas, Context context){
        this.areas = areas;
        this.context = context;
    }
    public AreasAdapter(AreaManager areaManager, Context context){
        this.areaManager = areaManager;
        this.areas = areaManager.getAreas();
        this.context = context;
    }

    class AreaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView areaName;
//        TextView areaDescription;
        ImageView areaPhoto;
        ImageView areaSmiley;
        ImageView expandButton;
        ImageView predictionGraph;
        TextView hidden;
        Boolean isExpanded;

        AreaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            areaName = (TextView)itemView.findViewById(R.id.area_name);
//            areaDescription = (TextView)itemView.findViewById(R.id.area_description);
            areaPhoto = (ImageView)itemView.findViewById(R.id.area_photo);
            areaSmiley = (ImageView)itemView.findViewById(R.id.area_smiley);
//            expandButton = (ImageView)itemView.findViewById(R.id.expand);
            predictionGraph = (ImageView)itemView.findViewById(R.id.prediction_graph);
//            hidden = (TextView)itemView.findViewById(R.id.hidden);
            isExpanded = false;
            predictionGraph.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ChartTabsActivity_main.class);
            context.startActivity(intent);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.rv = recyclerView;
    }

    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new AreaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AreaViewHolder holder, int position) {
        Area area = areas.get(position);
        holder.areaName.setText(area.getName());
//        holder.areaDescription.setText(area.getCurrentValue());
//        holder.areaDescription.setTextColor(getTextColor(area.getCurrentValue()));
        holder.areaPhoto.setImageResource(area.getPhotoId());
        holder.areaSmiley.setImageResource(getSmiley(area.getCurrentValue()));

        final boolean isExpanded = position == mExpandedPosition;
        holder.predictionGraph.setVisibility(isExpanded? View.VISIBLE:View.GONE);
//        if (isExpanded) {
//            holder.predictionGraph.setVisibility(View.VISIBLE);
//            holder.expandButton.setImageResource(R.drawable.ic_expand_less_black_48dp);
//        } else {
//            holder.predictionGraph.setVisibility(View.GONE);
//            holder.expandButton.setImageResource(R.drawable.ic_expand_more_black_48dp);
//        }
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:holder.getAdapterPosition();
                TransitionManager.beginDelayedTransition(rv);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }

    public String getURI(int i) {
        Area area = areas.get(i);
        return area.getUri();
    }

    public void updateValue(int i, String newValue) {
        Area area= areas.get(i);
        area.setDescription(newValue);
    }

    public void updateValue(int i, SensorReading sr) {
        Area area= areas.get(i);
        int light = Integer.parseInt(sr.getLight()) / 4;
        area.setDescription(Integer.toString(light)); //TODO: Deprecated
        area.setCurrentValue(Integer.toString(light));
    }

//    private int getTextColor(String valueStr) {
//        try {
//            String colorString;
//            int v = Integer.parseInt(valueStr);
//            if (v < lowerBound) {
//                colorString = lowerColor;
//            } else if (v > upperBound) {
//                colorString = higherColor;
//            } else {
//                colorString = middleColor;
//            }
//            return Color.parseColor(colorString);
//        } catch(Exception e) {
//            return Color.BLACK;
//        }
//    }

    private int getSmiley(String valueStr) {
        try {
            int v = Integer.parseInt(valueStr);
            if (v < bounds.get(0)) {
                return R.drawable.face1;
            } else if (v < bounds.get(1)) {
                return R.drawable.face2;
            } else if (v < bounds.get(2)) {
                return R.drawable.face3;
            } else if (v < bounds.get(3)) {
                return R.drawable.face4;
            } else {
                return R.drawable.face5;
            }
        } catch(Exception e) {
            return R.drawable.ic_sync_problem_black_48dp;
        }
    }

    public void resetExpandedPosition() {
        mExpandedPosition = -1;
    }

    public void sortAreas() {
        Collections.sort(areas, new AreaComparator());
    }

    private class AreaComparator implements Comparator<Area> {
        @Override
        public int compare(Area a1, Area a2) {
            try {
                int value1 = Integer.parseInt(a1.getCurrentValue());
                int value2 = Integer.parseInt(a2.getCurrentValue());
                return value1 - value2;
            } catch (Exception e) {
                return a1.getCurrentValue().compareTo(a2.getCurrentValue());
            }
        }
    }
}
