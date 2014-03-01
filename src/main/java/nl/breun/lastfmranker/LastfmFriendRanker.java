package nl.breun.lastfmranker;

import com.vaadin.annotations.Push;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

@Push
public class LastfmFriendRanker extends UI implements Updater {

    private static final String COLUMN_USER = "User";
    private static final String COLUMN_COMPATIBILITY = "Compatibility";

    private final TextField usernameField = new TextField();
    private final Button button = new Button("Let's go!");
    private final ProgressBar progressBar = new ProgressBar();
    private final Label statusLabel = new Label();
    private final Table table = new Table();

    @Override
    protected final void init(VaadinRequest request) {
        Page.getCurrent().setTitle("Last.fm Friend Ranker");

        final VerticalLayout layout = new VerticalLayout();

        usernameField.setInputPrompt("Last.fm username");
        layout.addComponent(usernameField);

        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final String username = usernameField.getValue();
                new Worker(LastfmFriendRanker.this, username).start();
            }
        });
        layout.addComponent(button);

        progressBar.setEnabled(false);
        layout.addComponent(progressBar);

        statusLabel.setValue("Ready");
        layout.addComponent(statusLabel);

        table.setRowHeaderMode(Table.RowHeaderMode.INDEX);
        table.addContainerProperty(COLUMN_USER, String.class, null);
        table.addContainerProperty(COLUMN_COMPATIBILITY, Float.class, null);
        table.setSortEnabled(false);
        layout.addComponent(table);

        setContent(layout);
    }

    @Override
    public void clearResults() {
        access(new Runnable() {
            @Override
            public void run() {
                table.removeAllItems();
                progressBar.setValue(0f);

            }
        });
    }

    @Override
    public void setStatus(final String message) {
        access(new Runnable() {
            @Override
            public void run() {
                statusLabel.setValue(message);
            }
        });
    }

    @Override
    public void setProgress(final Float value) {
        access(new Runnable() {
            @Override
            public void run() {
                progressBar.setValue(value);
            }
        });
    }

    @Override
    public void setRunning(final boolean running) {
        access(new Runnable() {
            @Override
            public void run() {
                statusLabel.setValue(running ? "Running..." : "Ready");
                button.setEnabled(!running);
                progressBar.setEnabled(running);
            }
        });
    }

    @Override
    public void addFriendCompatibility(final String friend, final Float compatibility) {
        access(new Runnable() {
            @Override
            public void run() {
                table.addItem(new Object[]{friend, compatibility}, friend);
                table.sort(new Object[]{COLUMN_COMPATIBILITY}, new boolean[]{false});
            }
        });
    }
}