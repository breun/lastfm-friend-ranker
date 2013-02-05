package nl.breun.lastfmranker;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import java.util.Iterator;

public class LastfmFriendRankerUI extends UI
{
    private static final String RANK = "Rank";
    private static final String USER = "User";
    private static final String COMPATIBILITY = "Compatibility";

    private final Configuration configuration = Configuration.getInstance();

    private final IndexedContainer friendData = new IndexedContainer();

    private final TextField usernameField = new TextField();
    private final Table table = new Table();

    private LastfmApiClient apiClient;

    @Override
    protected final void init(VaadinRequest request)
    {
        Page.getCurrent().setTitle("Last.fm Friend Ranker");

        initLastfmApiClient();

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
                    updateFriendData(username);
                }
            }
        );
        inputLayout.addComponent(button);

        friendData.addContainerProperty(RANK, Integer.class, -1);
        friendData.addContainerProperty(USER, String.class, "");
        friendData.addContainerProperty(COMPATIBILITY, String.class, "0");

        table.setContainerDataSource(friendData);
        layout.addComponent(table);
    }

    private void initLastfmApiClient()
    {
        final String apiKey = configuration.getLastfmApiKey();
        apiClient = new LastfmApiClient(apiKey);
    }

    private void updateFriendData(final String username)
    {
        friendData.removeAllItems();

        for (String friend : apiClient.getFriends(username))
        {
            final Float compatibility = apiClient.getCompatibility(username, friend);

            final Object id = friendData.addItem();
            friendData.getContainerProperty(id, USER).setValue(friend);
            friendData.getContainerProperty(id, COMPATIBILITY).setValue(Float.toString(compatibility));
        }

        sortAndAddRankToData();
    }

    private void sortAndAddRankToData()
    {
        // Sort by descending compatibility
        friendData.sort(new Object[]{COMPATIBILITY}, new boolean[]{false});

        // Add rank to sorted entries
        int rank = 1;

        for (Iterator iterator = friendData.getItemIds().iterator(); iterator.hasNext(); rank++)
        {
            final Object id = iterator.next();
            friendData.getContainerProperty(id, RANK).setValue(rank);
        }
    }
}