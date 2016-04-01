package webb8.wathub.search.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import webb8.wathub.R;
import webb8.wathub.hub.fragments.PostFeedFragment;
import webb8.wathub.models.Course;
import webb8.wathub.models.GroupStudy;
import webb8.wathub.models.Post;
import webb8.wathub.util.NavItem;
import webb8.wathub.util.PostCard;
import webb8.wathub.util.Util;

/**
 * Created by mismayil on 3/24/16.
 */
public class AdvancedSearchGroupStudyFragment extends AdvancedSearchFragment {

    // UI fields
    private EditText mGroupNameView;
    private EditText mGroupWhereView;
    private EditText mGroupWhenView;
    private EditText mGroupStartTimeView;
    private EditText mGroupEndTimeView;
    private Spinner mGroupCourseSubjectView;
    private Spinner mGroupCourseNumberView;
    private EditText mGroupMinPeopleView;
    private EditText mGroupMaxPeopleView;
    private RelativeLayout mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View actionSearchView = inflater.inflate(R.layout.fragment_advanced_search, container, false);
        final View actionSearchGroupStudyView = inflater.inflate(R.layout.fragment_advanced_search_groupstudy, container, false);
        mActionSearchContainer = (FrameLayout) actionSearchView.findViewById(R.id.advanced_search_container);

        mActionSearchContainer.addView(actionSearchGroupStudyView);
        mProgressBar = (RelativeLayout) actionSearchView.findViewById(R.id.progress_bar);

        mContentView = (EditText) actionSearchView.findViewById(R.id.edit_search_content);
        mSearchTypeView = (Spinner) actionSearchView.findViewById(R.id.select_search_type);
        mSearchBtnView = (Button) actionSearchView.findViewById(R.id.btn_search);

        mGroupNameView = (EditText) actionSearchGroupStudyView.findViewById(R.id.edit_search_group_name);
        mGroupWhereView = (EditText) actionSearchGroupStudyView.findViewById(R.id.edit_search_group_where);
        mGroupWhenView = (EditText) actionSearchGroupStudyView.findViewById(R.id.edit_search_group_when);
        mGroupStartTimeView = (EditText) actionSearchGroupStudyView.findViewById(R.id.edit_search_group_start_time);
        mGroupEndTimeView = (EditText) actionSearchGroupStudyView.findViewById(R.id.edit_search_group_end_time);
        mGroupCourseSubjectView = (Spinner) actionSearchGroupStudyView.findViewById(R.id.select_search_group_course_subject);
        mGroupCourseNumberView = (Spinner) actionSearchGroupStudyView.findViewById(R.id.select_search_group_course_number);
        mGroupMinPeopleView = (EditText) actionSearchGroupStudyView.findViewById(R.id.edit_search_group_min_people);
        mGroupMaxPeopleView = (EditText) actionSearchGroupStudyView.findViewById(R.id.edit_search_group_max_people);

