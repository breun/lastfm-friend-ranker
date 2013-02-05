package nl.breun.lastfmranker;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import java.util.Iterator;
import java.util.Map;

public class LastfmFriendRankerUI extends UI
{
    private static final String RANK = "Rank";
    private static final String USER = "User";
    private static final String COMPATIBILITY = "Compatibility";

    private final Configuration configuration = Configuration.getInstance();

    private final IndexedContainer data = new IndexedContainer();

    private final TextField usernameField = new TextField();
    private final Table table = new Table();

    private LastfmApiClient apiClient;

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

    private void initLastfmApiClient()
    {
        final String apiKey = configuration.getLastfmApiKey();
        apiClient = new LastfmApiClient(apiKey);
    }

    private void updateFriends(final String username)
    {
        data.removeAllItems();

        final Map<String, Float> friendsAndCompatibilityScores = apiClient.getFriendsAndCompatibilityScores(username);

        for (Map.Entry<String, Float> entry : friendsAndCompatibilityScores.entrySet())
        {
            final String friendName = entry.getKey();
            final Float friendCompatibility = entry.getValue();

            final Object id = data.addItem();
            data.getContainerProperty(id, USER).setValue(friendName);
            data.getContainerProperty(id, COMPATIBILITY).setValue(Float.toString(friendCompatibility));
        }

        sortAndAddRankToData();
    }

    private void sortAndAddRankToData()
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