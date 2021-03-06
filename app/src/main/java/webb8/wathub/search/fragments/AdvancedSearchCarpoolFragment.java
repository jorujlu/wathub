package webb8.wathub.search.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import webb8.wathub.R;
import webb8.wathub.hub.fragments.PostFeedFragment;
import webb8.wathub.hub.fragments.actions.ActionPostCarpoolFragment;
import webb8.wathub.models.Carpool;
import webb8.wathub.models.Post;
import webb8.wathub.util.NavItem;
import webb8.wathub.util.PostCard;

/**
 * Created by mismayil on 3/24/16.
 */
public class AdvancedSearchCarpoolFragment extends AdvancedSearchFragment {

    // UI fields
    private EditText mCarpoolFromView;
    private EditText mCarpoolToView;
    private EditText mCarpoolWhenView;
    private EditText mCarpoolMinPassengerView;
    private EditText mCarpoolMaxPassengerView;
    private EditText mCarpoolMinPriceView;
    private EditText mCarpoolMaxPriceView;
    private RelativeLayout mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View actionSearchView = inflater.inflate(R.layout.fragment_advanced_search, container, false);
        final View actionSearchCarpoolView = inflater.inflate(R.layout.fragment_advanced_search_carpool, container, false);
        mActionSearchContainer = (FrameLayout) actionSearchView.findViewById(R.id.advanced_search_container);

        mActionSearchContainer.addView(actionSearchCarpoolView);
        mProgressBar = (RelativeLayout) actionSearchView.findViewById(R.id.progress_bar);

        mContentView = (EditText) actionSearchView.findViewById(R.id.edit_search_content);
        mSearchTypeView = (Spinner) actionSearchView.findViewById(R.id.select_search_type);
        mSearchBtnView = (Button) actionSearchView.findViewById(R.id.btn_search);

        mCarpoolFromView = (EditText) actionSearchCarpoolView.findViewById(R.id.edit_search_carpool_from);
        mCarpoolToView = (EditText) actionSearchCarpoolView.findViewById(R.id.edit_search_carpool_to);
        mCarpoolWhenView = (EditText) actionSearchCarpoolView.findViewById(R.id.edit_search_carpool_when);
        mCarpoolMinPassengerView = (EditText) actionSearchCarpoolView.findViewById(R.id.edit_search_carpool_min_passenger);
        mCarpoolMaxPassengerView = (EditText) actionSearchCarpoolView.findViewById(R.id.edit_search_carpool_max_passenger);
        mCarpoolMinPriceView = (EditText) actionSearchCarpoolView.findViewById(R.id.edit_search_carpool_min_price);
        mCarpoolMaxPriceView = (EditText) actionSearchCarpoolView.findViewById(R.id.edit_search_carpool_max_price);

