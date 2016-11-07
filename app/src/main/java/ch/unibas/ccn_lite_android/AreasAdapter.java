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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by adrian on 2016-10-18.
 */

public class AreasAdapter extends RecyclerView.Adapter<AreasAdapter.AreaViewHolder>{

    private List<Area> areas;
    private int mExpandedPosition = -1;
    private Context context;
    private RecyclerView rv;

    private final String lowerColor = "#18DB52";
    private final String middleColor = "#EDD20B";
    private final String higherColor = "#E21414";
    private final int lowerBound = 15;
    private final int upperBound = 25;
    private final List<Integer> bounds = new ArrayList<>(Arrays.asList(15, 20, 25, 30));

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
        ImageView expandButton;
        ImageView predictionGraph;
        TextView hidden;
        Boolean isExpanded;

        AreaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            areaName = (TextView)itemView.findViewById(R.id.area_name);
            areaDescription = (TextView)itemView.findViewById(R.id.area_description);
            areaPhoto = (ImageView)itemView.findViewById(R.id.area_photo);
            areaSmiley = (ImageView)itemView.findViewById(R.id.area_smiley);
            expandButton = (ImageView)itemView.findViewById(R.id.expand);
            predictionGraph = (ImageView)itemView.findViewById(R.id.prediction_graph);
            hidden = (TextView)itemView.findViewById(R.id.hidden);
            isExpanded = false;
            predictionGraph.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(context, "Go to graph", Toast.LENGTH_SHORT).show();
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
        holder.areaDescription.setText(area.getDescription());
        holder.areaDescription.setTextColor(getTextColor(area.getDescription()));
        holder.areaPhoto.setImageResource(area.getPhotoId());
        holder.areaSmiley.setImageResource(getSmiley(area.getDescription()));

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
        String uri = area.getUri();
//        area.increaseValueCounter();
        return uri;
    }

    public void updateValue(int i, String newValue) {
        Area area= areas.get(i);
        area.setDescription(newValue);
    }

    public void updateValue(int i, SensorReading sr) {
        Area area= areas.get(i);
        int light = Integer.parseInt(sr.getLight()) / 4;
        area.setDescription(Integer.toString(light));
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

    private int getSmiley(String desc) {
        try {
            int value = Integer.parseInt(desc);
            if (value < bounds.get(0)) {
                return R.drawable.face1;
            } else if (value < bounds.get(1)) {
                return R.drawable.face2;
            } else if (value < bounds.get(2)) {
                return R.drawable.face3;
            } else if (value < bounds.get(3)) {
                return R.drawable.face4;
            } else {
                return R.drawable.face5;
            }
        } catch(Exception e) {
            return R.drawable.ic_sync_problem;
        }
    }

    public void sortAreas() {
        Collections.sort(areas, new AreaComparator());
    }

    public void resetExpandedPosition() {
        mExpandedPosition = -1;
    }
}
