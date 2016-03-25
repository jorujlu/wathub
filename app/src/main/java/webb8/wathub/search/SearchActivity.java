package webb8.wathub.search;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import webb8.wathub.R;
import webb8.wathub.hub.NavItem;
import webb8.wathub.hub.fragments.HubFragment;
import webb8.wathub.hub.fragments.PostFeedFragment;
import webb8.wathub.models.Post;
import webb8.wathub.util.PostCard;

public class SearchActivity extends AppCompatActivity {
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        thisActivity = this;

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ParseQuery<ParseObject> postQuery = Post.getQuery();
            postQuery.whereContains(Post.KEY_CONTENT, query);
            postQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    List<PostCard> postCards = new ArrayList<>();

                    for (ParseObject object : objects) {
                        Post post = Post.getInstance(object);
                        postCards.add(new PostCard(thisActivity, post));
                    }

                    PostFeedFragment postFeedFragment = PostFeedFragment.newInstance(postCards);

                    FragmentManager fragmentManager = getFragmentManager();

                    fragmentManager.beginTransaction()
                            .replace(R.id.search_container, postFeedFragment)
                            .commit();

                }
            });
        }
    }
}
