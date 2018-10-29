package com.example.jarvis.sproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Objects;

import Helper.FileManagerAdapter;
import Model.File;

public class ManagerFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    File[] files = {new File("Android", "dir", "04/08/2016", 539.65), new File("resume", "doc", "03/09/2018", 0.10123), new File("My Pic", "image", "03/09/2018", 1.66), new File("Shape of you", "audio", "25/07/2017", 11.9936), new File("Interstellar", "video", "17/12/2015", 1228.8), new File("Android", "dir", "04/08/2016", 539.65), new File("resume", "doc", "03/09/2018", 0.10123), new File("My Pic", "image", "03/09/2018", 1.66), new File("Shape of you", "audio", "25/07/2017", 11.9936), new File("Interstellar", "video", "17/12/2015", 1228.8), new File("Android", "dir", "04/08/2016", 539.65), new File("resume", "doc", "03/09/2018", 0.10123), new File("My Pic", "image", "03/09/2018", 1.66), new File("Shape of you", "audio", "25/07/2017", 11.9936), new File("Interstellar", "video", "17/12/2015", 1228.8), new File("Android", "dir", "04/08/2016", 539.65), new File("resume", "doc", "03/09/2018", 0.10123), new File("My Pic", "image", "03/09/2018", 1.66), new File("Shape of you", "audio", "25/07/2017", 11.9936), new File("Interstellar", "video", "17/12/2015", 1228.8)};
    String[] storageTypes = {"Internal", "SD Card"};
    TextView storageLocation;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager, container, false);

        GridView mainHolder = (GridView) view.findViewById(R.id.main_container);
        FileManagerAdapter fileManagerAdapter = new FileManagerAdapter(getContext(), files);
        mainHolder.setAdapter(fileManagerAdapter);

        //multiple item selection
        mainHolder.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mainHolder.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        //storage location bar
        storageLocation = (TextView) view.findViewById(R.id.storage_location);
        //storage type sppiner
        Spinner storageSpinner = (Spinner) view.findViewById(R.id.storage_type_spinner);
        storageSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> aa = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.support_simple_spinner_dropdown_item, storageTypes);
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        storageSpinner.setAdapter(aa);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        storageLocation.setText(storageTypes[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