        ArrayAdapter<CharSequence> searchTypeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.search_type_list, R.layout.simple_spinner_item);
        searchTypeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSearchTypeView.setAdapter(searchTypeAdapter);

        ArrayAdapter<CharSequence> courseSubjectAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.search_course_subject_list, R.layout.simple_spinner_item);
        courseSubjectAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> courseNumberAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.search_course_number_list, R.layout.simple_spinner_item);
        courseNumberAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        Util.updateCourseSubjectsAdapter(getActivity(), mGroupCourseSubjectView);

        mSearchTypeView.setSelection(3);

        mSearchTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();

                if (parent.getItemAtPosition(position).toString().equalsIgnoreCase(getString(NavItem.ALL_POSTS.getNameId()))) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.search_fragment_container, new AdvancedSearchFragment())
                            .commit();
                } else if (parent.getItemAtPosition(position).toString().equalsIgnoreCase(getString(NavItem.BOOK_EXCHANGE_POSTS.getNameId()))) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.search_fragment_container, new AdvancedSearchBookExchangeFragment())
                            .commit();
                } else if (parent.getItemAtPosition(position).toString().equalsIgnoreCase(getString(NavItem.CARPOOL_POSTS.getNameId()))) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.search_fragment_container, new AdvancedSearchCarpoolFragment())
                            .commit();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mGroupCourseSubjectView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Util.updateCourseNumbersAdapter(getActivity(), mGroupCourseNumberView, parent.getItemAtPosition(position).toString());
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
                final String name = mGroupNameView.getText().toString();
                final Boolean checkName = !(name.equals(emptyStr));
                final String where = mGroupWhereView.getText().toString();
                final Boolean checkWhere = !(where.equals(emptyStr));
                final String whenStr = mGroupWhenView.getText().toString();
                Boolean checkWhenBuf = !(whenStr.equals(emptyStr));
                final String startTimeStr = mGroupStartTimeView.getText().toString();
                Boolean checkStartBuf = !(startTimeStr.equals(emptyStr));
                final String endTimeStr = mGroupEndTimeView.getText().toString();
                Boolean checkEndBuf = !(endTimeStr.equals(emptyStr));
                // Reading date and time information in specific format:
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.CANADA);
                final Calendar startTime = Calendar.getInstance();
                final Calendar endTime = Calendar.getInstance();
                final Calendar today = Calendar.getInstance();
                // Need to if at least one of them was given as input:
                Boolean checkDateBuf = checkWhenBuf || checkStartBuf || checkEndBuf;
                if (checkDateBuf) {
                    // If so, ...
                    if (!checkWhenBuf) {
                        // If date is not specified,
                        // Today's date is set by default:
                        if (checkStartBuf) {
                            try {
                                startTime.setTime(format.parse(today.getTime().toString() + " " + startTimeStr));
                            } catch (java.text.ParseException e) {
                                // Currently, we do not have anything to do here;
                                // I hope, we will never catch any exception here...
                            }
                        }
                        if (checkEndBuf) {
                            try {
                                endTime.setTime(format.parse(today.getTime().toString() + " " + endTimeStr));
                            } catch (java.text.ParseException e) {
                                // Currently, we do not have anything to do here;
                                // I hope, we will never catch any exception here...
                            }
                        }
                        // After assigning today's date, need to change the situation:
                        checkWhenBuf = true;
                    } else {
                        // But if the date is given:
                        if (checkStartBuf) {

                            try {
                                startTime.setTime(format.parse(whenStr + " " + startTimeStr));
                            } catch (java.text.ParseException e) {
                                // Currently, we do not have anything to do here;
                                // I hope, we will never catch any exception here...
                            }
                        } else {
                            // If the start time is not given as input,
                            // 00:00 is set by default:
                            try {
                                startTime.setTime(format.parse(whenStr + " 00:00"));
                            } catch (java.text.ParseException e) {
                                // Currently, we do not have anything to do here;
                                // I hope, we will never catch any exception here...
                            }
                            checkStartBuf = true;
                        }
                        if (checkEndBuf) {
                            try {
                                endTime.setTime(format.parse(whenStr + " " + endTimeStr));
                            } catch (java.text.ParseException e) {
                                // Currently, we do not have anything to do here;
                                // I hope, we will never catch any exception here...
                            }
                        } else {
                            // if the end time is not given as input,
                            // 23:59 is set by default:
                            try {
                                endTime.setTime(format.parse(whenStr + " 23:59"));
                            } catch (java.text.ParseException e) {
                                // Currently, we do not have anything to do here;
                                // I hope, we will never catch any exception here...
                            }
                            checkEndBuf = true;
                        }
                    }
                }
                final Boolean checkDate = checkDateBuf;
                final Boolean checkWhen = checkWhenBuf;
                final Boolean checkStart = checkStartBuf;
                final Boolean checkEnd = checkEndBuf;
                final String courseSubject = mGroupCourseSubjectView.getSelectedItem().toString();
                final Boolean checkCourseSubject = !(courseSubject.toLowerCase().contains(selectStr));
                String courseNumberBuf = emptyStr;
                Boolean checkCourseNumberBuf = false;
                if (checkCourseSubject) {
                    courseNumberBuf += mGroupCourseNumberView.getSelectedItem().toString();
                    checkCourseNumberBuf = checkCourseNumberBuf || !(courseNumberBuf.toLowerCase().contains(selectStr));
                }
                final String courseNumber = courseNumberBuf;
                final Boolean checkCourseNumber = checkCourseNumberBuf;
                final String minPeople = mGroupMinPeopleView.getText().toString();
                // For the case below we are going to ignore MinPeople in the search:
                final Boolean checkMinPeople = !(minPeople.equals(emptyStr));
                final String maxPeople = mGroupMaxPeopleView.getText().toString();
                // For the case below we are going to ignore MaxPeople in the search:
                final Boolean checkMaxPeople = !(maxPeople.equals(emptyStr));

                /* First, we have to search through the courses
                 * to obtain the course IDs for being able to use them
                 * in the main search:
                 */
                ParseQuery<ParseObject> courseQuery = Course.getQuery();
                final Collection<Course> courses = new ArrayList<Course>();
                if (checkCourseSubject) {
                    courseQuery.whereEqualTo(Course.KEY_SUBJECT, courseSubject);
                    // Since we can select the number after selecting the subject:
                    if (checkCourseNumber)
                        courseQuery.whereEqualTo(Course.KEY_NUMBER, courseNumber);
                    courseQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                for (ParseObject object : objects) {
                                    courses.add(Course.getInstance(object));
                                }
                                // Searching through GroupStudy posts
                                // by taking each input into consideration:
                                ParseQuery<ParseObject> groupStudyPostQuery = GroupStudy.getQuery();
                                if (checkName)
                                    groupStudyPostQuery.whereContains(GroupStudy.KEY_GROUP_NAME, name);
                                groupStudyPostQuery.whereContainedIn(GroupStudy.KEY_COURSE, courses);
                                if (checkWhen && checkStart)
                                    groupStudyPostQuery.whereEqualTo(GroupStudy.KEY_START_TIME, startTime.getTime());
                                System.out.println(startTime.getTime().toString());
                                System.out.println(endTime.getTime().toString());
                                if (checkWhen && checkEnd)
                                    groupStudyPostQuery.whereEqualTo(GroupStudy.KEY_END_TIME, endTime.getTime());
                                if (checkWhere)
                                    groupStudyPostQuery.whereContains(GroupStudy.KEY_WHERE, where);
                                if (checkMinPeople)
                                    groupStudyPostQuery.whereGreaterThanOrEqualTo(GroupStudy.KEY_MAX_PEOPLE, Integer.parseInt(minPeople));
                                if (checkMaxPeople)
                                    groupStudyPostQuery.whereLessThanOrEqualTo(GroupStudy.KEY_MAX_PEOPLE, Integer.parseInt(maxPeople));
                                // Getting Post IDs of the found Group Study Posts:
                                groupStudyPostQuery.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            List<PostCard> postCards = new ArrayList<>();
                                            for (ParseObject object : objects) {
                                                Post post = GroupStudy.getInstance(object).getPost();
                                                postCards.add(new PostCard(getActivity(), post));
                                            }
                                            PostFeedFragment postFeedFragment = PostFeedFragment.newInstance(postCards);
                                            mProgressBar.setVisibility(View.GONE);

                                            FragmentManager fragmentManager = getFragmentManager();

                                            fragmentManager.beginTransaction()
                                                    .replace(R.id.advanced_search_container, postFeedFragment)
                                                    .commit();
                                        }
                                        // We do not need else case...
                                    }
                                });
                            }
                            // We do not need else case...
                        }
                    });
                } else {
                    // Searching through GroupStudy posts
                    // by taking each input into consideration:
                    ParseQuery<ParseObject> groupStudyPostQuery = GroupStudy.getQuery();
                    if (checkName)
                        groupStudyPostQuery.whereContains(GroupStudy.KEY_GROUP_NAME, name);
                    if (checkWhen && checkStart)
                        groupStudyPostQuery.whereEqualTo(GroupStudy.KEY_START_TIME, startTime.getTime());
                    System.out.println(startTime.getTime().toString());
                    System.out.println(endTime.getTime().toString());
                    if (checkWhen && checkEnd)
                        groupStudyPostQuery.whereEqualTo(GroupStudy.KEY_END_TIME, endTime.getTime());
                    if (checkWhere) groupStudyPostQuery.whereContains(GroupStudy.KEY_WHERE, where);
                    if (checkMinPeople)
                        groupStudyPostQuery.whereGreaterThanOrEqualTo(GroupStudy.KEY_MAX_PEOPLE, Integer.parseInt(minPeople));
                    if (checkMaxPeople)
                        groupStudyPostQuery.whereLessThanOrEqualTo(GroupStudy.KEY_MAX_PEOPLE, Integer.parseInt(maxPeople));
                    // Getting Post IDs of the found Group Study Posts:
                    groupStudyPostQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                List<PostCard> postCards = new ArrayList<>();
                                for (ParseObject object : objects) {
                                    Post post = GroupStudy.getInstance(object).getPost();
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
            }
        });

        return actionSearchView;
    }
}
