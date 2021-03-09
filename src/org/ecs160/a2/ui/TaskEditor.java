package org.ecs160.a2.ui;

import com.codename1.components.MultiButton;
import com.codename1.ui.*;

import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.TextModeLayout;
import com.codename1.ui.spinner.Picker;
import org.ecs160.a2.models.Task;
import org.ecs160.a2.models.TimeSpan;
import org.ecs160.a2.utils.Database;
import org.ecs160.a2.utils.UIUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskEditor extends Dialog {

    static public final String TITLE_CREATE = "New Task";
    static public final String TITLE_EDIT = "Edit Task";

    private Task task;
    private final String title;

    private TextComponent taskTitle;
    private TextComponent taskTags;
    private TextComponent taskDescription;
    private MultiButton taskSize;

    public TaskEditor(String title) {
        super(title, new BorderLayout());
        this.title = title;
        init();
    }

    public TaskEditor(Task task, String title) {
        super(title, new BorderLayout());
        this.task = task;
        this.title = title;
        init();
    }

    public void init() {
        constructView();
        setDisposeWhenPointerOutOfBounds(true);
        int displayHeight = Display.getInstance().getDisplayHeight();
        show(displayHeight/8, 0, 0, 0);
    }

    /**
     * Add TextFields and Multibuttons to display Task Addition dialog
     */
    private void constructView() {
        setDetailForm();
        setTimeSpanForm();

        Button addButton = new Button(title);
        addButton.addActionListener(e ->  {
            if (task == null) addTaskToDatabase();
            else editTaskInDatabase();
        });
        add(BorderLayout.SOUTH, addButton);
    }

    private void setDetailForm() {
        Form form = getForm("Task Details");

        taskTitle = new TextComponent().label("Title");
        taskTags = new TextComponent().label("Tags");
        taskDescription = new TextComponent().label("Description").multiline(true);
        taskSize = new MultiButton("Size");
        taskSize.addActionListener(e -> showSizePopup(taskSize));

        if (task != null) fillOutFields();

        form.addAll(taskTitle, taskSize, taskTags, taskDescription);
        add(BorderLayout.NORTH, form);
    }

    private Form getForm(String title) {
        TextModeLayout textLayout = new TextModeLayout(3, 2);
        return new Form(title, textLayout);
    }

    private Picker getDateTimePicker(LocalDateTime ldt) {
        Picker picker = new Picker();
        picker.setType(Display.PICKER_TYPE_DATE_AND_TIME);
        picker.setMinuteStep(1);
        picker.setDate(TimeSpan.toDate(ldt));
        return picker;
    }

    private void setTimeSpanForm() {
        Form form = getForm("Time Intervals");

        for (TimeSpan span : task.getTimeSpans()) {
            Picker startPicker = getDateTimePicker(span.getStart());
            Picker endPicker = getDateTimePicker(span.getEnd());
            Label arrow = new Label("", UIUtils.getNextIcon());
            form.add(FlowLayout.encloseCenter(startPicker, arrow, endPicker));
        }

        add(BorderLayout.CENTER, form);
    }

    /**
     * Repopulate task entry dialog for further editing
     */
    private void fillOutFields() {
        taskTitle.text(task.getTitle());
        taskTags.text(String.join(" ", task.getTags()));
        taskDescription.text(task.getDescription());
        taskSize.setTextLine1(task.getSize());
    }

    /**
     * Extract task details entered and write into database
     */
    private void addTaskToDatabase() {
        Task newTask = new Task(taskTitle.getText(), taskDescription.getText(),
                getSizeText(), extractTags());
        Database.write(Task.OBJECT_ID, newTask);
        dispose();
        TaskList.refresh();
    }

    /**
     * Resets task details to update in database
     */
    private void editTaskInDatabase() {
        task.setTitle(taskTitle.getText());
        task.setDescription(taskDescription.getText());
        task.setSize(getSizeText());
        task.setTags(extractTags());

        Database.update(Task.OBJECT_ID, task);
        dispose();
        TaskList.refresh();
    }

    /**
     * Gets size text selected from dialog window
     * @return Returns selected size. If not selected, returns "None"
     */
    private String getSizeText() {
        return taskSize.getText().equals("Size") ? "None" : taskSize.getText();
    }

    /**
     * Splits the tags TextField into multiple tags
     */
    private List<String> extractTags() {
        List<String> tags = new ArrayList<>();
        String[] splits = taskTags.getText().split(" ");
        for (String split : splits) {
            if (!split.equals("")) tags.add(split);
        }
        return tags;
    }

    /**
     * Searches a list of tasks for specific tag or title
     *
     * @param sizeButton Multibutton holder for all single buttons (one size buttons)
     *
     */
    private void showSizePopup(MultiButton sizeButton) {
        Dialog sizeDialog = new Dialog();
        sizeDialog.setLayout(BoxLayout.y());
        sizeDialog.getContentPane().setScrollableY(true);

        List<String> taskSizes = Task.sizes;

        for (int i = 0; i < taskSizes.size(); i++) {
            MultiButton oneSizeButton = new MultiButton(taskSizes.get(i));
            sizeDialog.add(oneSizeButton);
            oneSizeButton.addActionListener(e ->
                    displaySelectedSize(sizeDialog, oneSizeButton, sizeButton)
            );
        }
        sizeDialog.showPopupDialog(sizeButton);
    }

    /**
     * Searches a list of tasks for specific tag or title
     *
     * @param sizeDialog The dialog to be populated with sizes
     * @param oneSizeButton Single button with a specific size
     * @param sizeButton Multibutton holder for all single buttons (one size buttons)
     *
     */
    private void displaySelectedSize(Dialog sizeDialog, MultiButton oneSizeButton, MultiButton sizeButton) {
        sizeButton.setText(oneSizeButton.getText());
        sizeDialog.dispose();
        sizeButton.revalidate();
    }

}