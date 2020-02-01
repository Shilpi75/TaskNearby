package app.tasknearby.yashcreations.com.tasknearby;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;
import app.tasknearby.yashcreations.com.tasknearby.models.TaskSearchModel;
import app.tasknearby.yashcreations.com.tasknearby.utils.TaskStateUtil;

/**
 * Allows searching tasks with given task or location name in search box.
 *
 * @author shilpi.
 */
public class TaskSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private TaskRepository mTaskRepository;
    private TaskAdapter mTaskAdapter;
    private List<TaskStateWrapper> mTaskStateWrapperList;
    private Map<TaskStateWrapper, TaskSearchModel> mTaskSearchModelMap = new HashMap<>();
    private Toolbar toolbar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_search);
        initialize();
        setActionBar();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Handles the search intent by searching tasks which contains the entered location or task name.
     *
     * @param intent Intent to be handled.
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchTasks(query);
        }
    }

    /**
     * Called whenever a user submits a query.
     *
     * @param query String submitted by the user.
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Called whenever the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        searchTasks(newText);
        return true;
    }

    /**
     * It searches tasks with task or location name containing the query string. It then populates the recycler view list with those tasks.
     *
     * @param query task or location name to be searched.
     */
    private void searchTasks(String query) {
        List<TaskStateWrapper> taskStateWrappers = filterTasks(query);
        mTaskAdapter.setData(taskStateWrappers);
        mTaskAdapter.notifyDataSetChanged();
        setNoTasksView(mTaskAdapter.getItemCount());
    }

    /**
     * It filters the task which contains query string as task or location name.
     *
     * @param query task or location name to be searched.
     * @return List of {@link TaskStateWrapper} with tasks containing query string as task or location name.
     */
    private List<TaskStateWrapper> filterTasks(final String query) {
        final List<TaskStateWrapper> filteredTasks = new ArrayList<>();
        for (TaskStateWrapper taskStateWrapper : mTaskStateWrapperList) {
            TaskSearchModel taskSearchModel = mTaskSearchModelMap.get(taskStateWrapper);
            if (null == taskSearchModel) {
                addTaskSearchModel(taskStateWrapper);
                taskSearchModel = mTaskSearchModelMap.get(taskStateWrapper);
            }
            String querytextTrimmed = query.replaceAll("\\s+", "").toLowerCase();
            if (isTaskEligible(taskSearchModel, querytextTrimmed)) {
                filteredTasks.add(taskStateWrapper);
            }
        }
        return filteredTasks;
    }

    /**
     * It checks if the task contains the query as task or location name.
     *
     * @param taskSearchModel {@link TaskSearchModel} to be checked.
     * @param query           task or location name to be searched.
     * @return true if task contains query as task or location name, else false.
     */
    private boolean isTaskEligible(TaskSearchModel taskSearchModel, String query) {
        if (taskSearchModel.getTaskNameTrimmed().contains(query)) {
            return true;
        }
        String locationNameTrimmed = taskSearchModel.getLocationNameTrimmed();
        if (!Strings.isEmptyOrWhitespace(locationNameTrimmed) && locationNameTrimmed.contains(query)) {
            return true;
        }
        return false;
    }

    /**
     * It trimms the task and location name and adds it to mTaskSearchModelMap.
     *
     * @param taskStateWrapper {@link TaskStateWrapper} to be added to map.
     */
    private void addTaskSearchModel(TaskStateWrapper taskStateWrapper) {
        String taskName = taskStateWrapper.getTask().getTaskName();
        String taskNameTrimmed = taskName.replaceAll("\\s+", "").toLowerCase();
        String locationName = taskStateWrapper.getLocationName();
        String locationNameTrimmed = null;
        if (!Strings.isEmptyOrWhitespace(locationName)) {
            locationNameTrimmed = taskStateWrapper.getLocationName().replaceAll("\\s+", "").toLowerCase();
        }
        mTaskSearchModelMap.put(taskStateWrapper, new TaskSearchModel(taskNameTrimmed, locationNameTrimmed));
    }

    /**
     * It initializes all the views and class variables, sets recycler view and search view.
     */
    private void initialize() {
        toolbar = findViewById(R.id.toolbar);
        mTaskAdapter = new TaskAdapter(this);
        mTaskRepository = new TaskRepository(this);
        mTaskSearchModelMap = new HashMap<>();
        searchView = findViewById(R.id.search_view);
        setRecyclerView();
        initializeTaskSearchModelMap();
        setSearchView();
    }

    /**
     * Sets search view.
     */
    private void setSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getResources().getString(R.string.search_tasks_hint));
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
    }

    /**
     * Sets recycler view.
     */
    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_search_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mTaskAdapter);
    }

    /**
     * Initializes TaskSearchModelMap.
     */
    private void initializeTaskSearchModelMap() {
        List<TaskModel> taskModelList = mTaskRepository.getAllTasks();
        mTaskStateWrapperList = TaskStateUtil.getTasksStateListWrapper(this, taskModelList);
        addLocation(mTaskStateWrapperList);
    }

    /**
     * Adds task location to List<TaskStateWrapper>.
     *
     * @param stateWrappedTasks stateWrappedTasks containing tasks whose location needs to be added.
     */
    private void addLocation(List<TaskStateWrapper> stateWrappedTasks) {
        long locationId;
        for (TaskStateWrapper wrapper : stateWrappedTasks) {
            locationId = wrapper.getTask().getLocationId();
            wrapper.setLocationName(mTaskRepository.getLocationById(locationId).getPlaceName());
        }
    }

    /**
     * Sets activity's action bar.
     */
    private void setActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Sets no task view if no result is found with the entered search query.
     *
     * @param itemCount Number of tasks in search result.
     */
    private void setNoTasksView(int itemCount) {
        if (itemCount == 0) {
            findViewById(R.id.no_task_view).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.no_task_view).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initializeTaskSearchModelMap();
        String query = searchView.getQuery().toString();
        searchTasks(query);
    }
}
