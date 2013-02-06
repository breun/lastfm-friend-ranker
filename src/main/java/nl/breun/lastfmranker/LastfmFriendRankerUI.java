package nl.breun.lastfmranker;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import java.util.Collection;

public class LastfmFriendRankerUI extends UI
{
    private static final String USER = "User";
    private static final String COMPATIBILITY = "Compatibility";

    private final Configuration configuration = Configuration.getInstance();

    private final TextField usernameField = new TextField();
    private final Button button = new Button("Let's go!");
    private final ProgressIndicator progressIndicator = new ProgressIndicator();
    private final Label statusLabel = new Label();
    private final Table table = new Table();

    private LastfmApiClient apiClient;

    @Override
    protected final void init(VaadinRequest request)
    {
        Page.getCurrent().setTitle("Last.fm Friend Ranker");

        initLastfmApiClient();

        final VerticalLayout layout = new VerticalLayout();

        usernameField.setInputPrompt("Last.fm username");
        layout.addComponent(usernameField);

        button.addClickListener(new Button.ClickListener()
            {
                @Override
                public void buttonClick(ClickEvent event)
                {
                    final String username = usernameField.getValue();

                    button.setEnabled(false);
                    progressIndicator.setEnabled(true);
                    progressIndicator.setValue(0f);

                    new Worker(username).start();
                }
            }
        );
        layout.addComponent(button);

        progressIndicator.setEnabled(false);
        layout.addComponent(progressIndicator);

        statusLabel.setValue("Ready");
        layout.addComponent(statusLabel);

        table.setRowHeaderMode(Table.RowHeaderMode.INDEX);
        table.addContainerProperty(USER, String.class, "");
        table.addContainerProperty(COMPATIBILITY, String.class, "0");
        table.setSortEnabled(false);
        layout.addComponent(table);

        setContent(layout);
    }

    private void initLastfmApiClient()
    {
        final String apiKey = configuration.getLastfmApiKey();
        apiClient = new LastfmApiClient(apiKey);
    }

    class Worker extends Thread
    {
        private final String username;

        private int current = 1;
        private int total = Integer.MAX_VALUE;

        Worker(final String username)
        {
            this.username = username;
        }

        @Override
        public void run()
        {
            table.getUI().getSession().lock();
            try
            {
                table.removeAllItems();
            }
            finally
            {
                table.getUI().getSession().unlock();
            }

            loadFriendsAndCompatibilityScores();

            button.getUI().getSession().lock();
            try
            {
                button.setEnabled(true);
            }
            finally
            {
                button.getUI().getSession().unlock();
            }

            progressIndicator.getUI().getSession().lock();
            try
            {
                progressIndicator.setEnabled(false);
            }
            finally
            {
                progressIndicator.getUI().getSession().unlock();
            }
        }

        private void loadFriendsAndCompatibilityScores()
        {
            final Collection<String> friends = apiClient.getFriends(username);
            total = friends.size();

            for (String friend : friends)
            {
                statusLabel.getUI().getSession().lock();
                try
                {
                    statusLabel.setValue("Checking compatibility with " + friend + " (" + current + "/" + total + ")");
                }
                finally
                {
                    statusLabel.getUI().getSession().unlock();
                }

                final Float compatibility = apiClient.getCompatibility(username, friend);

                table.getUI().getSession().lock();
                try
                {
                    final Object id = table.addItem();
                    table.getContainerProperty(id, USER).setValue(friend);
                    table.getContainerProperty(id, COMPATIBILITY).setValue(Float.toString(compatibility));
                    table.sort(new Object[]{COMPATIBILITY}, new boolean[]{false});
                }
                finally
                {
                    table.getUI().getSession().unlock();
                }

                progressIndicator.getUI().getSession().lock();
                try
                {
                    progressIndicator.setValue((float) current / total);
                }
                finally
                {
                    progressIndicator.getUI().getSession().unlock();
                }

                current++;
            }

            statusLabel.getUI().getSession().lock();
            try
            {
                statusLabel.setValue("Ready");
            }
            finally
            {
                statusLabel.getUI().getSession().unlock();
            }
        }
    }
}