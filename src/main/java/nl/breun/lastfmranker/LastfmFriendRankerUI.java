package nl.breun.lastfmranker;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import de.umass.lastfm.Caller;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Tasteometer;
import de.umass.lastfm.Tasteometer.InputType;
import de.umass.lastfm.User;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LastfmFriendRankerUI extends UI
{
    private static final Logger LOGGER = Logger.getLogger(LastfmFriendRankerUI.class.getName());
    private static final String CONFIGURATION = "configuration.properties";

    private static final String RANK = "Rank";
    private static final String USER = "User";
    private static final String COMPATIBILITY = "Compatibility";

    private final IndexedContainer data = new IndexedContainer();

    private final TextField usernameField = new TextField();
    private final Table table = new Table();

    private String apiKey;

    @Override
    protected final void init(VaadinRequest request)
    {
        initLastfmApiClient();

        Page.getCurrent().setTitle("Last.fm Friend Ranker");

        final VerticalLayout layout = new VerticalLayout();
        setContent(layout);

        final HorizontalLayout inputLayout = new HorizontalLayout();
        layout.addComponent(inputLayout);

        usernameField.setInputPrompt("Last.fm username");
        inputLayout.addComponent(usernameField);

        final Button button = new Button("Let's go!",
            new Button.ClickListener()
            {
                @Override
                public void buttonClick(ClickEvent event)
                {
                    final String username = usernameField.getValue();
                    updateFriends(username);
                }
            }
        );
        inputLayout.addComponent(button);

        data.addContainerProperty(RANK, Integer.class, -1);
        data.addContainerProperty(USER, String.class, "");
        data.addContainerProperty(COMPATIBILITY, String.class, "0");

        table.setContainerDataSource(data);
        layout.addComponent(table);
    }

    private void initLastfmApiClient() {
        Caller.getInstance().setUserAgent("tst");
        initApiKey();
    }

    private void initApiKey()
    {
        try
        {
            Reader reader = new FileReader(CONFIGURATION);
            try
            {
                Properties properties = new Properties();
                properties.load(reader);
                apiKey = properties.getProperty("api_key");
            }
            finally
            {
                reader.close();
            }
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, "Error reading api_key from " + CONFIGURATION, ex);
        }
    }

    private void updateFriends(final String username)
    {
        resetData();

        updateFriendsAndCompatibilityScores(username);

        sortAndRank();
    }

    private void resetData()
    {
        data.removeAllItems();
    }

    private void updateFriendsAndCompatibilityScores(String username)
    {
        LOGGER.log(Level.INFO, "Getting friends and compatibility scores for Last.fm user {0}", username);

        final PaginatedResult<User> friends = User.getFriends(username, false, 1, Integer.MAX_VALUE, apiKey);

        for (User friend : friends)
        {
            final String friendName = friend.getName();
            final Float score = Tasteometer.compare(InputType.USER, username, InputType.USER, friendName, apiKey).getScore();

            LOGGER.log(Level.INFO, "Compatibility for {0} and {1} is {2}", new Object[]{username, friendName, score});

            final Object id = data.addItem();
            data.getContainerProperty(id, USER).setValue(friendName);
            data.getContainerProperty(id, COMPATIBILITY).setValue(Float.toString(score));
        }

        LOGGER.log(Level.INFO, "Done getting data for Last.fm user {0}", username);
    }

    private void sortAndRank()
    {
        // Sort by descending compatibility
        data.sort(new Object[]{COMPATIBILITY}, new boolean[]{false});

        // Add rank to sorted entries
        int rank = 1;

        for (Iterator iterator = data.getItemIds().iterator(); iterator.hasNext(); rank++)
        {
            final Object id = iterator.next();
            data.getContainerProperty(id, RANK).setValue(rank);
        }
    }
}