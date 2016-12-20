package ch.unibas.ccn_lite_android.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.unibas.ccn_lite_android.activities.CcnLiteAndroid;
import ch.unibas.ccn_lite_android.activities.HistorySearch;
import ch.unibas.ccn_lite_android.models.DatabaseTable;
import ch.unibas.ccn_lite_android.models.Prediction;
import ch.unibas.ccn_lite_android.models.Area;
import ch.unibas.ccn_lite_android.R;
import ch.unibas.ccn_lite_android.models.AreaManager;
import ch.unibas.ccn_lite_android.models.Sensor;
import ch.unibas.ccn_lite_android.models.SensorReading;

/**
 *
 * Created by adrian on 2016-10-18.
 */

public class AreasAdapter extends RecyclerView.Adapter<AreasAdapter.AreaViewHolder>
{
    private String TAG = "unoise";
    private AreaManager areaManager;
    private int mExpandedPosition = -1;
    private LineChart expandedChart;
    private Context context;
    private RecyclerView rv;
    private OnItemClickListener listener;
    private DatabaseTable dbTable;
    private Prediction prediction;

    private final List<Integer> bounds = new ArrayList<>(Arrays.asList(15, 20, 25, 30));

    public AreasAdapter(AreaManager areaManager, Context context, DatabaseTable dbTable){
        this.areaManager = areaManager;
        this.context = context;
        this.dbTable = dbTable;

    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class AreaViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView areaName;
        ImageView areaPhoto;
        ImageView areaSmiley;
        ImageView predictionGraph;
        Boolean isExpanded;
        LinearLayout expandedPart;
        LinearLayout sensorList;
        TextView pastChart;
        LineChart predictionChart;


        AreaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            areaName = (TextView)itemView.findViewById(R.id.area_name);
            areaPhoto = (ImageView)itemView.findViewById(R.id.area_photo);
            areaSmiley = (ImageView)itemView.findViewById(R.id.area_smiley);
//            predictionGraph = (ImageView)itemView.findViewById(R.id.prediction_graph);
            expandedPart = (LinearLayout)itemView.findViewById(R.id.expanded_part);
            sensorList = (LinearLayout)itemView.findViewById(R.id.sensor_list);
//            pastChart = (TextView)itemView.findViewById(R.id.past_chart_link);
            isExpanded = false;
            predictionChart = (LineChart) itemView.findViewById(R.id.predictionChart);
//            pastChart.setOnClickListener(this);
            areaPhoto.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                    startActivityForResultFunction(position);
                }
            });
        }

//        @Override
//        public void onClick(View v) {
////            ((CcnLiteAndroid) context).launchHistoryActivity(v);
////            Intent intent = new Intent(context, ChartTabsActivity_main.class);
//            Intent intent = new Intent(context, HistorySearch.class);
//            context.startActivity(intent);
//        }
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
        final Area area = areaManager.getAreas().get(position);
        holder.areaName.setText(area.getName());
        holder.areaPhoto.setImageResource(area.getPhotoId());
        holder.areaPhoto.setImageBitmap(area.getBitmap());
        holder.areaSmiley.setImageResource(getSmiley(area.getSmileyValue()));

        final boolean isExpanded = position == mExpandedPosition;
        holder.expandedPart.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    mExpandedPosition = -1;
                    expandedChart = null;
                } else {
                    mExpandedPosition = holder.getAdapterPosition();
                    expandedChart = holder.predictionChart;
                    ((CcnLiteAndroid) context).refreshPrediction(area);
                    ((CcnLiteAndroid) context).refreshHistory(area);
//                    prediction.makepredictionGraph(predictionChart);
                }
//                mExpandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
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
                    TextView humidity = (TextView)readingsList.findViewById(R.id.card_item_sensor_humidity);

                    TextView historyLink = (TextView)readingsList.
                            findViewById(R.id.history_link);
                    sensorName.setText(s.getUriWithSeqno());
                    light.setText(s.printLight());
                    temperature.setText(s.printTemperature());
                    humidity.setText(s.printHumidity());

                    final Sensor finalS = s;
                    historyLink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, HistorySearch.class);
                            intent.putExtra("SENSOR", finalS);
                            context.startActivity(intent);
                        }
                    });

                    holder.sensorList.addView(readingsList);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return areaManager.getAreas().size();
    }

//    public String getURI(int i) {
//        Area area = areaManager.getAreas().get(i);
//        if (area.isDeprecatedArea()) {
//            return area.getUriWithSeqno();
//        } else {
//            return area.getSensor(0).getUriWithSeqno(); //TODO: update to not only get the first URI
//        }
//    }

    public void updateValue(int areaPos, int sensorPos, String newValue) {
        Area area= areaManager.getAreas().get(areaPos);
        Sensor sensor = area.getSensor(sensorPos);
//        area.setDescription(newValue);
//        area.setSmileyValue(newValue);
        sensor.setAvailable(false);
    }


    public void updateValue(int areaPos, int sensorPos, SensorReading sr) {
        Area area = areaManager.getAreas().get(areaPos);
        Sensor sensor = area.getSensor(sensorPos);
        sensor.setAvailable(true);
        sensor.updateValues(sr);
        float smileyValue = Float.parseFloat(sr.getLight()) / 4;
        area.setSmileyValue(Float.toString(smileyValue));
//        area.setDescription(Float.toString(smileyValue)); //TODO: Deprecated
//        area.setSmileyValue(Float.toString(smileyValue));
        sr.updateSensorValues();
    }

    //Updates the image of an area
    public void updateImage(int i, Bitmap newImage) {
        Area area= areaManager.getAreas().get(i);
        area.setBitmap(newImage);
    }

    private int getSmiley(String valueStr) {
        try {
            float v = Float.parseFloat(valueStr);
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
        expandedChart = null;
    }

    //Opens an alertDialog that enable users to choose any image or delete an available image in a specific area specified with "position"
    private void selectImage(final int position) {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery",
                "Delete photo" };
        String userChoosenTask;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add or Delete a Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Gallery")) {
                    galleryIntent();
                }else if (items[item].equals("Delete photo")) {
                    Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.take_photo_thumbnail);
                    String areaName = areaManager.getAreas().get(position).getName();
                    dbTable.deleteFromTable(areaName);
                    updateImage(position, icon);
                    notifyItemChanged(position);
                }
            }
        });
        builder.show();
    }

    //Provides the camera facility of the user's cell phone
    public void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((Activity) context).startActivityForResult(intent, 1);
    }

    //Provides a facility for users to pick up any image from the SD card
    public void galleryIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);//
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select File"),2);
    }

    //Whenever user clicks on an image in an area, this function is called and cal selectImage function
    public void startActivityForResultFunction(int position){
        selectImage(position);
    }

    public void updatePredictionGraph(Prediction p) {
        if (expandedChart != null) {
            p.makepredictionGraph(expandedChart);
        }
    }
}
