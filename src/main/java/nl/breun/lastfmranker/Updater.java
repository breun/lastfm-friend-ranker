package nl.breun.lastfmranker;

interface Updater {

    void clearResults();

    void setRunning(boolean running);

    void setStatus(String message);

    void setProgress(Float value);

    void addFriendCompatibility(String friend, Float compatibility);
}
