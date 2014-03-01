package nl.breun.lastfmranker;

import de.umass.lastfm.Caller;
import de.umass.lastfm.Tasteometer;
import de.umass.lastfm.User;

import java.util.ArrayList;
import java.util.Collection;

class LastfmApiClient {

    private static final String USER_AGENT = "lastfm-friend-ranker";

    private final String apiKey;

    public LastfmApiClient(final String apiKey) {
        Caller.getInstance().setUserAgent(USER_AGENT);
        this.apiKey = apiKey;
    }

    public final Collection<String> getFriends(final String username) {
        final Collection<String> friends = new ArrayList<>();

        for (User friend : User.getFriends(username, false, 1, Integer.MAX_VALUE, apiKey)) {
            friends.add(friend.getName());
        }

        return friends;
    }

    public final Float getCompatibility(final String user1, final String user2) {
        return Tasteometer.compare(Tasteometer.InputType.USER, user1, Tasteometer.InputType.USER, user2, apiKey).getScore();
    }
}
