package org.ecs160.a2.ui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BoxLayout;

import org.ecs160.a2.models.Task;

public class Summary extends Container {

    // might move these to some other class if they need to be shared
    private static final Font nativeLight = Font.createTrueTypeFont("native:MainLight");
    private static final Font nativeRegular = Font.createTrueTypeFont("native:MainRegular");
    private static final Font nativeBold = Font.createTrueTypeFont("native:MainBold");

    // NOTE: this is temporary and will be changed to work with the
    // database later on!
    private static List<Task> taskList = new ArrayList<Task>();

    // containers that house their labels
    private Container taskContainer, sizeContainer, statsContainer;

    public Summary () {
        super(new BoxLayout(BoxLayout.Y_AXIS));
        this.setScrollableY(true);

        // temporary Tasks for the summary
        taskList.add(new Task("Task 1"));
        taskList.add(new Task("Task 2"));
        taskList.get(0).start(LocalDateTime.of(2021, 2, 21, 5, 0));
        taskList.get(0).stop(LocalDateTime.of(2021, 2, 21, 7, 0));

        // title
        this.add(createLabel("Summary", nativeBold, 0x000000, 8.0f));

        // TODO add other pages (by size and all task summary)

        // Tasks
        this.add(createLabel("Tasks", nativeBold, 0x000000, 5.5f));
        this.taskContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        this.add(this.taskContainer);  

        // Sizes
        this.add(createLabel("Sizes", nativeBold, 0x000000, 5.5f));
        this.sizeContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        this.add(this.sizeContainer);  

        // Stats
        this.add(createLabel("Statistics", nativeBold, 0x000000, 5.5f));
        this.statsContainer = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        this.add(this.statsContainer);  

        // call function on refresh (temporary, can have a better solution)
        this.addPullToRefresh(() -> updateVisibleContainers());
        this.updateVisibleContainers();
    }

    // create a label based on the specified parameters
    private Label createLabel (String labelText, Font style, int color, 
                                                             float fontSize) {
        Label label = new Label(labelText);

        int pixelSize = Display.getInstance().convertToPixels(fontSize);
        label.getAllStyles().setFont(style.derive(pixelSize, 
                                                  Font.STYLE_PLAIN));
        label.getAllStyles().setFgColor(color);

        return label;
    }

    private void updateTaskLabels () {
        int i;

        for (i = 0; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            Label label;

            if (i < this.taskContainer.getComponentCount()) {
                label = (Label) this.taskContainer.getComponentAt(i);
            } else {
                label = createLabel("", nativeLight, 0x000000, 3.0f);
                this.taskContainer.add(label);
            }

            label.setText(" - " + (task.getTotalTime() / 3600000) +
                          " hours total for " + task.getTitle());
        }

        while (i < this.taskContainer.getComponentCount()) {
            Component extraLabel = this.taskContainer.getComponentAt(i);
            this.taskContainer.removeComponent(extraLabel);
        }
    }

    private void updateSizeLabels () {

    }

    private void updateStatsLabels () {

    }

    public void updateVisibleContainers () {
        this.updateTaskLabels();
    }

    // definitely gonna change this, unneeded since this class is a container
    public Container get () {
        return this;
    }
}
