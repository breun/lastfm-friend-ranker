package nl.breun.lastfmranker;

import java.util.Collection;

class Worker extends Thread {

    private final Updater updater;
    private final String username;
    private final LastfmApiClient apiClient;

    Worker(final Updater updater, final String username) {
        this.updater = updater;
        this.username = username;

        Configuration configuration = Configuration.getInstance();
        String apiKey = configuration.getLastfmApiKey();
        apiClient = new LastfmApiClient(apiKey);
    }

    @Override
    public void run() {
        updater.clearResults();

        updater.setRunning(true);

        final Collection<String> friends = apiClient.getFriends(username);

        int current = 1;
        int total = friends.size();

        for (String friend : friends) {
            updater.setStatus("Checking compatibility with " + friend + " (" + current + "/" + total + ")");

            final Float compatibility = apiClient.getCompatibility(username, friend);

            updater.addFriendCompatibility(friend, compatibility);

            updater.setProgress((float) current / total);

            current++;
        }

        updater.setRunning(false);
    }
}