        ArrayAdapter<CharSequence> searchTypeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.search_type_list, R.layout.simple_spinner_item);
        searchTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSearchTypeView.setAdapter(searchTypeAdapter);

        mSearchTypeView.setSelection(4);


        mCarpoolWhenView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean arg) {
                if (arg) {
                    showDatePickerDialog(v);
                }
            }
        });

        mCarpoolWhenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        mSearchTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();

                if (parent.getItemAtPosition(position).toString().equalsIgnoreCase(getString(NavItem.ALL_POSTS.getNameId()))) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.search_fragment_container, new AdvancedSearchFragment())
                            .commit();
                } else if (parent.getItemAtPosition(position).toString().equalsIgnoreCase(getString(NavItem.GROUP_STUDY_POSTS.getNameId()))) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.search_fragment_container, new AdvancedSearchGroupStudyFragment())
                            .commit();
                } else if (parent.getItemAtPosition(position).toString().equalsIgnoreCase(getString(NavItem.BOOK_EXCHANGE_POSTS.getNameId()))) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.search_fragment_container, new AdvancedSearchBookExchangeFragment())
                            .commit();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSearchBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Should start while clicking the search button:
                mProgressBar.setVisibility(View.VISIBLE);
                final String selectStr = "select", emptyStr = "";

                // Input data and verification of the inputs:
                String from = mCarpoolFromView.getText().toString();
                Boolean checkFrom = !(from.equals(emptyStr));
                String to = mCarpoolToView.getText().toString();
                Boolean checkTo = !(to.equals(emptyStr));
                String whenStr = mCarpoolWhenView.getText().toString();
                Boolean checkWhen = !(whenStr.equals(emptyStr));
                Calendar when = Calendar.getInstance();
                if (checkWhen) {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
                    try {
                        when.setTime(format.parse(whenStr));
                        System.out.println(when);
                    } catch (java.text.ParseException e) {
                        // Currently, we do not have anything to do here;
                        // I hope, we will never catch any exception here...
                    }
                }
                String minPassenger = mCarpoolMinPassengerView.getText().toString();
                // For the case below we are going to ignore MinPassenger in the search:
                Boolean checkMinPass = !(minPassenger.equals(emptyStr));
                String maxPassenger = mCarpoolMaxPassengerView.getText().toString();
                // For the case below we are going to ignore MaxPassenger in the search:
                Boolean checkMaxPass = !(maxPassenger.equals(emptyStr));
                String minPrice = mCarpoolMinPriceView.getText().toString();
                // For the case below we are going to ignore MinPrice in the search:
                Boolean checkMinPrice = !(minPrice.equals(emptyStr));
                String maxPrice = mCarpoolMaxPriceView.getText().toString();
                // For the case below we are going to ignore MaxPrice in the search:
                Boolean checkMaxPrice = !(maxPrice.equals(emptyStr));

                // Searching through Carpool posts
                // by taking each input into consideration:
                ParseQuery<ParseObject> CarpoolPosts = Carpool.getQuery();
                if (checkFrom) CarpoolPosts.whereMatches(Carpool.KEY_FROM, from, "i");
                if (checkTo) CarpoolPosts.whereMatches(Carpool.KEY_TO, to, "i");
                if (checkWhen) CarpoolPosts.whereGreaterThanOrEqualTo(Carpool.KEY_WHEN, when.getTime());
                if (checkMinPass) CarpoolPosts.whereGreaterThanOrEqualTo(Carpool.KEY_MAX_PASSENGERS, Integer.parseInt(minPassenger));
                if (checkMaxPass) CarpoolPosts.whereLessThanOrEqualTo(Carpool.KEY_MAX_PASSENGERS, Integer.parseInt(maxPassenger));
                if (checkMinPrice) CarpoolPosts.whereGreaterThanOrEqualTo(Carpool.KEY_PRICE, Integer.parseInt(minPrice));
                if (checkMaxPrice) CarpoolPosts.whereLessThanOrEqualTo(Carpool.KEY_PRICE, Integer.parseInt(maxPrice));
                // Getting Post IDs of the found Carpool Posts:
                CarpoolPosts.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (objects.size() == 0) {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(actionSearchView.getContext(), "No result found", Toast.LENGTH_SHORT).show();
                        }
                        else if (e == null) {
                            List<PostCard> postCards = new ArrayList<PostCard>();
                            for (ParseObject object : objects) {
                                Post post = Carpool.getInstance(object).getPost();
                                try {
                                    post.fetch();
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                postCards.add(new PostCard(getActivity(), post));
                            }
                            PostFeedFragment postFeedFragment = PostFeedFragment.newInstance(postCards);
                            mProgressBar.setVisibility(View.GONE);

                            FragmentManager fragmentManager = getFragmentManager();

                            fragmentManager.beginTransaction()
                                    .replace(R.id.search_fragment_container, postFeedFragment)
                                    .commit();
                        }
                        // We do not need else case...
                    }
                });

            }
        });

        return actionSearchView;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            EditText mWhenView = (EditText) getActivity().findViewById(R.id.edit_search_carpool_when);
            mWhenView.setText(new StringBuilder().append(day).append("/")
                    .append(month+1).append("/").append(year));
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }

}
