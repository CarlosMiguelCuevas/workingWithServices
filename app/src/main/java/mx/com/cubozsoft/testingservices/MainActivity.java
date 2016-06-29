package mx.com.cubozsoft.testingservices;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mx.com.cubozsoft.testingservices.adapter.PersonAdapter;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mManager;
    List<Person> mDataList;
    Button add_button;
    public static final String SaveArray = "saveArrayPerson";

    private BroadcastReceiver bredcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Parcelable obj = extras.getParcelable(Person.DATAPARCELABLE);

            showData((Person) obj);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(HelloIntentService.BRADCAST_ACTION);

        registerReceiver(bredcastReceiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bredcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(SaveArray, (ArrayList<? extends Parcelable>) mDataList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null)
        {
            mDataList = new ArrayList<>();
        }
        else
        {
            mDataList = savedInstanceState.getParcelableArrayList(SaveArray);
        }


        mManager = new LinearLayoutManager(this);
        mAdapter = new PersonAdapter(mDataList,this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_person);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);

        add_button = (Button)findViewById(R.id.button_add);

        add_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HelloIntentService.class);
                startService(intent);
            }
        });



    }

    public void showData(Person data){
        Log.v("Si lllego esta amder", data.getName());
        mDataList.add(data);
        mAdapter.notifyDataSetChanged();
    }
}
