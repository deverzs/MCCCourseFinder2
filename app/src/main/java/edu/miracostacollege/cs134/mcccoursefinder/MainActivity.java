package edu.miracostacollege.cs134.mcccoursefinder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import edu.miracostacollege.cs134.mcccoursefinder.model.Course;
import edu.miracostacollege.cs134.mcccoursefinder.model.DBHelper;
import edu.miracostacollege.cs134.mcccoursefinder.model.Instructor;
import edu.miracostacollege.cs134.mcccoursefinder.model.Offering;

public class MainActivity extends AppCompatActivity {

    private DBHelper db;
    private static final String TAG = "MCC Course Finder";

    private List<Instructor> allInstructorsList;
    private List<Course> allCoursesList;
    private List<Offering> allOfferingsList;
    private List<Offering> filteredOfferingsList;

    private EditText courseTitleEditText;
    private Spinner instructorSpinner;
    private ListView offeringsListView;
    private  OfferingListAdapter offeringsListAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteDatabase(DBHelper.DATABASE_NAME);
        db = new DBHelper(this);
        db.importCoursesFromCSV("courses.csv");
        db.importInstructorsFromCSV("instructors.csv");
        db.importOfferingsFromCSV("offerings.csv");

        allCoursesList = db.getAllCourses();
        allInstructorsList = db.getAllInstructors();
        allOfferingsList = db.getAllOfferings();
        filteredOfferingsList = new ArrayList<>(allOfferingsList) ;
        courseTitleEditText = findViewById(R.id.courseTitleEditText);
        courseTitleEditText.addTextChangedListener(courseTitleTextWatcher);

        //wire up offeringsListView
        offeringsListView = findViewById(R.id.offeringsListView) ;
        //instantiate list view
        offeringsListAdapter = new OfferingListAdapter(this, R.layout.offering_list_item, filteredOfferingsList) ;
        //list view with list adapter
        offeringsListView.setAdapter(offeringsListAdapter);

        //TODO (1): Construct instructorSpinnerAdapter using the method getInstructorNames()
        //TODO: to populate the spinner.

        //wire up spinner
        instructorSpinner = findViewById(R.id.instructorSpinner) ;
        //have option for simple text or custom layout files such as image
        // and stuff in a custom layout file or rating bar

        //if custom layout for spinner, we'd add it for second paramter else use built in layout
        // method is used to populate spinner
        ArrayAdapter<String> instructorSpinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getInstuctorNames());

        instructorSpinner.setAdapter(instructorSpinnerAdapter);

        //event to handle everyone's name to select the chosen instructor
        //from offering class (the relationship table) every offering knows the course and the instuctor
        //loop though offering and pick the ones that have it and recreate
        //clicked nery, nery is tring
        //loop through offeringList and if instructor name equals then add to list
        instructorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //selected instuctor will show all - same as reset button - back to way it was
                //selected particular instructor show the particular list
                //get selected instrctor name

                //if positin is zero, call reset and exit
                if(position == 0)
                {
                    reset(view);
                    return;
                }
                String selectedName = String.valueOf(adapterView.getItemAtPosition(position));
                //clear list adapter for offerings
                //when clear offerlistAdapter will clear allOfferingList - all data is gone
                //so need dumplicate copy
                offeringsListAdapter.clear();
                //rebuild with new chosen courses by instructor selected
                //offerings lise
                for (int i = 0; i < allOfferingsList.size(); i++) {
                    if(allOfferingsList.get(i).getInstructor().getFullName().equals(selectedName))
                    {
                        offeringsListAdapter.add(allOfferingsList.get(i));
                    }

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    public String[] getInstuctorNames()
    {
        String[] names = new String[allInstructorsList.size() + 1] ; //size of 14 + 1 (for blank)
        names[0] = "[select instructor]"; //better to put into string.xml
        for (int i = 1; i < names.length  ; i++) {
            names[i]  = allInstructorsList.get(i-1).getFullName() ;
        }
        return  names;

    }
    //TODO (2): Create a method getInstructorNames that returns a String[] containing the entry
    //TODO: "[SELECT INSTRUCTOR]" at position 0, followed by all the full instructor names in the
    //TODO: allInstructorsList


    //TODO (3): Create a void method named reset that sets the test of the edit text back to an
    //TODO: empty string, sets the selection of the Spinner to 0 and clears out the offeringListAdapter,
    //TODO: then rebuild it with the allOfferingsList


    public void reset(View v)
    {
        //clear everything out
        //reset edit text
        courseTitleEditText.setText("");

        instructorSpinner.setSelection(0);

        //clear out offering and rebuild
        offeringsListAdapter.clear();
        for (Offering o: allOfferingsList)
        {
         offeringsListAdapter.add(o);
        }
    }

    //TODO (4): Create a TextWatcher named courseTitleTextWatcher that will implement the onTextChanged method.
    //TODO: In this method, set the selection of the instructorSpinner to 0, then
    //TODO: Clear the offeringListAdapter
    //TODO: If the entry is an empty String "", the offeringListAdapter should addAll from the allOfferingsList
    //TODO: Else, the offeringListAdapter should add each Offering whose course title starts with the entry.

    //listen to edit text using textWatcher

    public TextWatcher courseTitleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence text, int i, int i1, int i2) {
            //text is charsequence - rename text
            //grab text
            String cleanText =  text.toString().toLowerCase() ;
            if(!cleanText.isEmpty())
            {
                offeringsListAdapter.clear();
                //loop through offerings and if it contains clean text add to listAdapter
                for (Offering o : allOfferingsList)
                {
                    Course c = o.getCourse();
                    if(c.getFullName().toLowerCase().contains(cleanText))
                    {
                        offeringsListAdapter.add(o);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    } ;


    //TODO (5): Create an AdapterView.OnItemSelectedListener named instructorSpinnerListener and implement
    //TODO: the onItemSelected method to do the following:
    //TODO: If the selectedInstructorName != "[Select Instructor]", clear the offeringListAdapter,
    //TODO: then rebuild it with every Offering that has an instructor whose full name equals the one selected.
}
