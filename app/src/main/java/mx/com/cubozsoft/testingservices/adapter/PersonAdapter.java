package mx.com.cubozsoft.testingservices.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import mx.com.cubozsoft.testingservices.Person;
import mx.com.cubozsoft.testingservices.R;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder>{
    private final String LOG_TAG = PersonAdapter.class.getSimpleName();
    private List<Person> mDataSet = new ArrayList<>();
    private Context mContext;

    public PersonAdapter(List<Person> mDataSet, Context context) {
        this.mDataSet = mDataSet;
        this.mContext = context;
    }

    //it is used by the layout manager. simply create the object of the view holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);

        return new ViewHolder(item);
    }

    //it is used byt the layout manager to replace the data
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(mDataSet.get(position).getName());
//        holder.mImageView.setImageResource(mDataSet.get(position).getIdPicture());
        holder.mImageView.setImageBitmap(getImage(mDataSet.get(position).getIdPicture(),mContext));
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.v(LOG_TAG,"Im tapping");
//                mListener.ClickOnItemList(mDataSet.get(position));
//            }
//        });
    }

    public Bitmap getImage(String fileName, Context context){
        ContextWrapper cw = new ContextWrapper(context);
        try{
            File file = new File(cw.getFilesDir(), fileName);
            return BitmapFactory.decodeStream(new FileInputStream(file));
        }catch (Exception e){
            return null;
        }
    }

    //it is used by the layout manager
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;


        public ViewHolder(View parent) {
            super(parent);
            mTextView = (TextView)parent.findViewById(R.id.textViewPerson);
            mImageView = (ImageView)parent.findViewById(R.id.photoPerson);
        }
    }

}