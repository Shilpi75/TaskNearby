package app.tasknearby.yashcreations.com.tasknearby;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.tasknearby.yashcreations.com.tasknearby.models.TaskModel;
import app.tasknearby.yashcreations.com.tasknearby.utils.DbUpdatesSimulator;
import app.tasknearby.yashcreations.com.tasknearby.utils.TaskStateUtil;

/**
 * Fragment to display a list of tasks present in the database.
 *
 * @author vermayash8
 */
public class TasksFragment extends Fragment {

    private static final String TAG = TasksFragment.class.getSimpleName();

    private TaskRepository mTaskRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Set adapter to recycler view.
        TaskAdapter taskAdapter = new TaskAdapter(getActivity());
        recyclerView.setAdapter(taskAdapter);

        mTaskRepository = new TaskRepository(getActivity().getApplicationContext());
        // Fetch the live data object.
        LiveData<List<TaskModel>> liveData = mTaskRepository.getAllTasksWithUpdates();
        // observe the live data for changes.
        liveData.observe(this, taskModels -> {
            if (taskModels == null) {
                return;
            }


//            TODO:
//            When we get the liveData object, we can process it on MainTHread as well as by using
//            an AsyncTask. Choose either one on the basis of performance.
            // Use Async Task
            new TaskListProcessor(getActivity().getApplicationContext(), taskAdapter,
                    mTaskRepository).execute(taskModels);

            // Use MainThread.
/*
            List<TaskStateWrapper> stateWrappedTasks = TaskStateUtil.getTasksStateListWrapper(
                    getActivity(), taskModels);
            // Add location to the tasks.
            addLocation(stateWrappedTasks);
            // Add to adapter and notify.
            taskAdapter.setData(stateWrappedTasks);
            taskAdapter.notifyDataSetChanged();
            // Set the no task view.
            setNoTasksView(taskAdapter.getItemCount());
*/
        });

        // For demo.
        new DbUpdatesSimulator(getActivity().getApplicationContext(), mTaskRepository).start();

        return rootView;

    }

    /**
     * Assigns the locations to the list of TaskStateWrapper objects.
     */
    private void addLocation(List<TaskStateWrapper> stateWrappedTasks) {
        long locationId;
        for (TaskStateWrapper wrapper : stateWrappedTasks) {
            locationId = wrapper.getTask().getLocationId();
            wrapper.setLocationName(mTaskRepository.getLocationById(locationId).getPlaceName());
        }
    }

    /**
     * When no task is present, this shows the empty view.
     *
     * @param itemCount the number of tasks present in the database.
     */
    private void setNoTasksView(int itemCount) {
        if (itemCount == 0) {
            getActivity().findViewById(R.id.no_task_view).setVisibility(View.VISIBLE);
        } else {
            getActivity().findViewById(R.id.no_task_view).setVisibility(View.GONE);
        }
    }


    private static class TaskListProcessor extends AsyncTask<List<TaskModel>, Void,
            List<TaskStateWrapper>> {
        // TODO: After testing change this to WeakReference to not leak memory.
        private Context mContext;

        private TaskAdapter taskAdapter;
        private TaskRepository taskRepository;

        public TaskListProcessor(Context appContext, TaskAdapter taskAdapter, TaskRepository
                taskRepository) {
            mContext = appContext.getApplicationContext();
            this.taskAdapter = taskAdapter;
            this.taskRepository = taskRepository;
        }

        @Override
        protected List<TaskStateWrapper> doInBackground(List<TaskModel>[] lists) {
            List<TaskModel> taskModels = lists[0];
            List<TaskStateWrapper> stateWrappedTasks = TaskStateUtil.getTasksStateListWrapper(
                    mContext.getApplicationContext(), taskModels);
            // Add location to the tasks.
            addLocation(stateWrappedTasks);
            return stateWrappedTasks;
        }

        /**
         * Assigns the locations to the list of TaskStateWrapper objects.
         */
        private void addLocation(List<TaskStateWrapper> stateWrappedTasks) {
            long locationId;
            for (TaskStateWrapper wrapper : stateWrappedTasks) {
                locationId = wrapper.getTask().getLocationId();
                wrapper.setLocationName(taskRepository.getLocationById(locationId).getPlaceName());
            }
        }

        @Override
        protected void onPostExecute(List<TaskStateWrapper> taskStateWrappers) {
            super.onPostExecute(taskStateWrappers);
            // Add to adapter and notify.
            taskAdapter.setData(taskStateWrappers);
            taskAdapter.notifyDataSetChanged();
            // Set the no task view.
//            setNoTasksView(taskAdapter.getItemCount());
        }
    }
}
