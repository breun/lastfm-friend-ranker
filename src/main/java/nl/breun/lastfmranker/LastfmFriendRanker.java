package nl.breun.lastfmranker;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import de.umass.lastfm.Caller;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Tasteometer;
import de.umass.lastfm.Tasteometer.InputType;
import de.umass.lastfm.User;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LastfmFriendRanker extends Application {

    private static final Logger LOGGER = Logger.getLogger(LastfmFriendRanker.class.getName());
    private static final String CONFIGURATION = "configuration.properties";
    private static final String COLUMN_USERNAME = "Username";
    private static final String COLUMN_SCORE = "Score";

    private final TextField usernameField = new TextField();
    private final Table table = new Table();

    private String apiKey;

    @Override
    public final void init() {
        Caller.getInstance().setUserAgent("tst");
        initAPIKey();
        initUI();
        LOGGER.info("Application initialized, ready for requests!");
    }

    private void initAPIKey() {
        try {
            Reader reader = new FileReader(CONFIGURATION);
            try {
                Properties properties = new Properties();
                properties.load(reader);
                apiKey = properties.getProperty("api_key");
            } finally {
                reader.close();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error reading api_key from " + CONFIGURATION, ex);
        }
    }

    private void initUI() {
        final Window window = new Window("Last.fm Friend Ranker");

        usernameField.setInputPrompt("Last.fm username");
        window.addComponent(usernameField);

        Button submit = new Button("Let's go!", new ClickListener()  {

            @Override
            public void buttonClick(ClickEvent event) {
                String username = (String) usernameField.getValue();
                updateFriendsAndCompatibilityTable(username);
            }
        });
        window.addComponent(submit);

        table.addContainerProperty(COLUMN_USERNAME, String.class, "");
        table.addContainerProperty(COLUMN_SCORE, Float.class, 0);
        window.addComponent(table);

        setMainWindow(window);
    }

    private void updateFriendsAndCompatibilityTable(final String username) {
        table.removeAllItems();
        LOGGER.log(Level.INFO, "Getting friends for {0}", username);
        
        PaginatedResult<User> friends = User.getFriends(username, true, 1, Integer.MAX_VALUE, apiKey);
        
        for (User friend : friends) {
            final String friendName = friend.getName();
            final Float score = Tasteometer.compare(InputType.USER, username, InputType.USER, friendName, apiKey).getScore();
            LOGGER.log(Level.INFO, "Compatibility for {0} and {1} is {2}", new Object[]{username, friendName, score});

            // TODO: Isn't there a simpler way?
            Object id = table.addItem();
            table.getContainerProperty(id, COLUMN_USERNAME).setValue(friendName);
            table.getContainerProperty(id, COLUMN_SCORE).setValue(score);
            
            // TODO: Force table update/repaint?
        }
        LOGGER.log(Level.INFO, "Done getting data for {0}", username);
    }
}
