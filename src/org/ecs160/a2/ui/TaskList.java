package org.ecs160.a2.ui;

import com.codename1.components.Accordion;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.events.ActionEvent;

import org.ecs160.a2.models.Task;
import org.ecs160.a2.utils.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TaskList extends Container {

    public static TaskList instance;

    private final ArrayList<Task> activeList;
    private final ArrayList<Task> inactiveList;
    private final ArrayList<Task> archivedList;
    private String searchString;

    public TaskList() {
        super(BoxLayout.y());
        this.setScrollableY(false);

        this.activeList = new ArrayList<>();
        this.inactiveList = new ArrayList<>();
        this.archivedList = new ArrayList<>();
        this.searchString = "";

        this.configContainer();
        this.refreshContainer();

        TaskList.instance = this;
    }

    /**
     * Refreshes the content of the taskList Container
     * This static method is for outside code to use
     */
    public static void refresh() {
        if (instance != null) {
            instance.refreshContainer();
        }
    }

    /**
     * Adds an event so an outer class can make a change in the taskList on
     * the event
     *
     * @param e the ActionEvent which will cause the change in the taskList
     */
    public static void addSearch(ActionEvent e) {
        if (instance != null) {
            instance.addSearchEvent(e);
        }
    }

    /**
     * Clears the search string so the taskList shows everything
     */
    public static void clearSearch() {
        if (instance != null) {
            instance.searchString = "";
        }
    }

    /**
     * Configures anything to do with the Container holding the task list
     */
    private void configContainer() {
        this.addPullToRefresh(this::refreshContainer);
    }

    /**
     * Refreshes the content of the taskList Container
     */
    private void refreshContainer() {
        this.removeAll();
        this.loadData();
        this.addLists();
    }

    /**
     * What to do to the taskList in the case of a search event happening
     */
    private void addSearchEvent(ActionEvent e) {
        this.searchString = (String)e.getSource();
        this.refreshContainer();
    }

    /**
     * Creates/refreshes the task list
     */
    private void loadData() {
        this.activeList.clear();
        this.inactiveList.clear();
        List<Task> allTasks = (List) Database.readAll(Task.OBJECT_ID);
        this.inputTasks(allTasks);
    }

    /**
     * Splits the list of tasks into active and inactive tasks
     *
     * @param allTasks A list of every task in our database
     */
    private void inputTasks(List<Task> allTasks) {
        for (Task task : allTasks) {
            if (task.isInProgress()) {
                this.activeList.add(task);
            } else if (task.isArchived()) {
                this.archivedList.add(task);
            } else {
                this.inactiveList.add(task);
            }
        }
    }

    /**
     * Adds the task lists to the taskList Container
     */
    private void addLists() {
        this.listTasks("Active Tasks", this.activeList);
        this.listTasks("Inactive Tasks", this.inactiveList);
        this.listTasks("Archived Tasks", this.activeList);
    }

    /**
     * Creates and adds the content to the Container for each list type given
     * by label
     *
     * @param label The label of the task, expects "active" or "inactive"
     * @param tasks The list of tasks that are of type <label>
     */
    private void listTasks(String label, ArrayList<Task> tasks) {
        tasks = this.searchTasks(tasks);

        Accordion tasksAccordion = createTasksAccordion(label, tasks);

        this.addComponent(tasksAccordion);
    }

    private Accordion createTasksAccordion(String label, ArrayList<Task> tasks) {
        Accordion tasksAccordion = new Accordion();
        tasksAccordion.setScrollableY(true);

        int taskCountForLabel = tasks.size();

        Container tasksContainer =
                new Container(new BoxLayout(BoxLayout.Y_AXIS));
        tasksContainer.setScrollableY(false);
        for (Task task : tasks) {
            tasksContainer.addComponent(new TaskCard(task));
        }

        Container labelContainer = new Container(new BorderLayout());
        labelContainer.add(BorderLayout.WEST, new Label(label));
        labelContainer.add(BorderLayout.EAST, new Label(String.valueOf(taskCountForLabel)));

        tasksAccordion.addContent(labelContainer, tasksContainer);
        tasksAccordion.expand(tasksContainer);

        return tasksAccordion;
    }

    /**
     * Creates the task type label, either "Active Tasks" or "Inactive Tasks"
     *
     * @param label The label of the task, expects "active" or "inactive"
     *
     * @return The container holding the task type label
     */
    private Container makeTaskTypeLabel(String label) {
        Container taskTypeCont =
                new Container(new FlowLayout(Component.CENTER));

        Label taskTypeLabel = new Label(label);
        if (label.equals("Active Tasks")) {
            taskTypeLabel.setMaterialIcon(FontImage.MATERIAL_ALARM_ON);
        } else {
            taskTypeLabel.setMaterialIcon(FontImage.MATERIAL_ALARM_OFF);
        }
        taskTypeCont.addComponent(taskTypeLabel);

        return taskTypeCont;
    }

    /**
     * Creates the no tasks label
     *
     * @return The container holding the no tasks label
     */
    private Container makeNoTaskLabel() {
        Container emptyCont =
                new Container(new FlowLayout(Component.CENTER));

        Label emptyLabel = new Label("no tasks");
        emptyCont.addComponent(emptyLabel);

        return emptyCont;
    }

    /**
     * Searches a list of tasks for specific tag or title
     *
     * @param tasks The list of tasks that are going to be searched
     *
     * @return The searched list of tasks
     */
    private ArrayList<Task> searchTasks(ArrayList<Task> tasks) {
        if (this.searchString.equals("")) {
            return tasks;
        }

        tasks.removeIf(task -> !substringInTitle(task, this.searchString) &&
                !substringInTags(task, this.searchString));

        return tasks;
    }

    /**
     * Searches if a substring is in a task's title
     *
     * @param task The task to get the title from
     * @param substring The substring to search for
     *
     * @return A boolean that's true if the substring is found in the title
     */
    private boolean substringInTitle(Task task, String substring) {
        return task.getTitle().toLowerCase(Locale.ROOT).contains(
                substring.toLowerCase(Locale.ROOT));
    }

    /**
     * Searches if a substring is in a task's tags
     *
     * @param task The task to get tags from
     * @param substring The substring to search for
     *
     * @return A boolean that's true if the substring is found in the tags
     */
    private boolean substringInTags(Task task, String substring) {
        for (String tag : task.getTags()) {
            if (tag.toLowerCase(Locale.ROOT).contains(
                    substring.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}