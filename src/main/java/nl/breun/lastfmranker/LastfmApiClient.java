package nl.breun.lastfmranker;

import de.umass.lastfm.Caller;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Tasteometer;
import de.umass.lastfm.User;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LastfmApiClient
{
    private static final Logger LOGGER = Logger.getLogger(LastfmApiClient.class.getName());

    private String apiKey;

    public LastfmApiClient(final String apiKey)
    {
        Caller.getInstance().setUserAgent("tst");
        this.apiKey = apiKey;
    }

    public Map<String, Float> getFriendsAndCompatibilityScores(final String username)
    {
        final Map<String, Float> result = new HashMap<String, Float>();

        LOGGER.log(Level.INFO, "Getting friends and compatibility scores for Last.fm user {0}", username);

        final PaginatedResult<User> friends = User.getFriends(username, false, 1, Integer.MAX_VALUE, apiKey);

        for (User friend : friends)
        {
            final String friendName = friend.getName();
            final Float friendCompatibility = Tasteometer.compare(Tasteometer.InputType.USER, username, Tasteometer.InputType.USER, friendName, apiKey).getScore();

            LOGGER.log(Level.INFO, "Compatibility for {0} and {1} is {2}", new Object[]{username, friendName, friendCompatibility});

            result.put(friendName, friendCompatibility);
        }

        LOGGER.log(Level.INFO, "Done getting data for Last.fm user {0}", username);

        return result;
    }
}
