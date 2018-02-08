package com.miguel.gestorincidenciaapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailedIssueFragment extends Fragment {

    private TicketJsonBuilder object;
    private EditText title;
    private EditText date;
    private EditText status;
    private EditText urgency;
    private EditText priority;
    private EditText description;


    public DetailedIssueFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_detailed_issue, container, false);

        title = inflate.findViewById(R.id.txtTitleIssue);
        date = inflate.findViewById(R.id.dateIssue);
        status = inflate.findViewById(R.id.status);
        urgency = inflate.findViewById(R.id.txtUrgency);
        priority = inflate.findViewById(R.id.txtPriority);
        description = inflate.findViewById(R.id.description);

        Intent i = getActivity().getIntent();
        if(i != null) {

            object = (TicketJsonBuilder) i.getSerializableExtra("issueSelected");

            title.setText(object.getName());
            date.setText(object.getDate());
//            status.setText(statusI);
//            urgency.setText(urgencyI);
//            priority.setText(priorityI);
            description.setText(object.getContent());


        }

        return inflate;
    }
}