package ch.unibas.ccn_lite_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.unibas.ccn_lite_android.models.Area;
import ch.unibas.ccn_lite_android.R;
import ch.unibas.ccn_lite_android.models.AreaManager;
import ch.unibas.ccn_lite_android.models.Sensor;
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
        ImageView areaPhoto;
        ImageView areaSmiley;
        ImageView predictionGraph;
        TextView hidden;
        Boolean isExpanded;
        LinearLayout expandedPart;
        LinearLayout sensorList;

        AreaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            areaName = (TextView)itemView.findViewById(R.id.area_name);
            areaPhoto = (ImageView)itemView.findViewById(R.id.area_photo);
            areaSmiley = (ImageView)itemView.findViewById(R.id.area_smiley);
            predictionGraph = (ImageView)itemView.findViewById(R.id.prediction_graph);
//            hidden = (TextView)itemView.findViewById(R.id.hidden);
            expandedPart = (LinearLayout)itemView.findViewById(R.id.expanded_part);
            sensorList = (LinearLayout)itemView.findViewById(R.id.sensor_list);
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item, viewGroup, false);
        return new AreaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AreaViewHolder holder, int position) {
        Area area = areaManager.getAreas().get(position);
        holder.areaName.setText(area.getName());
        holder.areaPhoto.setImageResource(area.getPhotoId());
        holder.areaSmiley.setImageResource(getSmiley(area.getCurrentValue()));

        final boolean isExpanded = position == mExpandedPosition;
        holder.expandedPart.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
                TransitionManager.beginDelayedTransition(rv);
                notifyDataSetChanged();
            }
        });

        holder.sensorList.removeAllViews();
        if (isExpanded) {
            if (area.getSensors().size() == 0) {
                TextView v = new TextView(context);
                v.setText("No sensors to list");
                holder.sensorList.addView(v);
            } else {
                LayoutInflater inflater = LayoutInflater.from(context);
                for (int i = 0; i < area.getSensors().size(); i++) {
                    Sensor s = area.getSensor(i);
                    LinearLayout readingsList = (LinearLayout) inflater.inflate(R.layout.card_item_sensor, null);
                    TextView sensorName = (TextView)readingsList.findViewById(R.id.card_item_sensor_name);
                    TextView light = (TextView)readingsList.findViewById(R.id.card_item_sensor_light);
                    TextView temperature = (TextView)readingsList.findViewById(R.id.card_item_sensor_temperature);
                    sensorName.setText(s.getUri());
                    light.setText(s.getLight());
                    temperature.setText(s.getTemperature());

                    holder.sensorList.addView(readingsList);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return areaManager.getAreas().size();
    }

    public String getURI(int i) {
        Area area = areaManager.getAreas().get(i);
        if (area.isDeprecatedArea()) {
            return area.getUri();
        } else {
            return area.getSensor(0).getUri(); //TODO: update to not only get the first URI
        }
    }

    public void updateValue(int i, String newValue) {
        Area area= areaManager.getAreas().get(i);
        area.setDescription(newValue);
    }

    public void updateValue(int i, SensorReading sr) {
        Area area= areaManager.getAreas().get(i);
        int light = Integer.parseInt(sr.getLight()) / 4;
        area.setDescription(Integer.toString(light)); //TODO: Deprecated
        area.setCurrentValue(Integer.toString(light));
        area.updateSensors(sr);
    }

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
}
